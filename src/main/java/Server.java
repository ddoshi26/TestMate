import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Dhairya on 9/20/2016.
 */
public class Server implements Runnable {
    int portNumber;
    SocketWrapper socketWrapper = null;

    public Server() {
        portNumber = 9125;
    }

    public Server(int portNumber) {
        if (portNumber < 1000 || portNumber > 9999) {
            System.out.println("Invalid port input");
        }
        else {
            this.portNumber = portNumber;
        }
    }

    public ArrayList<File> getInputFiles() {
        // TODO: Get files from user
        return null;
    }

    @Override
    public void run() {
        SocketWrapper socketWrapper = new SocketWrapper(portNumber);
    }
}
