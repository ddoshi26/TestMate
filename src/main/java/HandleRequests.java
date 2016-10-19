import java.util.ArrayList;

/**
 * Created by Dhairya on 10/12/2016.
 */
public class HandleRequests implements Runnable {
    SocketWrapper socketWrapper;
    String message;
    StaticPorts staticPorts = new StaticPorts();
    int currentModuleId = -1;
    ArrayList<String> fileList;

    public HandleRequests() {
        socketWrapper = new SocketWrapper(staticPorts.getPairList().get(StaticPorts.pos));
        StaticPorts.pos++;
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
                        currentModuleId = Integer.parseInt(moduleIdStr);
                    }
                    else if (message.charAt(0) == '3') {
                        String moduleIdStr = message.substring(message.indexOf(':') + 1, message.length());
                        int moduleId = Integer.parseInt(moduleIdStr);

                        if (moduleId != currentModuleId) {
                            fileList = new ArrayList<String>();
                            currentModuleId = moduleId;
                        }

                        parseFileLocationString(message.substring(message.indexOf(';') + 1));
                    }
                }
            }

        }
    }

    public void parseFileLocationString(String fileLocationStr) {
        int i = 0;
        int len = fileLocationStr.length();
        String file;


        while (i < len) {
            file = "";
            char c;

            while ((c = fileLocationStr.charAt(i)) != ',' && i < len) {
                file += c;
                i++;
            }
            i++;
        }
    }

//    public String processInput(String inMessage) {
//        String outMessage = "";
//
//        // TODO: Get database functions and create/get Module to perform necessary action
//        if (inMessage.equalsIgnoreCase("Hi Server! I am the Input form")) {
//
//        }
//        else if (inMessage.equalsIgnoreCase("Hi Server! I am the Display form")) {
//
//        }
//        else if (inMessage.equalsIgnoreCase("Module Id: ")) {
//
//        }
//
//        return outMessage;
//    }

    public static void main(String[] args) {
        HandleRequests hr = new HandleRequests();
        hr.run();
    }
}
