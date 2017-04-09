package pt.ulisboa.tecnico.cmov.locmess.utils;

import java.net.Socket;

/**
 * Created by dharuqueshil on 09/04/2017.
 */

public class SocketHandler {
    private static Socket socket;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }
}