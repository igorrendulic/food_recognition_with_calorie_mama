package rendulic.igor.foodrecognition;

import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by igor
 */

public class FoodRecognitionClient {

    private static final String ENDPOINT = "https://api.caloriemama.ai/food";

    private String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;

    public FoodRecognitionClient(FoodTask foodTask) throws IOException {
        // creates a unique boundary based on time stamp
        if (foodTask == null) {
            throw new IllegalArgumentException("Food task can't be null");
        }

        boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL(ENDPOINT);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);    // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("Authorization", "OAuth " + foodTask.getToken());
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"),
                true);

        addImage("file", foodTask.getImage());
    }

    private void addImage(String fieldName, Bitmap image) throws IOException {
        String fileName = "foodimage.jpg";
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.writeTo(outputStream);
        writer.append(LINE_FEED);
        writer.flush();
        outputStream.flush();
        out.flush();
        out.close();
    }

    public String send() throws FoodRecognitionException {
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        StringBuilder output = new StringBuilder();

        try {

            int status = httpConn.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new FoodRecognitionException("Error code: " + status);
            }
        } catch (IOException e) {
            throw new FoodRecognitionException(e.getMessage(), e);
        }
        return output.toString();
    }

}
