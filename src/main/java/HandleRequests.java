import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.DDBClient;
import database.TestModule;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

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
        if (socketWrapper.createServer() && socketWrapper.getConnection()) {
            while ((message = socketWrapper.getMessage()) != null) {
                if (message.equalsIgnoreCase("") || message.equals("0:")) {
                    break;
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
                        }
                        else {
                            socketWrapper.sendMessage("Failed to create module:" + moduleName);
                        }
                    }
                    // SEND Module to UI
                    if (message.charAt(0) == '4') {
                        String moduleName = message.substring(message.indexOf(':') + 1);
                        TestModule sendModule;


                        if (StringUtils.isBlank(moduleName) || (sendModule = ddbClient.getTestModule(moduleName)) == null) {
                            socketWrapper.sendMessage("ModuleName Invalid or Not Found:" + moduleName);
                        }
                        else {
                            try {
                                socketWrapper.sendMessage(mapper.writeValueAsString(sendModule));
                            } catch (JsonProcessingException e) {
                                socketWrapper.sendMessage("Failed to parse module. Deleting it from DB");

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
                        // TODO: RUN SCRIPT BASED ON ORDER
                    }
                }
                message = "";
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

        for (int i = 0; i < runModule.getScriptFile().length(); i++) {
            String fileLocation = runModule.getScriptFile().toString();
            String type = getFileType(fileLocation);
            String fileName = getFileName(fileLocation);
            runCommand(type + " " + fileLocation, );
        }

        String fileLocationComplete = runModule.getScriptFile().toString();
        String fileLocation = fileLocationComplete.substring(0, fileLocationComplete.lastIndexOf('/') + 1);

        String fileName = fileLocation + "script_" + date.getTime() + ".sh";

        //TODO: Complete creating file using TestModule and first run execute file

        Process scriptFileExecute = null;
        String scriptFileLocation = runModule.getScriptFile().toString();
        try {
            scriptFileExecute = Runtime.getRuntime().exec(scriptFileLocation);
            scriptFileExecute.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fw = new FileWriter(fileName);
            PrintWriter pw = new PrintWriter(fw);

            pw.println("#!/bin/bash");
            pw.println("ls -al");

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        runCommand("sh " + fileName, fileName);
    }

    private void runCommand(String command, String fileName) {
        Process proc = null;

        try {
            proc = Runtime.getRuntime().exec(command);
            proc.waitFor();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
            socketWrapper.sendMessage("Invalid file location:" + fileName);    //TODO: Potential bug location; don't send any error messages
        } catch (InterruptedException e) {
            socketWrapper.sendMessage("Process interrupted while execution in progress:" + command);
            e.printStackTrace();
        }
    }

    public String getFileName(String fileLocation) {
        return fileLocation.substring(fileLocation.lastIndexOf('/') + 1);
    }

    public String getFileType(String fileLocation) {
        if (fileLocation.lastIndexOf('.') != -1)
            return fileLocation.substring(fileLocation.lastIndexOf('.') + 1);
        return null;
    }

    public static void main(String[] args) {
        HandleRequests hr = new HandleRequests();
        hr.run();
    }
}
