import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

@MultipartConfig
public class TestServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		
		// Check to determine that the form is multipart content
		if (!ServletFileUpload.isMultipartContent(request)) {
			System.out.println("No file data");
		} else {
			System.out.println("Contains file data");
		}
		
		// Get the name of the TestModule
		InputStreamReader isr = new InputStreamReader(request.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String name;
		while ((name = br.readLine()) != null) {
			if (name.contains("moduleName")) {
				br.readLine();
				name = br.readLine();
				break;
			}
		}
		
		// Get the main program files
	    List<Part> programFiles = request.getParts().stream().filter(
	    		part -> "program_files".equals(part.getName())).collect(Collectors.toList());
		
	    for (Part filePart: programFiles) {
	    	String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
	    	System.out.println(fileName);
	    	InputStream content = filePart.getInputStream();
	    }
		
	    // Get the test scripts
	    List<Part> testFiles = request.getParts().stream().filter(
	    		part -> "test_files".equals(part.getName())).collect(Collectors.toList());
		
	    for (Part filePart: testFiles) {
	    	String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
	    	System.out.println(fileName);
	    	InputStream content = filePart.getInputStream();
	    }
		
	    // Get the config scripts
	    List<Part> configScripts = request.getParts().stream().filter(
	    		part -> "config_scripts".equals(part.getName())).collect(Collectors.toList());
		
	    for (Part filePart: configScripts) {
	    	String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
	    	System.out.println(fileName);
	    	InputStream content = filePart.getInputStream();
	    }
		
		RequestDispatcher view = request.getRequestDispatcher("main.html");
		view.forward(request, response);
	}
	
}
