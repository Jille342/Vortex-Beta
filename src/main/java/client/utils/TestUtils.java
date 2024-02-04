package client.utils;

import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TestUtils {
    public static String getHardwareID() throws NoSuchAlgorithmException, SocketException {
        MessageDigest e = MessageDigest.getInstance("SHA-256");
        StringBuilder s = new StringBuilder();
        s.append(System.getProperty("os.name"));
        s.append(System.getProperty("os.version"));
        s.append(System.getProperty("user.name"));
        s.append(System.getProperty("user.country"));
        s.append(System.getenv("COMPUTERNAME"));
        s.append(System.getenv("PROCESSOR_ARCHITECTURE"));
        s.append(System.getenv("NUMBER_OF_PROCESSORS"));
        s.append(System.getenv("JAVA_HOME"));
        s.append(System.getenv("windir"));
        return bytesToHex(e.digest(s.toString().getBytes()));
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0xF];
        }
        return new String(hexChars);
    }
    public static String getComputerUsername(){
        return "Computer User :"+System.getProperty("user.name").trim();
    }
}
