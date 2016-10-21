import database.DDBClient;
import database.TestModule;

import java.io.*;
import java.util.ArrayList;

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


    public HandleRequests() {
        socketWrapper = new SocketWrapper(staticPorts.getPairList().get(StaticPorts.pos));
        StaticPorts.pos++;
        ddbClient = new DDBClient();

        //fileList(0) = Executable File list; fileList(1) = Test Files; fileList(2) = Script files
        fileList = new ArrayList<ArrayList<String>>(3);
        currentModuleName = null;
    }

    @Override
    public void run() {
        if (socketWrapper.createServer() && socketWrapper.getConnection()) {
            while ((message = socketWrapper.getMessage()) != null) {
                if (message.equalsIgnoreCase("")) {
                    break;
                }
                else {
                    if (message.charAt(0) == '4') {
                        String moduleNameStr = message.substring(message.indexOf(':') + 1, message.length());
                        //TODO: Connect to DB and send info

                        if (moduleNameStr != currentModuleName) {
                            fileList = new ArrayList<ArrayList<String>>(3);
                            currentModuleName = moduleNameStr;
                        }

                        currentModule = ddbClient.getTestModule(moduleNameStr);
                    }
                    else if (message.charAt(0) == '3') {
                        // Message to create a new module should be of the format:
                        // {{ModuleName:"moduleName"},{Files:<File1,...(Executable file); Test File; Script file...>}}

                        String moduleNameStr = message.substring(message.indexOf(':') + 2, message.indexOf('}') - 1);
                        String moduleName = moduleNameStr;

                        if (moduleName != currentModuleName) {
                            fileList = new ArrayList<ArrayList<String>>(3);
                            currentModuleName = moduleName;
                        }

                        parseFileLocationString(message.substring(message.indexOf(',') + 1));
                        //TODO: Create a TestModule object and send it to DB.
                        ddbClient.createNewTestModule(currentModuleName, fileList.get(0).get(0), fileList.get(1).get(0), fileList.get(2).get(0));
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

    public void createBashScript(double moduleId) {
        if (fileList == null || currentModule == null) {
            System.out.println("Current module or file list is empty");
            return;
        }

        try {
            FileWriter fw = new FileWriter("/homes/doshid/script.sh");
            PrintWriter pw = new PrintWriter(fw);

            pw.println("#!/bin/bash");
            pw.println("ls -al");

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Process proc = null;

        try {
            proc = Runtime.getRuntime().exec("sh /homes/doshid/script.sh");
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HandleRequests hr = new HandleRequests();
        hr.run();
    }
}
