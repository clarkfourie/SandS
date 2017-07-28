import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class test {
	
	public static void main(String[] args) throws JSONException {
		String message;
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		JSONObject item = new JSONObject();
		item.put("msgId", 1);
		item.put("msgContent", "content");
		item.put("subjectivity", "sentest");
		item.put("sentiment", "sentest");
		array.put(item);
		json.put("result", array);
		message = json.toString();
		System.out.println(message);
	}

}
