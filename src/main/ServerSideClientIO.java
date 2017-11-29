package main;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

import data.*;

/**
 * 
 * @author Jared Heidt, Chris Carter
 *
 */
public class ServerSideClientIO implements Runnable {
	private ClypeServer server;
	private Socket clientSocket;

	private boolean closeConnection;

	private ClypeData dataToRecieveFromClient;
	private ClypeData dataToSendToClient;

	private ObjectInputStream inFromClient;
	private ObjectOutputStream outToClient;

	private String clientUserName;

	public ServerSideClientIO(ClypeServer server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;

		this.closeConnection = false;

		this.dataToRecieveFromClient = this.dataToSendToClient = null;
		this.inFromClient = null;
		this.outToClient = null;
		this.clientUserName = null;
	}

	/***
	 * Runs the server side listener.
	 */
	@Override
	public void run() {
		try {
			this.inFromClient = new ObjectInputStream(this.clientSocket.getInputStream());
			this.outToClient = new ObjectOutputStream(this.clientSocket.getOutputStream());

			recieveUserName();
			System.out.println("Accepted new client: " + clientUserName + ' ' + new Date());

			sendListUsers();
			
			while (!this.closeConnection) {
				recieveData();
				if (this.dataToRecieveFromClient != null) {
					this.setSendDataToClient(this.dataToRecieveFromClient);
					broadcastToClients();
					this.dataToSendToClient = null;
				}
			}

			this.inFromClient.close();
			this.outToClient.close();
			this.clientSocket.close();
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage() + " with " +this.clientUserName);
		}
	}

	/***
	 * Recieves the username from the client.
	 */
	public void recieveUserName() {
		recieveData();
		this.clientUserName = this.dataToRecieveFromClient.getUserName();
		this.dataToRecieveFromClient = null;
	}

	/***
	 * Recieve data from client.
	 */
	public void recieveData() {
		try {
			dataToRecieveFromClient = (ClypeData) inFromClient.readObject();
	
			if (dataToRecieveFromClient.getType() == (ClypeData.LOG_OUT)) {
				this.dataToSendToClient = this.dataToRecieveFromClient;
				sendData();
				this.server.remove(this);
				dataToRecieveFromClient = null;
				sendListUsers();
				this.inFromClient.close();
				this.outToClient.close();
				this.clientSocket.close();
				this.closeConnection = true;
			}
		} catch (NullPointerException npe) {
			System.err.println("Null pointer issue recieving data server side: " + npe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.err.println("Class not found server side" + cnfe.getMessage());
		} catch (InvalidClassException ice) {
			System.err.println("Invalid class issue recieving data server side: " + ice.getMessage());
		} catch (SocketException se) {
			System.err.println("Socket issue recieving data server side: " + se.getMessage());
			closeConnection = true;
			this.server.remove(this);
			dataToRecieveFromClient = null;
			try {
				this.inFromClient.close();
				this.outToClient.close();
				this.clientSocket.close();
			} catch (IOException ioe) {
				System.err.println(ioe.getMessage() + " with " + this.clientUserName);
			}
		} catch (IOException ioe) {
			System.err.println("Issue recieving data server side: " + ioe.getMessage());
			closeConnection = true;
			this.server.remove(this);
			dataToRecieveFromClient = null;
			try {
				this.inFromClient.close();
				this.outToClient.close();
				this.clientSocket.close();
			} catch (IOException ioe2) {
				System.err.println("Error closing streams and sockets server side: " + ioe2.getMessage());
			}
		}
	}

	/***
	 * Sends data to client.
	 */
	public void sendData() {
		try {
			this.outToClient.writeObject(this.dataToSendToClient);
		} catch (InvalidClassException ice) {
			System.err.println("Sending invalid class server side: " + ice.getMessage());
		} catch (IOException ioe) {
			System.err.println("Issue sending data server side: " + ioe.getMessage());
		}
	}

	/***
	 * Sends ClypeData object to client.
	 * 
	 * @param toSendToClient
	 *            A ClypeData object to send to client.
	 */
	public void sendData(ClypeData toSendToClient) {
		try {
			this.outToClient.writeObject(toSendToClient);
		} catch (IOException ioe) {
			System.err.println("Issue sending data server side");
		}
	}

	private void sendListUsers() {
		setSendDataToClient(new MessageClypeData("",server.getListUsers(),ClypeData.LIST_USERS));
		broadcastToClients();
	}
	
	/***
	 * Sets data to send to client
	 * 
	 * @param dataToSendToClient
	 *            A ClypeData object to send to client.
	 */
	public void setSendDataToClient(ClypeData dataToSendToClient) {
		this.dataToSendToClient = dataToSendToClient;
	}

	/***
	 * Broadcasts data to clients.
	 */
	public void broadcastToClients() {
	//	if (this.dataToRecieveFromClient.getType() != ClypeData.LIST_USERS) {
			this.server.broadcast(this.dataToSendToClient); // send to all clients
	//	} else {
	//		this.server.broadcast(this.dataToSendToClient, this);// send to only this client
	//	}

	}

	public String getUserName() {
		return this.clientUserName;
	}

}
