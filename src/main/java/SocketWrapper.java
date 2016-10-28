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

            String outMessage = commProtocol.processInput(inMessage);
            outstream.println(outMessage);
            outstream.flush();

            if (commProtocol.getState() == 3 || commProtocol.getState() == 4 || commProtocol.getState() == 5) {
                return commProtocol.getState() + ":" + inMessage.substring(inMessage.indexOf(':') + 1);
            }
            else {
                return "WAITING";
            }

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

    public void sendMessage(String outMessage) {
        outstream.println(outMessage);
        outstream.flush();
    }
}
