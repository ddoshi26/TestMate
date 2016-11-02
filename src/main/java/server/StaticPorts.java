package server;

import java.util.ArrayList;

/**
 * Created by Dhairya on 10/12/2016.
 */
public class StaticPorts {
    public static ArrayList<Integer> portList = new ArrayList<Integer>();
    static int pos = 0;

    public static ArrayList<Integer> getPairList() {
        return portList;
    }

    public static int getPos() {
        return pos;
    }

    public StaticPorts() {
        for (int i = 8001; i < 9099; i++) {
            if (i != 8080 && i != 8888) {
                StaticPorts.portList.add(new Integer(i));
            }
        }
    }
}


