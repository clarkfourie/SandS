import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;

public class DemoCheckEmail {

	public static void main(String[] args) {
		// Set mail properties and configure accordingly
		String hostval = "pop.gmail.com";
		String mailStrProt = "pop3";
		String uname = "dcm0374@gmail.com";
		String passwd = "Chelsea22";
		String filePath = "C:\\tmp\\";
		String extention = ".txt";
		String charset = "utf-8";
		// Calling checkMail method to check received emails
		checkMail(hostval, mailStrProt, uname, passwd, filePath, extention, charset);
	}

	public static void checkMail(String hostval, String mailStrProt, String uname, String passwd, String filePath,
			String extention, String charset) {
		try {
			System.out.println("start");
			// Set property values
			Properties propvals = new Properties();
			propvals.put("mail.pop3.host", hostval);
			propvals.put("mail.pop3.port", "995");
			propvals.put("mail.pop3.starttls.enable", "true");
			Session emailSessionObj = Session.getDefaultInstance(propvals);
			// Create POP3 store object and connect with the server
			Store storeObj = emailSessionObj.getStore("pop3s");
			storeObj.connect(hostval, uname, passwd);
			// Create folder object and open it in read-only mode
			Folder emailFolderObj = storeObj.getFolder("INBOX");
			emailFolderObj.open(Folder.READ_ONLY);
			// Fetch messages from the folder and print in a loop
			Message[] messageobjs = emailFolderObj.getMessages();
			// Create file to store email content
			String date = new SimpleDateFormat("ddMMyy").format(new Date());
			File emailStore = new File(filePath + "emailStore_" + date + extention);

			try (Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(emailStore.getAbsoluteFile()), charset))) {

				String result = "";
				for (int i = 0, n = messageobjs.length; i < n; i++) {
					Message indvidualmsg = messageobjs[i];
					System.out.println("Printing individual messages");
					System.out.println("No# " + (i + 1));
					System.out.println("Email Subject: " + indvidualmsg.getSubject());
					System.out.println("Sender: " + indvidualmsg.getFrom()[0]);
					System.out.println("Content: " + indvidualmsg.getContent().toString());

					// Take action depending on mimeType
					if (indvidualmsg.isMimeType("text/plain")) {
						result = indvidualmsg.getContent().toString();
					} else if (indvidualmsg.isMimeType("multipart/*")) {
						MimeMultipart mimeMultipart = (MimeMultipart) indvidualmsg.getContent();
						System.out.println("multipart");
						for (int k = 0; k < mimeMultipart.getCount(); k++) {
							BodyPart bodyPart = mimeMultipart.getBodyPart(k);
							if (bodyPart.isMimeType("text/plain")) {
								result = result + "\n" + bodyPart.getContent();
								break; // without break same text appears twice in my tests
							} else if (bodyPart.isMimeType("text/html")) {
								String html = (String) bodyPart.getContent();
								result = result + "\n" + Jsoup.parse(html).text();
							}
						}
					}

					// Write email content to file
					writer.append("### mail " + String.valueOf(i + 1));
					writer.append(System.lineSeparator());
					writer.append(result);
					writer.append(System.lineSeparator());

				}
				writer.close();
			}
			// Close all the objects
			emailFolderObj.close(false);
			storeObj.close();
			System.out.println("END");

			// Remove unwanted text
			File tmp = File.createTempFile("emailStore_" + date, extention, emailStore.getParentFile());
			String removeStr1 = "See you at #SageSummit Tour 2017. Learn More<https://www.sage.com/sage-summit/>";
			String removeStr2 = "Sage Summit goes local. Business Builders, Build On!";
			String removeStr3 = "The information contained in this email transmission may constitute confidential information. If you are not the intended recipient, please take notice that reuse of the information is prohibited.";
			String removeStr4 = "Paris | Madrid | Melbourne | Berlin | Johannesburg | Singapore | London | Atlanta | Toronto";
			String removeStr5 = "Regards,";

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(emailStore), charset));
			Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(tmp.getAbsoluteFile()), charset));

			for (String line; (line = reader.readLine()) != null;) {
				line = line.replace(removeStr1, "");
				line = line.replace(removeStr2, "");
				line = line.replace(removeStr3, "");
				line = line.replace(removeStr4, "");
				line = line.replace(removeStr5, "");
				writer.append(line);
				writer.append(System.lineSeparator());
			}

			reader.close();
			writer.close();
			emailStore.delete();
			tmp.renameTo(emailStore);

		} catch (NoSuchProviderException exp) {
			exp.printStackTrace();
		} catch (MessagingException exp) {
			exp.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
}