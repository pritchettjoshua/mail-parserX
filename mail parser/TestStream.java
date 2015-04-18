import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.util.SharedFileInputStream;
import javax.swing.JOptionPane;

public class TestStream {
	public static int numberOfFiles;
	public static String mboxFileName;
	public static String mboxFilePath;
	public static String outputPath;
	

	public static void main(String args[]) throws Exception {
		mboxFileName = JOptionPane.showInputDialog("Please input name of Mbox File, i.e FullDisc or FunSec");
		mboxFilePath = JOptionPane.showInputDialog("Please input location path to Mbox file"); 
		outputPath   = JOptionPane.showInputDialog("Please input location path for the parser output files--ie C:/Users/josh/Documents/"); 
		
		fileSplitter();
		parser();
		tuplesGenerator();
		fromActivity();
		deleteFiles();

	}

	public static void fileSplitter() throws IOException {
		int count = 0;
		FileInputStream fs = new FileInputStream(mboxFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String line = "";
		FileOutputStream fos = new FileOutputStream(outputPath + "fileSplit"+ count);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		while ((line = br.readLine()) != null) {
			String mine = line.trim();

			if (mine.startsWith("From - ")) {
				// while ((line = br.readLine())!=null &&
				// !line.startsWith("From - ")) {
				// line = br.readLine();
				// }
				bw.close();
				count++;
				fos = new FileOutputStream(outputPath+"fileSplit"+ count);
				bw = new BufferedWriter(new OutputStreamWriter(fos));
			} else {
				bw.write(line);
				bw.newLine();
			}
		}
		numberOfFiles = count;
		bw.close();
		br.close();
	}

	public static void parser() throws MessagingException, IOException {
		int count = 0;
		FileOutputStream fos = new FileOutputStream(outputPath +"output");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		while (count <= numberOfFiles - 1) {
			// Create empty properties
			Properties props = new Properties();

			// Get session
			Session session = Session.getInstance(props);

			MimeMessage msg;
			File file = new File(outputPath+"fileSplit"+ count);
			msg = new MimeMessage(session,
					new SharedFileInputStream(
							file));

			javax.mail.Address[] a;
			bw.write("" + (count + 1) + "\n");

			try {
				if ((a = msg.getFrom()) != null) {
					for (int j1 = 0; j1 < a.length; j1++){
						String temp = a[j1].toString();
						try{
						if(temp.contains("<")&& temp.contains(">")){
							int charInt1=temp.indexOf("<");
							int charInt2=temp.indexOf(">");
							temp= temp.substring(charInt1+1, charInt2);
							temp= "From: " +temp;
							bw.write(temp + "\n");
						}
						else	bw.write("From: " +a[j1].toString() + "\n");
						}
						catch(java.lang.StringIndexOutOfBoundsException e){}
						//bw.write("From: " +a[j1].toString() + "\n");
					}
				}

				if ((a = msg.getAllRecipients()) != null) {
					for (int j1 = 0; j1 < a.length; j1++){
						String temp = a[j1].toString();
						try{
						if(temp.contains("<")&& temp.contains(">")){
							int charInt1=temp.indexOf("<");
							int charInt2=temp.indexOf(">");
							temp= temp.substring(charInt1+1, charInt2);
							temp= "TO: " +temp;
							bw.write(temp + "\n");
						}
						else	bw.write("TO: " + a[j1].toString() + "\n");
						}
						catch(java.lang.StringIndexOutOfBoundsException e){}
						//bw.write("TO: " + a[j1].toString() + "\n");
					}
						
				}
				bw.write("Subject: " + msg.getSubject() + "\n");

				bw.write("Date: " + msg.getSentDate() + "\n");
				bw.write("#*#* \n");
				bw.write("\n");
				
			} catch (javax.mail.internet.AddressException e) {
			} finally {
				count++;
			}

		}
		bw.flush();
		bw.close();

	}
	public static void tuplesGenerator() throws IOException{
		FileInputStream fs = new FileInputStream(outputPath +"output");
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String line = "";
		FileOutputStream fos = new FileOutputStream(outputPath +"tuples");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		String from="";
		String subject="";
		ArrayList<String> to = new ArrayList<String>();
		int count=0;
		

		while ((line = br.readLine()) != null) {
			
			while(line != null&& !line.startsWith("#*#*")){
				
				if(line.startsWith("From: ")){
				from = line.substring(6);	
				}
				if(line.startsWith("TO: ")){
					to.add(line.substring(4));
					count++;
				}
				if(line.startsWith("Subject: ")){
					subject = line.substring(8);
				}
				line = br.readLine();
				
			}
			if(line.startsWith("#*#*")){
				for(int index=0; index < count; index++){
					bw.write(mboxFileName + ", " + from + ", "+to.get(index) + ", " + subject+ "\n");
				}
				count=0;
				to.clear();
				line=br.readLine();
			}
		
	}
		br.close();
		bw.flush();
		bw.close();
	}
	public static void fromActivity() throws IOException{
		FileInputStream fs = new FileInputStream(outputPath +"output");
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String line = "";
		FileOutputStream fos = new FileOutputStream(outputPath +"fromActivity");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		ArrayList<String> marked = new ArrayList<String>();
		int count = 0;
		while ((line = br.readLine()) != null) {
			if(line.startsWith("From: ")){
				if(!marked.contains(line.substring(6))){
					marked.add(line.substring(6));
					FileInputStream fs2 = new FileInputStream(outputPath +"output");
					BufferedReader br2 = new BufferedReader(new InputStreamReader(fs2));
					String line2 ="";
					while((line2=br2.readLine())!=null){
						if(line.equals(line2))count++;
						
					}
					br2.close();
					bw.write(line.substring(6)+", " + count+ "\n");
					count=0;
				}
			}
		}
		br.close();
		bw.flush();
		bw.close();
	}
	 public static void deleteFiles() throws IOException {
		 int count = 0;
		 while(count < numberOfFiles){
		 File file = new File(outputPath+"fileSplit"+ count);
		 file.delete();
		 count++;
		 }
	 }
}
