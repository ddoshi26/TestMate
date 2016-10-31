import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.DDBClient;
import database.TestJob;
import database.TestModule;

import java.io.*;
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

                            String moduleName = message.substring(message.indexOf(':') + 2, message.indexOf('}') - 1);

                            parseFileLocationString(message.substring(message.indexOf(',') + 1));
                            //TODO: Check for update option

                            ddbClient.createNewTestModule(moduleName, fileList.get(0).get(0), fileList.get(1).get(0), fileList.get(2).get(0));

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
                            TestModule runModule = ddbClient.getTestModule(message.substring(message.indexOf(':') + 1));

                            if (runModule == null) {
                                socketWrapper.sendMessage("Invalid module name");
                                return;
                            }

                            createBashScript(runModule);
                            // TODO: RUN SCRIPT BASED ON ORDER <------ WIP
                        }
                        //SEND ALL MODULES TO UI
                        // {{Name:<TM_Name>,Time:<TimeStamp>
                        else if (message.charAt(0) == '6') {
                            List<TestModule> moduleList = ddbClient.getAllTestModules();

                            TestJob latest;
                            String moduleListStr = "{";

                            for (int i = 0; i < moduleList.size(); i++) {
                                latest = ddbClient.getTestJob(moduleList.get(i).getLatestTestJobName());
                                /*
                                moduleListStr += "{Name:" + moduleList.get(i).getName() + ",";
                                moduleListStr += "Time" + latest.getTimestamp() + ",";
                                moduleListStr += "Total" + latest.getTotalTests() + ",";
                                moduleListStr += "Passed" + latest.getTestsPassed() +",";
                                moduleListStr += "Failed" + latest.getTestsFailed() + ",";
                               */
                                moduleListStr +=  "{" + moduleList.get(i).getName() + ",";
                                moduleListStr += latest.getTimestamp() + ",";
                                moduleListStr += latest.getTotalTests() + ",";
                                moduleListStr += latest.getTestsPassed() +",";
                                moduleListStr += latest.getTestsFailed() + ",";

                                moduleListStr += '}';
                                if (i != moduleList.size() - 1)
                                    moduleListStr += ';';
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
        int i = 0;
        int len = str.length();
        String file = "";

        i = str.indexOf(':', str.indexOf('{'));

        int fileListindex = 0;

        while (i < len) {
            file = "";
            char c;

            while ((c = str.charAt(i)) != ';' && i < len) {
                while (c != ',' && i < len) {
                    if (c != '{' && c != '}')
                        file += c;
                    i++;
                }
                i++;

                if (StringUtils.isNotBlank(file)) {
                    fileList.get(fileListindex).add(file);
                }
            }

            i++;
            fileListindex++;
        }

        if (StringUtils.isNotBlank(file)) {
            fileList.get(fileListindex).add(file);
        }
    }

    public void createBashScript(TestModule runModule) {
        if (runModule == null) {
            return;
        }

        //for (int i = 0; i < runModule.getScriptFile().length(); i++) {
            String fileLocationStr = runModule.getScriptFile().toJson();
            //TODO: ^ Parse this json and extract the str

            String type = getFileType(fileLocationStr);
            String fileNameStr = getFileName(fileLocationStr);

            String fileDirectory = fileLocationStr.substring(0, fileLocationStr.lastIndexOf('/') + 1);
            //TODO: NOTE: Do not change directory. Use absolute paths. runCommand("cd " + fileDirectory, null);

            if (type.equals("node install")) {
                runCommand(type, null, 60);
            }
            else {
                if (type.equals("")) {
                    runCommand(fileLocationStr, fileNameStr, 60);
                }
                else {
                    runCommand(type + " " + fileLocationStr, fileNameStr, 60);
                }
            }
        //}

        char lastChar = fileDirectory.charAt(fileDirectory.length() - 1);

        String execScriptfileName = fileDirectory + ((lastChar == '/') ? "" : "/") + "script_" + date.getTime() + ".sh";

        //TODO: Complete creating file using TestModule and first run execute file
        //TODO: ^^ Check TODO below

        Process scriptFileExecute = null;
        String scriptFileLocation = runModule.getScriptFile().toJson();
        // TODO: ^ Parse the json and get the actual location
//
//        try {
//            scriptFileExecute = Runtime.getRuntime().exec(scriptFileLocation);
//            scriptFileExecute.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            FileWriter fw = new FileWriter(execScriptfileName);
            PrintWriter pw = new PrintWriter(fw);

            pw.println("#!/bin/bash");
            //pw.println("cd " + fileDirectory);

            String jsonExec = runModule.getExecutableFile().toJson();
            //TODO: ^ Parse json to get actual file


            // TODO: Replace the below with the code in the comments
            ArrayList<String> testFileList = new ArrayList<String>(); //runModule.getTestFile();
            testFileList.add("homes/doshid/cs408/programsForTesting/module3/testall");
            testFileList.add("homes/doshid/cs408/programsForTesting/module2/testall");

            Map<String, ArrayList<String>> cmdList = new CommandListMap().commandList;

            for (int i = 0; i < testFileList.size(); i++) {
                String currentFileType = getFileType(testFileList.get(i));

                if (cmdList.containsKey(currentFileType)) {
                    cmdList.get(currentFileType).add(testFileList.get(i));
                }
                else {
                    cmdList.put(currentFileType, new ArrayList<String>());
                    cmdList.get(currentFileType).add(testFileList.get(i));
                }
            }

            Iterator<String> cmdListKey = cmdList.keySet().iterator();

            while (cmdListKey.hasNext()) {
                String key = cmdListKey.next();
                ArrayList<String> fileList = cmdList.get(key);

                pw.print(key + " ");
                for (int i = 0; i < fileList.size(); i++) {
                    pw.print(fileList.get(i));

                    if (i != fileList.size() - 1)
                        pw.print(" ");
                }
                pw.print("\n");
            }

            // TODO: Write the cmdList object into script file.
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        runCommand("sh " + execScriptfileName, null, 60);
    }

    private String runCommand(String command, String fileName, int timeout) {
        Process proc = null;

        try {
            proc = Runtime.getRuntime().exec(command);
            long start = System.currentTimeMillis();

            proc.waitFor(timeout, TimeUnit.SECONDS);

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

    public static void main(String[] args) {
        HandleRequests hr = new HandleRequests();
        hr.currentDir = hr.runCommand("pwd", null, 5);
        hr.currentDir = hr.currentDir.substring(0, hr.currentDir.length() - 1);
        hr.createBashScript(new TestModule());

        //String output = hr.runCommand("mv /u/data/u99/doshid/HuluCodingChallenge.py " + hr.currentDir, null);

        System.out.println(hr.currentDir + "\n");
        hr.run();
    }
}
