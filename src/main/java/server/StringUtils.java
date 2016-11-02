package server;

/**
 * Created by Dhairya on 10/12/2016.
 */
public class StringUtils {
    public static boolean isBlank(String str) {
        if (str == null || str.equals(""))
            return true;
        return false;
    }

    public static boolean isNotBlank(String str) {
        if (str != null && !str.equals(""))
            return true;
        return false;
    }
}
