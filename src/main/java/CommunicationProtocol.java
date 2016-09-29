/**
 * Created by Dhairya on 9/28/2016.
 */
public class CommunicationProtocol {
    private static final int WAITING = 0;
    private static final int SENTINITIALMESSAGE = 1;
    private static final int COMMUNICATING = 2;
    private static final int RECEIVING_FILES = 3;
    private static final int RECEIVING_MODULE_ID = 4;

    private int state = WAITING;

    String inputValue = "";

    public int getState() {
        return state;
    }

    public String processInput(String inMessage) {
        String outMessage = "";

        if (state == WAITING) {
            outMessage = "Hey! This is the Server!";
            state = SENTINITIALMESSAGE;
        }
        else if (state == SENTINITIALMESSAGE) {
            if (inMessage.equalsIgnoreCase("Hi Server! I am the Input form")) {
                outMessage = "Hi InputForm. Send me you data";
                state = COMMUNICATING;
            }
            else if (inMessage.equalsIgnoreCase("Hi Server! I am the Display form")) {
                outMessage = "Hi DisplayForm. What do you need";
                state = COMMUNICATING;
            }
            else {
                outMessage = "Unknown input";
                state = WAITING;
            }
        }
        else if (state == COMMUNICATING) {
            if (inMessage.equalsIgnoreCase("Here are the file names")) {
                outMessage =  "I am ready. Send them over";
                state = RECEIVING_FILES;
            }
            else if (inMessage.equalsIgnoreCase("Send me the info for the module:")) {
                outMessage = "Ready to get ModuleId";
                state = RECEIVING_MODULE_ID;
            }
            else {
                outMessage = "Unknown input";
                state = WAITING;
            }
        }
        else if (state == RECEIVING_MODULE_ID || state == RECEIVING_FILES) {
            inputValue = inMessage;
            state = COMMUNICATING;
        }
        else {
            outMessage = "Waiting for input";
            state = WAITING;
        }

        return outMessage;
    }
}
