package rendulic.igor.foodrecognition;

import android.graphics.Bitmap;

/**
 * Simple Task object for FoodRecognitionTask
 *
 * Created by igor
 */

public class FoodTask {

    private Bitmap image;
    private String token;

    public FoodTask(String token, Bitmap image) throws IllegalArgumentException {
        if (token == null || image == null) {
            throw new IllegalArgumentException("Both parameters required");
        }
        this.token = token;
        this.image = image;
    }

    public String getToken() {
        return this.token;
    }

    public Bitmap getImage() {
        return this.image;
    }
}
