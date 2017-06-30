package rendulic.igor.foodrecognition;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Async Task
 *
 * Created by igor
 */
public class FoodRecognitionTask extends AsyncTask<FoodTask,Void,String> {

    private FoodServiceCallback<JSONObject> delegate = null;
    private FoodRecognitionException exception = null;

    public FoodRecognitionTask(FoodServiceCallback<JSONObject> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute(){}

    @Override
    protected String doInBackground(FoodTask... params) {
        try {
            FoodRecognitionClient foodClient = new FoodRecognitionClient(params[0]);
            String response = foodClient.send();
            return response;
        } catch (IOException e) {
            Log.e("FoodRecognitionExample", e.getMessage(), e);
            this.exception = new FoodRecognitionException(e.getMessage());
        } catch (FoodRecognitionException e) {
            Log.e("FoodRecognitionExample", e.getMessage(), e);
            this.exception = e;
        }
        return null;
    }


    @Override
    protected void onPostExecute(String result){

        JSONObject json = null;
        try {
            if (result != null && result.startsWith("{")) {
                json = new JSONObject(result);
                if (json.has("error")) {
                    JSONObject error = json.optJSONObject("error");
                    String errorDetail = error.optString("errorDetail");
                    int code = error.optInt("code");
                    this.exception = new FoodRecognitionException("Code: " + code + ", error: " + errorDetail);
                }
            } else {
                this.exception = new FoodRecognitionException("Returned document not a valid JSON: " + result);
            }
        } catch (JSONException e) {
            Log.e("FoodRecognitionExample", e.getMessage(), e);
            this.exception = new FoodRecognitionException(e.getMessage());
        }
        delegate.finishRecognition(json, this.exception);
    }
}
