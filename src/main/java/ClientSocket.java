import java.io.*;
import java.net.*;

/**
 * Created by Dhairya on 10/18/2016.
 */
public class ClientSocket {

    public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 8001;

        try {
            Socket clientSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String fromServer;
            String fromUser;

            out.println("Hello Server");
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;

                out.println("Hello Server");
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Could not connect to host on port:" + portNumber);
            System.exit(1);
        }
    }
}
