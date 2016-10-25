/**
 * Created by Dhairya on 9/28/2016.
 */
public class CommunicationProtocol {
    private static final int WAITING = 0;
    private static final int SENTINITIALMESSAGE = 1;
    private static final int COMMUNICATING = 2;
    private static final int RECEIVING_MODULE = 3;
    private static final int RECEIVING_MODULE_NAME = 4;

    private int state = WAITING;

    public int getState() {
        return state;
    }

    public String processInput(String inMessage) {
        String outMessage = "";

        // @ Misha and Chris Refer to this to see what kind of messages are expected
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
            if (inMessage.equalsIgnoreCase("Module Id: ")) {
                outMessage = "Ready to get ModuleId";
                state = RECEIVING_MODULE_NAME;
            }
            else {
                outMessage = "Unknown input";
                state = WAITING;
            }
        }
        else if (state == RECEIVING_MODULE_NAME) {
            outMessage = "Received Module ID: " + inMessage;
            state = RECEIVING_MODULE;
        }
        else if (state == RECEIVING_MODULE) {
            outMessage = "Received Module: " + inMessage;
            state = COMMUNICATING;
        }
        else {
            outMessage = "Waiting for input";
            state = WAITING;
        }

        return outMessage;
    }
}
