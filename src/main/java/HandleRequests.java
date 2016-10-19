/**
 * Created by Dhairya on 10/12/2016.
 */
public class HandleRequests implements Runnable {
    SocketWrapper socketWrapper;
    String message;
    StaticPorts staticPorts = new StaticPorts();

    public HandleRequests() {
        socketWrapper = new SocketWrapper(staticPorts.getPairList().get(StaticPorts.pos));
        StaticPorts.pos++;
    }

    @Override
    public void run() {
        if (socketWrapper.createServer() && socketWrapper.getConnection()) {
            while ((message = socketWrapper.getMessage()) != null) {
                if (message.equalsIgnoreCase(""))
                    break;
            }

        }
    }

    public String processInput(String inMessage) {
        String outMessage = "";

        // TODO: Get database functions and create/get Module to perform necessary action
        if (inMessage.equalsIgnoreCase("Hi Server! I am the Input form")) {

        }
        else if (inMessage.equalsIgnoreCase("Hi Server! I am the Display form")) {

        }
        else if (inMessage.equalsIgnoreCase("Module Id: ")) {

        }

        return outMessage;
    }

    public static void main(String[] args) {
        HandleRequests hr = new HandleRequests();
        hr.run();
    }
}
