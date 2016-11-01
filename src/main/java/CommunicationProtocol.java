/**
 * Created by Dhairya on 9/28/2016.
 */
public class CommunicationProtocol {
    private static final int WAITING = 0;
    private static final int SENTINITIALMESSAGE = 1;
    private static final int COMMUNICATING = 2;
    private static final int CREATE_MODULE = 3;
    private static final int SEND_MODULE = 4;
    private static final int RUN_MODULE = 5;
    private static final int GET_ALL = 6;
    private static final int CLOSE_SOCKET = -1;

    private int state = SENTINITIALMESSAGE;

    public int getState() {
        return state;
    }

    public void resetState() {
        state = WAITING;
    }

    public String processInput(String inMessage) {
        String outMessage = "";

        if (inMessage.equalsIgnoreCase("Bye")) {
            state = CLOSE_SOCKET;
            return inMessage;
        }

        // @ Misha and Chris Refer to this to see what kind of messages are expected
        if (inMessage.equalsIgnoreCase("Hi Server! I am the Input form")) {
            outMessage = "Hi InputForm. Send me you data";
            state = COMMUNICATING;
        }
        else if (inMessage.equalsIgnoreCase("Hi Server! I am the Display form")) {
            outMessage = "Hi DisplayForm. What do you need";
            state = COMMUNICATING;
        }
        // Format: "SEND:<ModuleName>"
        else if (inMessage.contains("SEND")) {
            outMessage = "Received Send message:" + inMessage;
            state = SEND_MODULE;
        }
        // Format: "CREATE:{{ModuleName:"moduleName"},{Files:<File1,...(Executable file); Test File; Script file...>}}
        else if (inMessage.contains("CREATE")) {
            outMessage = "Received message to Create:" + inMessage;
            state = CREATE_MODULE;
        }
        // Format: "RUN:<ModuleName>"
        else if (inMessage.contains("RUN")) {
            outMessage = "Received Run message:" + inMessage;
            state = RUN_MODULE;
        }
        else if (inMessage.contains("GET ALL")) {
            outMessage = "GET ALL MODULES";
            state = GET_ALL;
        }
        else {
            outMessage = "Unknown input";
            state = WAITING;
        }

        return outMessage;
    }
}