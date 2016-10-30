/*
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.servlet.http.Part;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


//Prerequirement for html page : we need assign tag id="result".

import java.util.List;

public class JsoupParser {
	String filePath;  //the name of html to open and modify
	Document doc; // // get the Document for the fileName, which is easy to access in any methods in this class.
    static String moduleName;
    static List<Part> programFiles;
    static List<Part> testFiles;
   	static List<Part> configureFiles;

    public JsoupParser() {
    }

	public JsoupParser(String filePath) throws IOException {
		 this.filePath = filePath;
		 // get the Document for the fileName
		 File input = new File(filePath);
		 Document doc = Jsoup.parse(input, "UTF-8");
		 this.doc = doc;

	}
	public void displayResult(String result) throws IOException {
		// Specify the Element form the document
		 Element res = doc.getElementById("result");
		 //System.out.println(res.text()); // for debug purpose
		 
		 // modify the text content of the Element
		 res.text(result);

		 System.out.println(res.text()); // for debug purpose
		 // write back to the same file

		 SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	 FileWriter fw = null;
					try {
						fw = new FileWriter(filePath);
						fw.write(doc.toString());
						 fw.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

			    }
			});
		 
		 /* simpiler method for writting file back.
		 // write back to the same file		 
    	 FileWriter fw = new FileWriter(filePath);
		 fw.write(doc.toString());
	     fw.close();
	}
		  */
	
	/*
	public static void setModuleName(String name) {
		moduleName = name;
	}

	public static void setProgramFiles(List<Part> fileParts) {
		programFiles = fileParts;
	}

	public static void setTestFiles(List<Part> fileParts) {
		testFiles = fileParts;
	}

	public static void setConfigFiles(List<Part> fileParts) {
		configureFiles = fileParts;

	}
	public static void sendDataToServer() {  // port:8001
		try {
			ClientSocket clientSocket = new ClientSocket(moduleName, "Input Form");
			clientSocket.run();


        } catch (Exception e) {
        	 System.err.println("Client Error: " + e.getMessage());
        }

	}
	*/
	/*

	public static void getTestJobFromServer(ArrayList<TestJob> lastestTestJobs) {
		
		Element table = doc.select("table");
		Elements rows = table.selct("tr").get(0);
		int count = 0; 
		for (TestJob testjob : latestTestJobs) {
		       rows.append("<tr> <td>" + count + "</td>" + 
		            "<td>" + testJob.testModuleName + "</td>" + 
		    	    "<td>" + testJob.timestamp + "</td>" + 
		            "<td>" + testJob.totalTests + "</td>" +
		            "<td>" + testJob.testsPassed + "</td>" +
		            "<td>" + testJob.testsFailed + "</td> </tr");
		}
	}
    */
/*	
	public static void getTestJobFromServer() {
		
		Element table = doc.select("table");
		Elements rows = table.selct("tr").get(0);
		int count = 0; 
		for (count = 0; count < 5 ; count ++) {
		       rows.append("<tr> <td>" + count + "</td>" + 
		            "<td>" + "test2 + "</td>" + 
		    	    "<td>" + "test2" + "</td>" + 
		            "<td>" + "test2"  + "</td>" +
		            "<td>" + "test2" + "</td>" +
		            "<td>" + "test2"  + "</td> </tr");
		}
	} 
	public static void main(String[] args) throws IOException {

        //JsoupParser jp = new JsoupParser();
        //jp.setModuleName("abcd");
        //jp.sendDataToServer();
        
  
	     JsoupParser jp2 = new JsoupParser("/Users/MyHome/Desktop/github/TestMate/src/test/main.html");
	     //TestJob testjob1;
	     jp2.getTestJobFromServer();
	     
	     //jp2.displayResult("test5 passed");



	}
	
   
}
*/