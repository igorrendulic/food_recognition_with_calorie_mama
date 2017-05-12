package rendulic.igor.foodrecognizerexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import rendulic.igor.foodrecognition.FoodRecognitionException;
import rendulic.igor.foodrecognition.FoodRecognitionTask;
import rendulic.igor.foodrecognition.FoodServiceCallback;
import rendulic.igor.foodrecognition.FoodTask;

public class MainActivity extends AppCompatActivity {

    private Button takePicture;
    private ImageView imageView;
    private Uri imageFile;
    private ListView foodListView;
    String mCurrentPhotoPath;
    List<Map<String,String>> mFoodData;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    SimpleAdapter simpleAdapter;

    private static final String MY_TOKEN = "YOUR TOKEN HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePicture = (Button) findViewById(R.id.button_image);
        imageView = (ImageView) findViewById(R.id.imageview);
        foodListView = (ListView) findViewById(R.id.foodListView);


        takePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePicture(v);
            }
        });

        mFoodData = JSONUtil.getInitalListData();

        // create the grid item mapping
        String[] from = new String[] {"col_1", "col_2" };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2};

        ListView listView = (ListView) findViewById(R.id.foodListView);
        simpleAdapter = new SimpleAdapter(this,mFoodData,android.R.layout.simple_list_item_1,from,to);

        listView.setAdapter(simpleAdapter);
    }

    // using intent to take a picture with existing camera app on the phone
    public void takePicture(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("FoodRecognitionExample", ex.getMessage(), ex);
                // TODO: return and toast
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "rendulic.igor.foodrecognitionexample.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageStagte = Environment.getExternalStorageState();
        if (externalStorageStagte.equals(Environment.MEDIA_MOUNTED)) {
            File photoDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(photoDir, getString(R.string.app_name));
            if (!outputDir.exists())
                if (!outputDir.mkdirs()) {
                    Toast.makeText(
                            this,
                            "Failed to create directory "
                                    + outputDir.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                    outputDir = null;
                }
        }
        return outputDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap original = setPic(); // set image to the ImageView

            Bitmap cropped = ImageUtil.cropCenterImage(original, 544, 544); // crop center image and resize to 544x544
            FoodTask foodTask = new FoodTask(MY_TOKEN, cropped);

            final ProgressDialog progressDialog = ProgressDialog.show(this,"Please wait...", "Recognizing food");
            progressDialog.setCancelable(true);

            new FoodRecognitionTask(new FoodServiceCallback<JSONObject>() {

                @Override
                public void finishRecognition(JSONObject response, FoodRecognitionException exception) {

                    progressDialog.dismiss();

                    if (exception != null) {
                        // handle exception gracefully
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        JSONUtil.foodJsonToList(response, mFoodData);
                        simpleAdapter.notifyDataSetChanged();
                    }

                }
            }).execute(foodTask);

        }

    }

    private Bitmap setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        try {
            bitmap = ImageUtil.rotateImageIfRequired(bitmap, Uri.parse(mCurrentPhotoPath));
        } catch (IOException e) {
            Log.e("FoodRecognitionExample", e.getMessage(),e);
        }

        imageView.setImageBitmap(bitmap);

        return bitmap;
    }
}
