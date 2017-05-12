package rendulic.igor.foodrecognition;

import org.json.JSONObject;

/**
 * Created by igor
 */

public interface FoodServiceCallback<T> {

    /**
     * Indicates that the upload operation has finished. This method is called even if the
     * upload hasn't completed successfully.
     */
    void finishRecognition(JSONObject response, FoodRecognitionException exception);
}
