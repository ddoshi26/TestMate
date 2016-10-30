import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
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

@WebServlet(name = "TestServlet", urlPatterns = {"/Search"})
public class TestServlet extends HttpServlet {

	private struct Obj{
		String modNo;
		String testPassed;
		String testFailed;
		String totalTests;
		String lastRunDate;
	}

	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {


			Obj ob= new Obj();
			ob.modNo="1";				//Make an array of this object
			ob.testPassed="10";
			ob.testFailed="3";
			ob.totalTests="7";
			ob.lastRunDate="10/25/16";
			System.out.println("Active");
			
			request.setAttribute("testJob",ob);
			request.getRequestDispatcher("Main.jsp").forward(request, response);






		}
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		
		// Serious problem encountered?
		boolean error = false;
		
		// Get the name of the TestModule
		String name = request.getParameter("moduleName");
		String programFiles = request.getParameter("program_files");
		String testFiles = request.getParameter("test_files");
		String configScripts = request.getParameter("config_scripts");
		
		if (name == null || programFiles == null ||
				testFiles == null || configScripts == null) {
			error = true;
		}
		
		/*
		System.out.println("Parameter names");
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements()) {
			System.out.println(names.nextElement());
		}
		System.out.println("End parameter names");
		*/
		
		String[] parsedProgramFiles = programFiles.split(";");
		String[] parsedTestFiles = testFiles.split(";");
		String[] parsedConfigScripts = configScripts.split(";");
		
		/*
		for (String a: parsedProgramFiles) {
			System.out.println(a);
		}
		for (String a: parsedTestFiles) {
			System.out.println(a);
		}
		for (String a: parsedConfigScripts) {
			System.out.println(a);
		}
		*/
		
		/*
		JSoupParser.setProgramFiles(parsedProgramFiles);
		JSoupParser.setTestFiles(parsedProgramFiles);
		JSoupParser.setConfigScripts(parsedConfigScripts);		
		*/
		
		
		if (!error) {
			RequestDispatcher view = request.getRequestDispatcher("main.html");
			view.forward(request, response);
		}
	}
	
}