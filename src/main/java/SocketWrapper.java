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
    String currentModuleName = null;

    public SocketWrapper(int portNumber) {
        this.portNumber = portNumber;
        this.clientSocket = null;
        this.serverSocket = null;
    }

    public boolean createServer() {
        try {
            serverSocket = new ServerSocket(portNumber);
            serverSocket.setReuseAddress(true);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getConnection() {
        try {
            clientSocket = serverSocket.accept();
            System.out.println("Got connection");
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
        if (serverSocket == null)
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
                if (inMessage.contains("\r\n"))
                    break;
            }

            if (commProtocol.getState() == 4) {
                currentModuleName = inMessage;
                outstream.println("Received moduleName:" + inMessage);
                outstream.flush();
                return commProtocol.getState() + ":" + currentModuleName;
            }
            else if (commProtocol.getState() == 3) {
                outstream.println("Received info for moduleName:" + currentModuleName + ";" + inMessage);
                outstream.flush();
                return commProtocol.getState() + ":" + currentModuleName + "," + inMessage;
            }

            String outMessage = commProtocol.processInput(inMessage);
            outstream.println(outMessage);
            outstream.flush();
        } catch (IOException e) {
            try {
                outstream.flush();
                serverSocket.close();
                outstream.close();
                inputStream.close();
                clientSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Connection closed");
        }

        return "" + commProtocol.getState() + ":" + inMessage;
    }
}
