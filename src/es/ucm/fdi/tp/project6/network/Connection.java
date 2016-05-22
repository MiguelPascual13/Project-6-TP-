package es.ucm.fdi.tp.project6.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Just an utility class to make easier to send and receive objects in a
 * Client-Server relation.
 */
public class Connection {

	private Socket socket;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	/**
	 * Determines the reference to the socket and creates the object streams.
	 * 
	 * @param socket
	 * @throws IOException
	 */
	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		this.objectInputStream = new ObjectInputStream(socket.getInputStream());
	}

	/**
	 * Sends an object to the other end point of the socket, just prevents from
	 * forgetting the flush or the reset
	 * 
	 * @param obj
	 * @throws IOException
	 */
	public void sendObject(Object object) throws IOException {
		objectOutputStream.writeObject(object);
		objectOutputStream.flush();
		objectOutputStream.reset();
	}

	/**
	 * Just a simple getter of the object sent by the other tube end point.
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Object getObject() throws ClassNotFoundException, IOException {
		return objectInputStream.readObject();
	}

	/**
	 * Closes the socket, remember that sockets as well as streams must be
	 * closed at the end.
	 * 
	 * @throws IOException
	 */
	public void stop() throws IOException {
		socket.close();
	}
}
