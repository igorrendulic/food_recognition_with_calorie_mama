package rendulic.igor.foodrecognition;

/**
 * Created by igor
 */

public class FoodRecognitionException extends Exception {
    public FoodRecognitionException(String msg) {
        super(msg);
    }

    public FoodRecognitionException(Throwable cause) {
        super(cause);
    }

    public FoodRecognitionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
