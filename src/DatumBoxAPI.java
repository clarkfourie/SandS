import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
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
		// DatumBox API key
		final String API_KEY = "acc832738d8917b6e2c1fac9e56e7f6c";

		// checkMail parameters
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

		// JSON result set
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		int msgId = 0;

		String line = null;
		while ((line = br.readLine()) != null) {
			JSONObject item = new JSONObject();

			// Create body of POST
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

			// POST to DatumBox subjectivity analysis API
			URL subjectivityURL = new URL("http://api.datumbox.com/1.0/SubjectivityAnalysis.json");
			HttpURLConnection subjectivityConn = (HttpURLConnection) subjectivityURL.openConnection();
			subjectivityConn.setRequestMethod("POST");
			subjectivityConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			subjectivityConn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			subjectivityConn.setDoOutput(true);
			subjectivityConn.getOutputStream().write(postDataBytes);

			// Build a string in JSON format from DatumBox subjectivity analysis API
			StringBuilder subBuilder = new StringBuilder();
			Reader subjectIn = new BufferedReader(new InputStreamReader(subjectivityConn.getInputStream(), charset));
			for (int c; (c = subjectIn.read()) >= 0;) {
				subBuilder.append((char) c);
			}

			// Create JSON object of subjectivity analysis string to return subjectivity
			// result
			try {
				JSONObject jsonObj = new JSONObject(subBuilder.toString());
				// System.out.println(jsonObj.getJSONObject("output").get("result"));
				item.put("subjectivity", jsonObj.getJSONObject("output").get("result").toString());
			} catch (JSONException e) {
				System.out.println("Error parsing data " + e.toString());
			}

			// Build a string in JSON format from DatumBox sentiment analysis API
			URL sentimentURL = new URL("http://api.datumbox.com:80/1.0/SentimentAnalysis.json");
			HttpURLConnection sentimentConn = (HttpURLConnection) sentimentURL.openConnection();
			sentimentConn.setRequestMethod("POST");
			sentimentConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			sentimentConn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			sentimentConn.setDoOutput(true);
			sentimentConn.getOutputStream().write(postDataBytes);

			// Build a string in JSON format from DatumBox sentiment analysis API
			StringBuilder sentBuilder = new StringBuilder();
			Reader sentimentIn = new BufferedReader(new InputStreamReader(sentimentConn.getInputStream(), charset));
			for (int c; (c = sentimentIn.read()) >= 0;) {
				sentBuilder.append((char) c);
			}

			// Create JSON object of sentiment analysis string to return sentiment result
			try {
				JSONObject jsonObj = new JSONObject(sentBuilder.toString());
				item.put("sentiment", jsonObj.getJSONObject("output").get("result").toString());
			} catch (JSONException e) {
				System.out.println("Error parsing data " + e.toString());
			}

			// Finalizing JSON result object
			msgId++;
			item.put("msgId", msgId);
			item.put("msgContent", line.toString());
			array.put(item);

			json.put("result", array);

		}

		// Write JSON result to text file
		try (FileWriter jsonfile = new FileWriter(filePath + "json.json")) {
			jsonfile.write(json.toString(4));
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + json);
		}

		br.close();

	}

}
