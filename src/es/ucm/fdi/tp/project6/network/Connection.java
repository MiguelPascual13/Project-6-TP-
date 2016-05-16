package es.ucm.fdi.tp.project6.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
	
	private Socket s;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public Connection(Socket s) throws IOException{
		this.s = s;
		this.out = new ObjectOutputStream(s.getOutputStream());
		this.in = new ObjectInputStream(s.getInputStream());
	}
	
	public void sendObject(Object obj) throws IOException{
		out.writeObject(obj);
		out.flush();
		out.reset();
	}
	public Object getObject() throws ClassNotFoundException, IOException{
		return in.readObject();
	}
	
	public void stop() throws IOException{
		s.close();
	}
}
