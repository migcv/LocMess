package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LocMess {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			ServerSocket ss = new ServerSocket(6666);
			Socket s = ss.accept();// establishes connection
			System.out.println("ACEITEI ALGUEM!");
			DataInputStream dis = new DataInputStream(s.getInputStream());
			String str = dis.readUTF();
			System.out.println("message= " + str);
			ss.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
