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
    int state;
    int portNumber;
    double currentModuleId = -1;

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

        return true;
    }

    public boolean getConnection() {
        try {
            clientSocket = serverSocket.accept();
            if (serverSocket != null && clientSocket != null) {
                this.outstream = new PrintWriter(clientSocket.getOutputStream(), true);
                this.inputStream = new BufferedInputStream(clientSocket.getInputStream());
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getMessage() {
        this.createServer();
        String inMessage = "";

        CommunicationProtocol commProtocol = new CommunicationProtocol();
        try {
            byte[] contents = new byte[1024];

            int bytesRead = 0;
            int read = 0;
            String strFileContents;
            while((read = inputStream.read(contents)) != -1) {
                inMessage += new String(contents, bytesRead, read);
                bytesRead += read;
            }

            if (commProtocol.getState() == 4) {
                currentModuleId = Double.parseDouble(inMessage);
            }
            else if (commProtocol.getState() == 3) {
                Server.addFile(currentModuleId, inMessage);
            }

            String outMessage = commProtocol.processInput(inMessage);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return inMessage;
    }
}
