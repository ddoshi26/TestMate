


public class Main {
    public static void main(String[] args) {

        System.err.println("**** TestMate - Server Initializing... ****");

        HandleRequests hr = new HandleRequests();

        hr.run();

    }
}
