import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.util.*;

/**
 * Created by Dhairya on 9/20/2016.
 */
public class Server implements Runnable {
    int portNumber;
    private SocketWrapper socketWrapper = null;
    private ArrayList<String> fileLocationList;

    public Server() {
        portNumber = 9125;
        fileLocationList = new ArrayList<String>();
    }

    public Server(int portNumber) {
        if (portNumber < 1000 || portNumber > 9999) {
            System.out.println("Invalid port input");
        }
        else {
            this.portNumber = portNumber;
        }
        fileLocationList = new ArrayList<String>();
    }

    public ArrayList<String> getInputFiles() {
        // TODO: Get file location from user
        Scanner in = new Scanner(System.in);
        char continue_char = 'n';

        System.out.println("Please provide the full path of the file that you wish to execute\n");
        while (fileLocationList.size() < 3 || continue_char == 'y') {
            if (fileLocationList.size() < 3) {
                String fileName = getFile(in, fileLocationList.size());
            }
            else {
                System.out.println("Do you wish to enter another file? \n Enter (y) or (n): ");

                continue_char = (char) in.nextByte();
                if (continue_char == 'y') {
                    String fileName = getFile(in, fileLocationList.size());
                    if (StringUtils.isBlank(fileName) || fileName.equals("")) {
                        return fileLocationList;
                    } else {
                        fileLocationList.add(fileName);
                    }
                } else {
                    break;
                }
            }
        }
        return null;
    }

    public String getFile(Scanner in, int fileNumber) {
        System.out.println("Enter file " + fileNumber + ": ");
        String file = in.nextLine();

        if ((StringUtils.isNotBlank(file) && fileLocationList.contains(file)) || (StringUtils.isBlank(file))) {
            System.out.print("This file has already been provided.");

            if (fileLocationList.size() < 3) {
                System.out.print("Please enter a different file: ");
                file = getFile(in, fileNumber);
                System.out.println();
            } else {
                System.out.println("Do you wish to enter another file? \n Enter (y) or (n): ");

                char continue_char = (char) in.nextByte();
                if (continue_char == 'y') {
                    file = getFile(in, fileNumber);
                } else {
                    return "";
                }
            }
        } else {
            return file;
        }

        return "";
    }

    @Override
    public void run() {
        SocketWrapper socketWrapper = new SocketWrapper(portNumber);
    }


    public static void main(String [] args) {
        try {
            FileWriter fw = new FileWriter("/homes/doshid/script.sh");
            PrintWriter pw = new PrintWriter(fw);

            pw.println("#!/bin/bash");
            pw.println("ls -al");

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Process proc = null;

        try {
            proc = Runtime.getRuntime().exec("sh /homes/doshid/script.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
