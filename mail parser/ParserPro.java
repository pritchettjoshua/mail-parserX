//library imports
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
/**
 * 
 * @author Joshua Pritchett
 *
 */
public class ParserPro {
	public static int numberOfFiles;//counter variable for split files created
	public static String mboxFileName;//mbox file name storage
	public static String mboxFilePath;//mbox file path storage
	public static String tuplesOutputPath;//tuples output file path storage
	public static String fromActivityOutputPath;//from activity output file path storage
	public static String doNotInclude;//stores a email address not to be parsed
	/**
	 * 
	 * @param args
	 * @throws Exception
	 * This method is the main method--the main method calls all of the sub methods in
	 * order of which they appear.
	 */
	public static void main(String args[]) throws Exception {
		//Below are GUIs to accept user input
		mboxFileName = JOptionPane.showInputDialog("Please input name of Mbox File, i.e FullDisc or FunSec");
		mboxFilePath = JOptionPane.showInputDialog("Please input location path to Mbox file"); 
		tuplesOutputPath   = JOptionPane.showInputDialog("Please input path for the tuples file output --ie C:/Users/josh/Documents/tuples");
		fromActivityOutputPath   = JOptionPane.showInputDialog("Please input path for the from activity file output --ie C:/Users/josh/Documents/from");
		doNotInclude  = JOptionPane.showInputDialog("Please input an email, if any, that you do not want parsed--such as a mailing list");
		if(doNotInclude.equals(""))doNotInclude="&&%$#@@@###$%%^&&*%^%#%%";//replaces empty string with an unlikely sequence of chars to prevent a comparison match
		fileSplitter();//calls filesplitter method
		parser();//calls parser method
		tuplesGenerator();//calls tuplesGenerator method
		fromActivity();//calls fromActivity method
		deleteFiles();//calls delete file method

	}
/**
 * 
 * @throws IOException
 * fileSplitter takes an mbox file and splits it into individual files 
 * for each email in the mbox file
 */
	public static void fileSplitter() throws IOException {
		int count = 0;//initializes count to zero
		FileInputStream fs = new FileInputStream(mboxFilePath);//creates FileInputStream object
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));//creates BufferedReader object using the FileInputStream
		String line = "";//initializes string to empty string
		FileOutputStream fos = new FileOutputStream("fileSplit"+ count);//creates FileOutputStrean object
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));//creates BufferedWriter object using the FileOutputStream

		while ((line = br.readLine()) != null) { //this is a loop that splits the file into separate files where "From: " appears
			String mine = line.trim();

			if (mine.startsWith("From - ")) {
				// while ((line = br.readLine())!=null &&
				// !line.startsWith("From - ")) {
				// line = br.readLine();
				// }
				bw.close();//closes buffered writer
				count++;//increments count variable
				fos = new FileOutputStream("fileSplit"+ count);//creates new FileOutputStream object
				bw = new BufferedWriter(new OutputStreamWriter(fos));//creates new BufferedWriter object
			} else {
				bw.write(line); //This writes the line onto the file with the given path--"fileSplit"+ count
				bw.newLine();  //this creates a newline onto the file with the given path--"fileSplit"+ count
			}
		}
		numberOfFiles = count; //sets the numberOfFiles equal to count
		bw.close();//closes the BufferedWriter
		br.close();//closes the BufferedReader
	}
/**
 * 
 * @throws MessagingException
 * @throws IOException
 * the parser method is responsible for scraping the To, From, Date, and Cc, and Subject.
 * the output is saved on an output file.
 */
	public static void parser() throws MessagingException, IOException {
		int count = 0;//initializes the count variable to zero
		FileOutputStream fos = new FileOutputStream("output");//creates new FileOutputStream object
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));//creates new BufferedWriter object

		while (count <= numberOfFiles - 1) {//this loop runs until all emails are parsed--numberOfFiles stores that number
			// Create empty properties
			Properties props = new Properties();//creates new properties object

			// Get session
			Session session = Session.getInstance(props);//creates new session object using an instance of properties.

			MimeMessage msg;//declares new Minemessage called msg
			File file = new File("fileSplit"+ count);//creates new file for all split files--which is each individual email
			msg = new MimeMessage(session,
					new SharedFileInputStream(
							file));//creates new message object using a session and SharedFileInputStream object.

			javax.mail.Address[] a; //declares new javax.mail.Address array name a
			bw.write("" + (count + 1) + "\n");//this is a counter that represents the amount of emails parsed
			//below grabs the From field from message object and prints to outputfile
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
				//below grabs all recipients --To and Cc-- from the message object and prints to outputfile
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
				bw.write("Subject: " + msg.getSubject() + "\n");//grabs and prints the subject field from the message object to outputfile

				bw.write("Date: " + msg.getSentDate() + "\n");//grabs and prints the date field from the message object to outputfile
				bw.write("#*#* \n");// prints what represent end of message marker
				bw.write("\n");//prints a new line to outputfile
				
			} catch (javax.mail.internet.AddressException e) { //catches the illegal address exceptions
			} finally {
				count++;//increments counter in order to parse the next file
			}

		}
		bw.flush();//flushes the memory to clear the bufferedwriter
		bw.close();//closes the bufferedwriter to prevent memory leaks

	}
	/**
	 * 
	 * @throws IOException
	 * this method generates the tuples and prints the output file
	 */
	public static void tuplesGenerator() throws IOException{
		FileInputStream fs = new FileInputStream("output");//creates new FileInputStream object
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));//creates new BufferedReader object using the FileInputStream object
		String line = "";//initializes line to empty string
		FileOutputStream fos = new FileOutputStream(tuplesOutputPath);//creates new FileOutputStream object
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));//creates new BufferedWriter object using the FileOutputStream object
		String from="";//initializes from to empty string
		String subject="";//initializes subject to empty string
		ArrayList<String> to = new ArrayList<String>();//creates new arraylist of type string
		int count=0;//initializes count to zero
		

		while ((line = br.readLine()) != null) {//until all of the file is read this loop runs
			
			while(line != null&& !line.startsWith("#*#*")){//as long as the line being read isn't null or the end of message marker"#*#*" this loop runs
				
				if(line.startsWith("From: ")){//stores the from
				from = line.substring(6);	
				}
				if(line.startsWith("TO: ")&&!line.contains(doNotInclude)){//stores the To field into the to--arraylist
					to.add(line.substring(4));
					count++;
				}
				if(line.startsWith("Subject: ")){//stores the subject
					subject = line.substring(8);
				}
				line = br.readLine();//reads the next line
				
			}
			if(line.startsWith("#*#*")&&to.size()!=0){//if the marker is reached print the tuple
				for(int index=0; index < count; index++){
					bw.write(mboxFileName + ", " + from + ", "+to.get(index) + ", " + subject+ "\n");
				}
				count=0;//reset the counter
				to.clear();//clear the arraylist
				line=br.readLine();//read the next line
			}
		
	}
		br.close();//close the bufferedreader
		bw.flush();//flushes the bufferedwriter
		bw.close();//close the bufferedwriter
	}
	/**
	 * 
	 * @throws IOException
	 * This method is responsible for counting the amount of messages that each user sends.
	 * This method writes the results to an output file specified by the client.
	 */
	public static void fromActivity() throws IOException{
		FileInputStream fs = new FileInputStream("output");//creates new FileInputStream object
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));//creates new BufferedReader object using the FileInputStream
		String line = "";//initializes line to empty string
		FileOutputStream fos = new FileOutputStream(fromActivityOutputPath);//creates new FileOutputStream object
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));//creates new BufferedWriter using the FileOutputStream object
		ArrayList<String> marked = new ArrayList<String>();//creates new Arraylist called marked to represent users counted previously
		int count = 0; //initializes the count variable to zero
		while ((line = br.readLine()) != null) {//runs until end of file--output file that is
			//stores unread From: fields into an arraylist--marked
			if(line.startsWith("From: ")){
				if(!marked.contains(line.substring(6))){
					marked.add(line.substring(6));
					FileInputStream fs2 = new FileInputStream("output");//creates new FileInputStream object
					BufferedReader br2 = new BufferedReader(new InputStreamReader(fs2));//creates new Bufferedreader object using the FileInputStream
					String line2 ="";//initializes line2 to empty string
					//this loop runs until the end of file is reached--it compares the lines and if the from: fields equal the counter increments
					while((line2=br2.readLine())!=null){
						if(line.equals(line2))count++;
						
					}
					br2.close();//closes the buffered reader
					bw.write(line.substring(6)+", " + count+ "\n");//print output of the from activity
					count=0;//resets the count to zero
				}
			}
		}
		br.close();//closes the bufferedreader
		bw.flush();//flushes the bufferedwriter
		bw.close();//closes the bufferedwriter
	}
	/**
	 * 
	 * @throws IOException
	 * This method deletes most, if not all, of the splitfiles created 
	 * from the original mbox file
	 */
	 public static void deleteFiles() throws IOException {
		 int count = 0;//initializes the count variable to zero
		 while(count < numberOfFiles){//runs until the count equals the number of split files
		 File file = new File("fileSplit"+ count);//creates new file
		 file.delete();//deletes the file
		 count++;//increments the counter
		 }
	 }
}
