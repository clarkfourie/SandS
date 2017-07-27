import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatumBoxAPI {

	public static void main(String[] args) throws Exception {
		
		final String API_KEY = "acc832738d8917b6e2c1fac9e56e7f6c";
		String text = "Heil Hitler!";
		
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("api_key", API_KEY);
		params.put("text", text);
		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		byte[] postDataBytes = postData.toString().getBytes("UTF-8");

		URL subjectivityURL = new URL("http://api.datumbox.com/1.0/SubjectivityAnalysis.json");
		
		HttpURLConnection subjectivityConn = (HttpURLConnection) subjectivityURL.openConnection();
		subjectivityConn.setRequestMethod("POST");
		subjectivityConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		subjectivityConn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		subjectivityConn.setDoOutput(true);
		subjectivityConn.getOutputStream().write(postDataBytes);

		Reader subjectIn = new BufferedReader(new InputStreamReader(subjectivityConn.getInputStream(), "UTF-8"));

		for (int c; (c = subjectIn.read()) >= 0;)
			System.out.print((char) c);
		
		URL sentimentURL = new URL("http://api.datumbox.com:80/1.0/SentimentAnalysis.json");
		
		HttpURLConnection sentimentConn = (HttpURLConnection) sentimentURL.openConnection();
		sentimentConn.setRequestMethod("POST");
		sentimentConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		sentimentConn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		sentimentConn.setDoOutput(true);
		sentimentConn.getOutputStream().write(postDataBytes);

		Reader sentimentIn = new BufferedReader(new InputStreamReader(sentimentConn.getInputStream(), "UTF-8"));

		for (int c; (c = sentimentIn.read()) >= 0;)
			System.out.print((char) c);
	}

}
