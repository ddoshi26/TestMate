/**
 * Created by Dhairya on 10/12/2016.
 */
public class HandleRequests implements Runnable {
    SocketWrapper socketWrapper;
    String message;

    public HandleRequests() {
        socketWrapper = new SocketWrapper(StaticPorts.getPairList().get(StaticPorts.pos));
        StaticPorts.pos++;
    }

    @Override
    public void run() {
        if (socketWrapper.createServer() && socketWrapper.getConnection()) {
            while ((message = socketWrapper.getMessage()) != null) {
                if (message.equalsIgnoreCase(""));
            }

        }
    }

    public String processInput(String inMessage) {
        String outMessage = "";

        if (inMessage.equalsIgnoreCase("Hi Server! I am the Input form")) {

        }
        else if (inMessage.equalsIgnoreCase("Hi Server! I am the Display form")) {

        }
        else if (inMessage.equalsIgnoreCase("Module Id: ")) {

        }

        return outMessage;
    }
}
