import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.servlet.http.Part;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsoupParser {
	String filePath;  //the name of html to open and modify
	Document doc; // // get the Document for the fileName, which is easy to access in any methods in this class.
    private static String moduleName;
    private static List<Part> programFiles;
    private static List<Part> testFiles;
   	private static List<Part> configureFiles;
	private static ClientSocket clientSocket = new ClientSocket();

	public JsoupParser() {
    }

	public JsoupParser(String filePath) throws IOException {
		 this.filePath = filePath;
		 // get the Document for the fileName
		 File input = new File(filePath);
		 Document doc = Jsoup.parse(input, "UTF-8");
		 this.doc = doc;
	}

//	public void getDataFromHtml() {
//
//	}

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

	public static void sendTestModuleToServer() {  // port:8001
		String module = "CREATE:{{ModuleName:" + moduleName + "},{Files:"; //File1,...(Executable file); Test File; Script file...>}}

		for (int i = 0; i < programFiles.size(); i++) {
			module += programFiles.get(i);

			if (i != programFiles.size() - 1) {
				module += ",";
			}
		}
		module += ";";

		for (int i = 0; i < testFiles.size(); i++) {
			module += testFiles.get(i);

			if (i != testFiles.size() - 1) {
				module += ",";
			}
		}
		module += ";";

		for (int i = 0; i < configureFiles.size(); i++) {
			module += configureFiles.get(i);

			if (i != configureFiles.size() - 1) {
				module += ",";
			}
		}
		module += ";";

		try {
			clientSocket.run(module, "Input Form");

        } catch (Exception e) {
        	 System.err.println("Client Error: " + e.getMessage());
        }

	}

	public static String sendGetRequestToServer(String moduleName) {
		String getRequest = "SEND:" + moduleName;
		try {
			String message = clientSocket.run("Hi Server! I am the Display form", "");
			if (message.equalsIgnoreCase("Hi DisplayForm. What do you need")) {
				message = clientSocket.run(getRequest, "Display Form");

				return message;  //TODO: Change this if necessary
			}
		} catch (Exception e) {
			System.err.println("Client Error: " + e.getMessage());
		}

		return "";
	}

	public static void runModule(String moduleName) {
		String runRequest = "RUN:" + moduleName;
		try {
			String message = clientSocket.run("Hi Server! I am the Display form", "");
			if (message.equalsIgnoreCase("Hi DisplayForm. What do you need")) {
				message = clientSocket.run(runRequest, "Display Form");

				//return message;  TODO: Change this if necessary
			}
		} catch (Exception e) {
			System.err.println("Client Error: " + e.getMessage());
		}
	}

    
	public static void main(String[] args) throws IOException {

        JsoupParser jp = new JsoupParser();
        jp.setModuleName("abcd");
        jp.sendDataToServer();
        /*jp.displayResult("test4 passed");

	     JsoupParser jp2 = new JsoupParser("C:\\Users\\User\\eclipse\\JsoupParser\\src\\test.html");
	     jp2.displayResult("test5 passed");*/


	}

}