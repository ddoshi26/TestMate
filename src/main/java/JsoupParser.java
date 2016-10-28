import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.http.Part;

import javax.swing.SwingUtilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JsoupParser {
	String filePath;  //the name of html to open and modify
	Document doc; // // get the Document for the fileName, which is easy to access in any methods in this class.
    static String moduleName;
    static List<Part> programFiles;
    static List<Part> testFiles;
   	static List<Part> configureFiles;


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
		 System.out.println(res.text()); // for debug purpose
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
	}
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
	public static void sendData() {  // port:8001
		try {
			Socket clientSocket = new Socket("http://localhost", 8001);
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer.writeObject(moduleName);
            outToServer.writeObject(programFiles);
            outToServer.writeObject(testFiles);
            outToServer.writeObject(configureFiles);
            clientSocket.close();

        } catch (Exception e) {
        	 System.err.println("Client Error: " + e.getMessage());
        }

	}
    
	public static void main(String[] args) throws IOException {

	     JsoupParser jp = new JsoupParser("C:\\Users\\User\\eclipse\\JsoupParser\\src\\FrontEnd.html");
	     jp.displayResult("test4 passed");

	     JsoupParser jp2 = new JsoupParser("C:\\Users\\User\\eclipse\\JsoupParser\\src\\test.html");
	     jp2.displayResult("test5 passed");

	}

}