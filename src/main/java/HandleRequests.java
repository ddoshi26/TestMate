import database.DDBClient;

import java.util.ArrayList;

/**
 * Created by Dhairya on 10/12/2016.
 */
public class HandleRequests implements Runnable {
    SocketWrapper socketWrapper;
    String message;
    StaticPorts staticPorts = new StaticPorts();
    int currentModuleId = -1;
    ArrayList<ArrayList<String>> fileList;
    String moduleName;

    DDBClient ddbClient;

    public HandleRequests() {
        socketWrapper = new SocketWrapper(staticPorts.getPairList().get(StaticPorts.pos));
        StaticPorts.pos++;
        ddbClient = new DDBClient();
        //fileList(0) = Executable File list; fileList(1) = Test Files; fileList(2) = Script files
        fileList = new ArrayList<ArrayList<String>>(3);
        moduleName = null;
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
                        String moduleIdStr = message.substring(message.indexOf(':') + 1, message.length());
                        int moduleId = Integer.parseInt(moduleIdStr);
                        //TODO: Connect to DB and send info

                        if (moduleId != currentModuleId) {
                            fileList = new ArrayList<ArrayList<String>>(3);
                            currentModuleId = moduleId;
                            moduleName = "";
                        }
                    }
                    else if (message.charAt(0) == '3') {
                        // Message to create a new module should be of the format:
                        // {{ModuleId:"moduleId"},{ModuleName:"moduleName"},{Files:<File1,...(Executable file); Test File; Script file...>}}

                        String moduleIdStr = message.substring(message.indexOf(':') + 1, message.indexOf('}'));
                        int moduleId = Integer.parseInt(moduleIdStr);

                        if (moduleId != currentModuleId) {
                            fileList = new ArrayList<ArrayList<String>>(3);
                            currentModuleId = moduleId;
                            moduleName = "";
                        }

                        parseFileLocationString(message.substring(message.indexOf(',') + 1));
                        //TODO: Create a TestModule object and send it to DB.
                        ddbClient.createNewTestModule(moduleName, fileList.get(0).get(0), fileList.get(1).get(0), fileList.get(2).get(0));
                    }
                }
            }

        }
    }

    // ModuleName:"moduleName"},{Files:<File1, ...>}}
    public void parseFileLocationString(String str) {
        int i = 0;
        int len = str.length();
        String file = "";

        moduleName = str.substring(str.indexOf('"') + 1, str.indexOf('}') - 1);

        i = str.indexOf(':', str.indexOf(','));

        int fileListindex = 0;

        while (i < len) {
            file = "";
            char c;

            while ((c = str.charAt(i)) != ';' && i < len) {
                while (c != ',' && i < len) {
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

    public static void main(String[] args) {
        HandleRequests hr = new HandleRequests();
        hr.run();
    }
}
