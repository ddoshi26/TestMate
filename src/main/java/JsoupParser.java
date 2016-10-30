import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jscoup.JsoupParser;

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

    public void getTestJobFromServer(String moduleListStr) {
    	ArrayList<String []> moduleList = new ArrayList<String []>();
    	//String module[] = new String[5]; // future bug insertion position
        String module[];
        // 1) parse  the string input
		// get rid of { in the beginning and } in the end;
		String subStr = moduleListStr.substring(1, moduleListStr.length() - 1);
		//split the substring into tokens
		String modules[] = subStr.split(";");
		// for each module, get rid of { and }.
		System.out.println(modules.length);
		for (int i = 0; i < modules.length; i++) {
			String subMods = modules[i].substring(1, (modules[i].length() -1)); // possible inserting bug position :  modules[0]
			System.out.println(subMods);
		    // get the value of  tokens
		 	module = new String[5];
		    String tokens[] = subMods.split(",");
		    for (int j = 0; j < tokens.length; j++) {
		   
		    	module[j] = tokens[j];
		    	System.out.println("module[j] : " + module[j]);
		    }
		    moduleList.add(module);
	    }
		
	 	// 2) pass string variable into doc
        Element table = doc.select("table").get(0);
		// erase old table
		Elements tbody = doc.select("tbody");
	    Elements rows = tbody.select("tr");
		//System.out.println("rows.size : " + tbody.size());
	
		for(int i = 1; i < rows.size(); i++) {
		  
		   rows.get(i).remove();
		}
		// fill table with new xdata
		//rows = doc.select("tr");
		System.out.println(moduleList.size());
		for (int i = 0; i < moduleList.size() ; i ++) {
			  // appending  each row on tbody is better than appending on table
		      module = moduleList.get(i); 
		      System.out.println(Arrays.toString(module));
			  tbody.get(0).append("<tbody><tr> <td>" + i + "</td>" + 
		            "<td>" + module[0] + "</td>"+ 
		    	    "<td>" + module[1] + "</td>" + 
		            "<td>" + module[2]  + "</td>" +
		            "<td>" + module[3] + "</td>" + 
		            "<td>" + module[4] + "</td>" +
		            "<td><button class=\"detail\" onClick=\"window.open('details.html');\"><span class=\"icon\">details</span></button></td></tr></tbody>"
		  );
		}
		 FileWriter fw;
		try {
			fw = new FileWriter(filePath);
			 fw.write(doc.toString());
		     fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} 

	public static void main(String[] args) throws IOException {

        JsoupParser jp = new JsoupParser();
        jp.setModuleName("abcd");
        //jp.sendDataToServer();
        //jp.displayResult("test4 passed");
        JsoupParser jp = new JsoupParser("/Users/MyHome/Desktop/github/TestMate/src/test/main.html");
	     //jp.displayResult("test5 passed");
	    jp.getTestJobFromServer("{{module1, 19091103, 7, 3, 10 }; {module2, 20161003, 6, 4, 10}}");
	    //jp.getTestJobFromServer("{{module3, 19091103, 7, 3, 10 }; {module4, 20161003, 6, 4, 10}}");



	}
	
   
}