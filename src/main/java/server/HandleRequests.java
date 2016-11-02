package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.DDBClient;
import database.TestJob;
import database.TestModule;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Created by Dhairya on 10/12/2016.
 */
public class HandleRequests implements Runnable {
    SocketWrapper socketWrapper;
    String message;
    StaticPorts staticPorts = new StaticPorts();

    String currentModuleName;
    TestModule currentModule;
    ArrayList<ArrayList<String>> fileList;

    ArrayList<String> fileList1 = new ArrayList<String>();

    DDBClient ddbClient;
    Date date;
    ObjectMapper mapper = new ObjectMapper();

    String currentDir = "";

    public HandleRequests() {
        socketWrapper = new SocketWrapper(staticPorts.getPairList().get(StaticPorts.pos));
        StaticPorts.pos++;
        ddbClient = new DDBClient();

        //fileList(0) = Executable File list; fileList(1) = Test Files; fileList(2) = Script files
        fileList = new ArrayList<ArrayList<String>>(3);
        currentModuleName = null;
        date = new Date();
    }

    @Override
    public void run() {
        while (true) {
            if (socketWrapper.createServer() && socketWrapper.getConnection()) {
                while ((message = socketWrapper.getMessage()) != null) {
                    if (message.equalsIgnoreCase("") || message.equals("0:") || message.equalsIgnoreCase("Bye")) {
                        break;
                    }
                    else if (message.equalsIgnoreCase("WAITING")) {
                        continue;
                    }
                    else {
                        // CREATE Module
                        if (message.charAt(0) == '3') {
                            // Message to create a new module should be of the format:
                            // {{ModuleName:"moduleName"},{Files:<File1,...(Executable file); Test File; Script file...>}}

//                            StringTokenizer st = new StringTokenizer(message, "\"");
//                            st.nextToken();
//                            String moduleName = st.nextToken();

                            int f_index = message.indexOf(':');
                            String moduleName = message.substring(f_index + 1, message.indexOf('}'));

                            parseFileLocationString(message.substring(message.indexOf(',') + 1));
                            //TODO: Check for update option

                            ddbClient.createNewTestModule(moduleName, fileList1.get(0), fileList1.get(1), fileList1.get(2));

                            if (ddbClient.getTestModule(moduleName) != null) {
                                socketWrapper.sendMessage("Successfully created module:" + moduleName);
                            } else {
                                socketWrapper.sendMessage("Failed to create module:" + moduleName);
                            }
                        }
                        // SEND Module to UI
                        if (message.charAt(0) == '4') {
                            String moduleName = message.substring(message.indexOf(':') + 1);
                            TestModule sendModule;

                            String result = "{";
                            if (StringUtils.isBlank(moduleName) || (sendModule = ddbClient.getTestModule(moduleName)) == null) {
                                socketWrapper.sendMessage("ERROR: ModuleName Invalid or Not Found:" + moduleName);
                            } else {
                                result += "Name:" + moduleName +",";
                                result += "TestJobs:{";

                                List<TestJob> testJobList = ddbClient.getAllTestJobsForATestModule(moduleName);

                                for (int i = 0; i < testJobList.size(); i++) {
                                    result += moduleName + ",";
                                    result += testJobList.get(i).getTestJobName() + ",";
                                    result += testJobList.get(i).getTimestamp() + ",";
                                    result += testJobList.get(i).getTestsPassed() + ",";
                                    result += testJobList.get(i).getTestsFailed() + ",";
                                    result += testJobList.get(i).getTotalTests() + "}";

                                    if (i != testJobList.size() - 1) {
                                        result += ";";
                                    }
                                }
                                result += "}}";

                                try {
                                    socketWrapper.sendMessage(mapper.writeValueAsString(sendModule));
                                } catch (JsonProcessingException e) {
                                    socketWrapper.sendMessage("ERROR: Failed to parse module. Deleting it from DB");

                                    // TODO: DELETE MODULE IMPLEMENTATION MISSING IN DB
                                    // TODO: ANOTHER Bug could be single delete in only 1 table rather than cross deleting both TestModule and Relevant TestJobs
                                    e.printStackTrace();
                                }
                            }
                        }
                        // RUN Module
                        else if (message.charAt(0) == '5') {
                            StringTokenizer str = new StringTokenizer(message, ":");
                            str.nextToken(); str.nextToken();
                            String name = str.nextToken();

                            TestModule runModule = ddbClient.getTestModule(name);

                            if (runModule == null) {
                                socketWrapper.sendMessage("Invalid module name");
                                return;
                            }

                            boolean result = createBashScript(runModule);
                            if (result)
                                socketWrapper.sendMessage("Success");
                            else
                                socketWrapper.sendMessage("Failed");
                            // TODO: RUN SCRIPT BASED ON ORDER <------ WIP
                        }
                        //SEND ALL MODULES TO UI
                        // {{Name:<TM_Name>,Time:<TimeStamp>,
                        else if (message.charAt(0) == '6') {
                            List<TestModule> moduleList = ddbClient.getAllTestModules();
                            TestJob latest;
                            String moduleListStr = "{";

                            for (int i = 0; i < moduleList.size(); i++) {
                                latest = ddbClient.getTestJob(moduleList.get(i).getLatestTestJobName());

                                if (latest == null) {
                                    moduleListStr += "{Name:" + moduleList.get(i).getName() + ",";
                                    moduleListStr += "JobName:" + "NA" + ",";
                                    moduleListStr += "Time:" + "NA" + ",";
                                    moduleListStr += "Passed:" + "0" +",";
                                    moduleListStr += "Failed:" + "0" + ",";
                                    moduleListStr += "Total:" + "0";
                                }
                                else {
                                    moduleListStr += "{Name:" + moduleList.get(i).getName() + ",";
                                    moduleListStr += "JobName:" + latest.getTestJobName() + ",";
                                    moduleListStr += "Time:" + latest.getTimestamp() + ",";
                                    moduleListStr += "Passed:" + latest.getTestsPassed() + ",";
                                    moduleListStr += "Failed:" + latest.getTestsFailed() + ",";
                                    moduleListStr += "Total:" + latest.getTotalTests();
                                }

                                moduleListStr += '}';
                                if (i != moduleList.size() - 1)
                                    moduleListStr += ',';
                            }

                            moduleListStr +='}';

                            socketWrapper.sendMessage(moduleListStr);
                        }
                    }
                    message = "";
                }

            }
        }
    }

    // {Files:<File1,...(Executable file); Test File; Script file...>}}
    public void parseFileLocationString(String str) {
//        int i = 0;
//        int len = str.length();
//        String file = "";
//
//        i = str.indexOf(':', str.indexOf('{'));
//        i += 1;
//        int fileListindex = 0;
//
//        while (i < len) {
//            file = "";
//            char c;
//
//            while ((c = str.charAt(i)) != ';' && i < len) {
//                while ((c = str.charAt(i)) != ',' && i < len) {
//                    if (c == ';') {
//                        i++;
//                        break;
//                    }
//                    if (c != '{' && c != '}')
//                        file += c;
//                    i++;
//                }
//                i++;
//
//                if (server.StringUtils.isNotBlank(file)) {
//                    fileList.get(fileListindex).add(file);
//                }
//            }
//
//            i++;
//            fileListindex++;
//        }
//
//        if (server.StringUtils.isNotBlank(file)) {
//            fileList.get(fileListindex).add(file);
//        }

        StringTokenizer st = new StringTokenizer(str, "}");
        while(st.hasMoreTokens()) {
            String a = st.nextToken();

            //System.out.println(getValue(a) + "\n");
            String val = getValue(a);
            fileList1.add(val);
        }
    }

    public static String getValue(String pair) {
        StringTokenizer st = new StringTokenizer(pair, ":");
        st.nextToken();

        return st.nextToken();
    }

    public boolean createBashScript(TestModule runModule) {
        if (runModule == null) {
            return false;
        }

        //for (int i = 0; i < runModule.getScriptFile().length(); i++) {
       String fileLocationStr = runModule.filePathMap.get("testFile");
//        //TODO: ^ Parse this json and extract the str
//        if (fileLocationStr.equals(" "))
//        String type = getFileType(fileLocationStr);
//        String fileNameStr = getFileName(fileLocationStr);
//
//        String fileDirectory = fileLocationStr.substring(0, fileLocationStr.lastIndexOf('/') + 1);
//        //TODO: NOTE: Do not change directory. Use absolute paths. runCommand("cd " + fileDirectory, null);
//
//        if (type.equals("node install")) {
//            runCommand(type, null, 60);
//        }
//        else {
//            if (type.equals("")) {
//                runCommand(fileLocationStr, fileNameStr, 60);
//            }
//            else {
//                runCommand(type + " " + fileLocationStr, fileNameStr, 60);
//            }
//        }
//        //}
//
//        char lastChar = fileDirectory.charAt(fileDirectory.length() - 1);

//        String execScriptfileName = fileDirectory + ((lastChar == '/') ? "" : "/") + "script_" + date.getTime() + ".sh";
//
//        //TODO: Complete creating file using TestModule and first run execute file
//        //TODO: ^^ Check TODO below
//
//        Process scriptFileExecute = null;
//        String scriptFileLocation = runModule.getScriptFile().toJson();
//        // TODO: ^ Parse the json and get the actual location
////
////        try {
////            scriptFileExecute = Runtime.getRuntime().exec(scriptFileLocation);
////            scriptFileExecute.waitFor();
////        } catch (IOException e) {
////            e.printStackTrace();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//
////        try {
////            FileWriter fw = new FileWriter(execScriptfileName);
////            PrintWriter pw = new PrintWriter(fw);
////
////            pw.println("#!/bin/bash");
////            //pw.println("cd " + fileDirectory);
////
////            String jsonExec = runModule.getExecutableFile().toJson();
////            //TODO: ^ Parse json to get actual file
////
////
////            // TODO: Replace the below with the code in the comments
////            ArrayList<String> testFileList = new ArrayList<String>(); //runModule.getTestFile();
////            testFileList.add("homes/doshid/cs408/programsForTesting/module3/testall");
////            testFileList.add("homes/doshid/cs408/programsForTesting/module2/testall");
////
////            Map<String, ArrayList<String>> cmdList = new server.CommandListMap().commandList;
////
////            for (int i = 0; i < testFileList.size(); i++) {
////                String currentFileType = getFileType(testFileList.get(i));
////
////                if (cmdList.containsKey(currentFileType)) {
////                    cmdList.get(currentFileType).add(testFileList.get(i));
////                }
////                else {
////                    cmdList.put(currentFileType, new ArrayList<String>());
////                    cmdList.get(currentFileType).add(testFileList.get(i));
////                }
////            }
////
////            Iterator<String> cmdListKey = cmdList.keySet().iterator();
////
////            while (cmdListKey.hasNext()) {
////                String key = cmdListKey.next();
////                ArrayList<String> fileList = cmdList.get(key);
////
////                pw.print(key + " ");
////                for (int i = 0; i < fileList.size(); i++) {
////                    pw.print(fileList.get(i));
////
////                    if (i != fileList.size() - 1)
////                        pw.print(" ");
////                }
////                pw.print("\n");
////            }
////
////            // TODO: Write the cmdList object into script file.
////            pw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String scriptFile = runModule.filePathMap.get("scriptFile");
        String execFile = runModule.filePathMap.get("executableFile");
        String testFile = runModule.filePathMap.get("testFile");

        makePathExecutable(scriptFile);
        makePathExecutable(execFile);
        makePathExecutable(testFile);

        String message = runCommand(fileLocationStr, fileLocationStr, 1);

        // Parse message for tests passed, failed and total tests
        StringTokenizer st = new StringTokenizer(message, "\n");
        int totalTests = 0, testsPassed = 0, testsFailed = 0;
        while(st.hasMoreTokens()) {
            totalTests++;
            if(st.nextToken().contains("failed")) {
                testsPassed++;
            } else {
                testsFailed++;
            }
        }

        String testJobName = ddbClient.createNewTestJob(runModule.getName(), totalTests, testsPassed, testsFailed);
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date currentDate = new Date();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        ddbClient.updateTestModuleWithNewTestJob(runModule.getName(), testJobName, dateFormat.format(currentDate));
        return true;

        //runCommand("sh " + execScriptfileName, null, 60);
    }

    private String runCommand(String command, String fileName, int timeout) {
        Process proc = null;

        try {
            // set permissions
            makePathExecutable(fileName);

            proc = Runtime.getRuntime().exec(command);
            long start = System.currentTimeMillis();

            proc.waitFor(timeout, TimeUnit.MICROSECONDS);

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            StringBuilder builder = new StringBuilder();

            String line = null;

            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

            String result = builder.toString();

            return result;

        } catch (IOException e) {
            e.printStackTrace();

            if (StringUtils.isBlank(fileName)) {
                System.out.println("Invalid file operation");
            }
            else {
                socketWrapper.sendMessage("Invalid file location:" + fileName);    //TODO: Potential bug location; don't send any error messages
            }

        } catch (InterruptedException e) {
            socketWrapper.sendMessage("Process interrupted while execution in progress:" + command);
            e.printStackTrace();
        }
        return "";
    }

    public String getFileName(String fileLocation) {
        return fileLocation.substring(fileLocation.lastIndexOf('/') + 1);
    }

    public String getFileType(String fileLocation) {
        String type = "";

        if (fileLocation.lastIndexOf('.') != -1) {
            type = fileLocation.substring(fileLocation.lastIndexOf('.') + 1);

            switch (type) {
                case "json":
                    runCommand("node install", null, 60);
                    break;

                case "sh":
                case "java":
                case "py":
                    return type;

                case "c":
                case "cpp":
                default:
                    return "";
            }
        }
        else if (StringUtils.isBlank(type)) {
            return "";
        }

        return "";
    }

    public void makePathExecutable(String path) {
        // set permissions
        Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
        //add owners permission
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        //add group permissions
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        //add others permissions
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);

        try {
            Files.setPosixFilePermissions(Paths.get(path), perms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
