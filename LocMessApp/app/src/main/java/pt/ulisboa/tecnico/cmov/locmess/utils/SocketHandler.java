package pt.ulisboa.tecnico.cmov.locmess.utils;

import java.net.Socket;

/**
 * Created by dharuqueshil on 09/04/2017.
 */

public class SocketHandler {
    private static Socket socket;
    private static String token;
    private static String username;

    public static synchronized Socket getSocket() {
        return socket;
    }

    public static synchronized void setSocket(Socket socket) {
        SocketHandler.socket = socket;
    }

    public static synchronized String getToken() {
        return token;
    }

    public static synchronized void setToken(String token) {
        SocketHandler.token = token;
    }

    public static synchronized String getUsername() { return username; }

    public static synchronized void setUsername(String username) { SocketHandler.username = username; }
}