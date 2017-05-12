package rendulic.igor.foodrecognizerexample;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by igor
 */

public class ImageUtil {

    /**
     * Crops the center of the image to desired wiidth, height
     * @param sourceBitmap source image from camera
     * @param targetWidth must be 544 for food recognition
     * @param targetHeight must be 544 for food recognition
     * @return cropped bitmap
     */
    public static Bitmap cropCenterImage(Bitmap sourceBitmap, int targetWidth, int targetHeight) {
        Bitmap outputBitmap = ThumbnailUtils.extractThumbnail(sourceBitmap, targetWidth, targetHeight);
        return outputBitmap;
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);


        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}
