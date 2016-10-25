import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Dhairya on 10/18/2016.
 */
public class ClientSocket {
    String name = "";
    String formType = "";

    public ClientSocket(String name, String formType) {
        this.name = name;
        this.formType = formType;
    }

    public void run() {
        String hostName = "localhost";
        int portNumber = 8001;

        try {
            Socket clientSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String fromServer = "";
            String fromUser;

            out.println("Hi Server! I am the " + formType);
            if ((fromServer = in.readLine()) != null){
                System.out.println("Server: " + fromServer);
                //if (fromServer.equals("Bye."))
                //break;
                out.println();
            }
        } catch(UnknownHostException e){
                System.err.println("Don't know about host " + hostName);
                System.exit(1);
        } catch(IOException e){
                System.err.println("Could not connect to host on port:" + portNumber);
                System.exit(1);
        }

    }
}
