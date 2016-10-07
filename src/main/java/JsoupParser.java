import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

//Prerequirement for html page : we need assign tag id="result".

public class JsoupParser {
	String filePath;  //the name of html to open and modify
	Document doc; // // get the Document for the fileName, which is easy to access in any methods in this class.

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
		 //System.out.println(res.text()); // for debug purpose
		 
		 // write back to the same file		 
    	 FileWriter fw = new FileWriter(filePath);
		 fw.write(doc.toString());
	     fw.close();
	
	}
	
	/*// usage example: 
	public static void main(String[] args) throws IOException {
	     
	     JsoupParser jp = new JsoupParser("C:\\Users\\User\\eclipse\\JsoupParser\\src\\FrontEnd.html");
	     jp.displayResult("test4 passed");
		 
		 
	}
    */
}