import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.util.*;

/**
 * Created by Dhairya on 9/20/2016.
 */
public class Server implements Runnable {
    static int portNumber;
    private static SocketWrapper socketWrapper = null;
    //private static ArrayList<String> fileLocationList;
    private static Map<Double, ArrayList<String>> ModuleIdFileList;
    private static double currentModuleId;
    private static ArrayList<Thread> threads;

    public Server() {
        portNumber = 9125;
        ModuleIdFileList = new HashMap<Double, ArrayList<String>>();
        currentModuleId = -1;
    }

    public Server(int portNumber) {
        if (portNumber < 1000 || portNumber > 9999) {
            System.out.println("Invalid port input");
        }
        else {
            this.portNumber = portNumber;
        }
        ModuleIdFileList = new HashMap<Double, ArrayList<String>>();
        currentModuleId = -1;
    }

    public ArrayList<String> getInputFiles() {
        // TODO: Get file location from user
        Scanner in = new Scanner(System.in);
        String continue_str = "n";

        System.out.println("Please provide the ModuleId:");
        String moduleIdStr = in.nextLine();

        double moduleId = Double.parseDouble(moduleIdStr);
        currentModuleId = moduleId;
        ArrayList<String> fileLocationList;

        if ((fileLocationList = ModuleIdFileList.get(moduleId)) == null) {
            System.out.println("Invalid moduleId");
            return null;
        }

        System.out.println("Please provide the full path of the file that you wish to execute\n");
        while (fileLocationList.size() < 3 || continue_str.equals("y")) {
            if (fileLocationList.size() < 3) {
                String fileName = getFile(in, fileLocationList.size());
            }
            else {
                System.out.println("Do you wish to enter another file? \n Enter (y) or (n): ");

                continue_str = in.nextLine();
                if (continue_str.equals("y")) {
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

    public static int addFile(double moduleId, String path) {
        ArrayList<String> fileLocationList;

        Double d = new Double(moduleId);

        if ((fileLocationList = ModuleIdFileList.get(d)) == null) {
            System.out.println("Invalid moduleId");
            return -1;
        }

        if (StringUtils.isNotBlank(path)) {
            if (fileLocationList.contains(path))
                return 1;
            else {
                fileLocationList.add(path);
                return 0;
            }
        }
        else {
            return -1;
        }
    }

    public String getFile(Scanner in, int fileNumber) {
        ArrayList<String> fileLocationList;

        Double d = new Double(currentModuleId);

        if ((fileLocationList = ModuleIdFileList.get(d)) == null) {
            System.out.println("Invalid moduleId");
            return null;
        }

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

                String continue_str = in.nextLine();
                if (continue_str.equals("y")) {
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
        Thread t1 = new Thread(socketWrapper = new SocketWrapper(portNumber));
        threads.add(t1);
        t1.start();
    }


    public static void createBashScript(double moduleId) {
        ArrayList<String> fileLocationList;

        Double d = new Double(currentModuleId);

        if ((fileLocationList = ModuleIdFileList.get(d)) == null) {
            System.out.println("Invalid moduleId");
            return;
        }

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
            proc.waitFor();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
