import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatumBoxAPI {

	public static void main(String[] args) throws Exception {

		final String API_KEY = "acc832738d8917b6e2c1fac9e56e7f6c";

		String hostval = "pop.gmail.com";
		String mailStrProt = "pop3";
		String uname = "dcm0374@gmail.com";
		String passwd = "Chelsea22";
		String filePath = "C:\\tmp\\";
		String extention = ".txt";
		String charset = "utf-8";
		// Calling checkMail method to check received emails and writes to file
		BufferedReader br = new BufferedReader(new FileReader(
				DemoCheckEmail.checkMail(hostval, mailStrProt, uname, passwd, filePath, extention, charset)));

		String line = null;
		while ((line = br.readLine()) != null) {

			Map<String, Object> params = new LinkedHashMap<>();
			params.put("api_key", API_KEY);
			params.put("text", line);
			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), charset));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), charset));
			}
			byte[] postDataBytes = postData.toString().getBytes(charset);

			URL subjectivityURL = new URL("http://api.datumbox.com/1.0/SubjectivityAnalysis.json");

			HttpURLConnection subjectivityConn = (HttpURLConnection) subjectivityURL.openConnection();
			subjectivityConn.setRequestMethod("POST");
			subjectivityConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			subjectivityConn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			subjectivityConn.setDoOutput(true);
			subjectivityConn.getOutputStream().write(postDataBytes);

			String resultStr = "";
			
			StringBuilder subBuilder = new StringBuilder();
			Reader subjectIn = new BufferedReader(new InputStreamReader(subjectivityConn.getInputStream(), charset));
			for (int c; (c = subjectIn.read()) >= 0;) {
				subBuilder.append((char) c);
			}

			try {
				JSONObject jsonObj = new JSONObject(subBuilder.toString());
//				System.out.println(jsonObj.getJSONObject("output").get("result"));
				resultStr += jsonObj.getJSONObject("output").get("result").toString();
			} catch (JSONException e) {
				System.out.println("Error parsing data " + e.toString());
			}

			URL sentimentURL = new URL("http://api.datumbox.com:80/1.0/SentimentAnalysis.json");

			HttpURLConnection sentimentConn = (HttpURLConnection) sentimentURL.openConnection();
			sentimentConn.setRequestMethod("POST");
			sentimentConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			sentimentConn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			sentimentConn.setDoOutput(true);
			sentimentConn.getOutputStream().write(postDataBytes);

			StringBuilder sentBuilder = new StringBuilder();
			Reader sentimentIn = new BufferedReader(new InputStreamReader(sentimentConn.getInputStream(), charset));
			for (int c; (c = sentimentIn.read()) >= 0;) {
				sentBuilder.append((char) c);
			}

			try {
				JSONObject jsonObj = new JSONObject(sentBuilder.toString());
//				System.out.println(jsonObj.getJSONObject("output").get("result"));
				resultStr += ";" + jsonObj.getJSONObject("output").get("result").toString();
			} catch (JSONException e) {
				System.out.println("Error parsing data " + e.toString());
			}
			
			String message;
			JSONObject json = new JSONObject();
//			json.put("subjectivity", "subtest");

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

		br.close();

	}

}
