package rendulic.igor.foodrecognizerexample;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igor
 */

public class JSONUtil {


    public static List<Map<String,String>> getInitalListData() {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(createItem("Food Name", "Calories"));
        return list;
    }

    public static void foodJsonToList(JSONObject response, List<Map<String, String>> list) {

        list.clear();

        if (response != null) {
            JSONArray results = response.optJSONArray("results");
            for (int i=0; i<results.length(); i++) {
                JSONObject result = results.optJSONObject(i);
                JSONArray items = result.optJSONArray("items");
                for (int j=0; j<items.length(); j++) {
                    JSONObject item = items.optJSONObject(j);
                    list.add(createItem(item.optString("name"), ""));
                }
            }
        }
    }

    private static Map<String,String> createItem(String foodName, String calories) {
        Map<String,String>  item =  new HashMap<>();
        item.put("col_1", foodName);
        item.put("col_2", calories);
        return item;
    }

}
