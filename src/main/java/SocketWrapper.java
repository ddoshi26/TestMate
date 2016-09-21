import java.io.*;
import java.net.*;

/**
 * Created by Dhairya on 9/20/2016.
 */
public class SocketWrapper {
    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter outstream;
    BufferedInputStream inputStream;
    int portNumber;

    public SocketWrapper(int portNumber) {
        this.portNumber = portNumber;

        this.clientSocket = null;
        this.serverSocket = null;
    }

    public boolean createServer() {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }

        try {
            clientSocket = serverSocket.accept();
            if (serverSocket != null && clientSocket != null) {
                this.outstream = new PrintWriter(clientSocket.getOutputStream(), true);
                this.inputStream = new BufferedInputStream(clientSocket.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
