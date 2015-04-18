import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.util.SharedFileInputStream;

public class TestStream {
	public static int numberOfFiles;

	public static void main(String args[]) throws Exception {
		fileSplitter();
		
		 int count=0;
		 while(count <= numberOfFiles){
			 // Create empty properties
			 Properties props = new Properties();
			
			 // Get session
			 Session session = Session.getInstance(props);
		
			 MimeMessage msg; msg = new MimeMessage(session, new SharedFileInputStream(
		 "C:/Users/dannystormball/Documents/cs495/fileSplit" + count));
		
		 javax.mail.Address[] a;
		
		 if ((a = msg.getFrom()) != null) {
		 for (int j1 = 0; j1 < a.length; j1++)
		 System.out.println("From: " + a[j1].toString());
		 }
		
		 if ((a = msg.getAllRecipients()) != null) {
		 for (int j1 = 0; j1 < a.length; j1++)
		 System.out.println("TO: " + a[j1].toString());
		 }
		 System.out.println("Subject: " + msg.getSubject());
		
		 System.out.println("Date: " + msg.getSentDate());
		 System.out.println();
		 System.out.println();
		 count++;
		 }
	}

	public static void fileSplitter() throws IOException {
		int count = 0;
		FileInputStream fs = new FileInputStream(
				"C:/Users/dannystormball/Documents/cs495/FunSec");
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String line = "";
		FileOutputStream fos = new FileOutputStream(
				"C:/Users/dannystormball/Documents/cs495/fileSplit" + count);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		while ((line = br.readLine()) != null) {
			String mine = line.trim();

			if (mine.startsWith("Note:")) {
				while ((line = br.readLine())!=null && !line.startsWith("From - ")) {
					line = br.readLine();
					}
				bw.close();
				count++;
				fos = new FileOutputStream(
						"C:/Users/dannystormball/Documents/cs495/fileSplit"
								+ count);
				bw = new BufferedWriter(new OutputStreamWriter(fos));
			} else {
				bw.write(line);
				bw.newLine();
			}
		}
			numberOfFiles = count;
	}
}
