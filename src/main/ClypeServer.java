package main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import data.ClypeData;
import data.MessageClypeData;

public class ClypeServer {

	public static final int DEFAULT_PORT = 7000;
	private final static int MININMUM_PORT_NUM = 1024;

	private int port;
	private boolean closeConnection;
	private ArrayList<ServerSideClientIO> serverSideClientIOList;

	/**
	 * Constructor that takes port and sets data to null.
	 * 
	 * @param port
	 *            The port.
	 */
	public ClypeServer(int port) throws IllegalArgumentException {
		if (port < MININMUM_PORT_NUM) {
			throw new IllegalArgumentException("Illegal port number given to ClypeServer constructor");
		}
		this.port = port;
		this.serverSideClientIOList = new ArrayList<ServerSideClientIO>();
	}

	/**
	 * Default constructor, sets port to 7000.
	 */
	public ClypeServer() {
		this(DEFAULT_PORT);
	}

	/**
	 * Starts server.
	 */
	public void start() {
		try {
			ServerSocket socket = new ServerSocket(port);

			while (!this.closeConnection) {
				Socket clientSocket = socket.accept();
				ServerSideClientIO newServerSideClientIO = new ServerSideClientIO(this, clientSocket);

				this.serverSideClientIOList.add(newServerSideClientIO);
				Thread newClientThread = new Thread(newServerSideClientIO);
				newClientThread.start();
			}

			socket.close();

		} catch (IOException ioe) {
			System.err.println("Issue with IO on server side");
		}
	}

	/**
	 * Broadcasts data to all clients.
	 * 
	 * @param dataToBroadcastToClients
	 *            message to be sent to all clients
	 */
	public synchronized void broadcast(ClypeData dataToBroadcastToClients) {
		if (dataToBroadcastToClients.getType() != ClypeData.LIST_USERS) {
			for (ServerSideClientIO io : this.serverSideClientIOList) {
				io.setSendDataToClient(dataToBroadcastToClients);
				io.sendData();
			}
		} else {
			for (ServerSideClientIO io : this.serverSideClientIOList) {
				io.setSendDataToClient(dataToBroadcastToClients);
				io.sendData();
			}
		}
	}

	/**
	 * Broadcasts data to one client
	 * 
	 * @param dataToBroadcastToClients
	 *            message to be sent to one client only
	 */
	public synchronized void broadcast(ClypeData dataToBroadcastToClients, ServerSideClientIO client) {
		if (dataToBroadcastToClients.getType() == ClypeData.LIST_USERS) {
			String listOfUsers = getUsers();
			MessageClypeData usersData = new MessageClypeData(client.getUserName(), listOfUsers, ClypeData.LIST_USERS);
			client.sendData(usersData);
		} else
			client.sendData();
	}

	public synchronized void remove(ServerSideClientIO serverSideClientToRemove) {
		System.out.println("Closed Connection with: " + serverSideClientToRemove.getUserName() + " at " + new Date());
		this.serverSideClientIOList.remove(serverSideClientToRemove);
	}

	/**
	 * Returns the port.
	 * 
	 * @return The port.
	 */
	public int getPort() {
		return this.port;
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + this.port;
		result = result + 37 * (this.closeConnection ? 1 : 0);
		return result;
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (!(other instanceof ClypeServer)) {
			return false;
		}

		ClypeServer otherServer = (ClypeServer) other;
		return this.port == otherServer.getPort();
	}

	public String toString() {
		String result = "Port: " + this.port + "\nClose Connection: " + this.closeConnection;
		return result;
	}

	public String getUsers() {
		String listOfUsers = "";
		int userCounter = 1;
		for (ServerSideClientIO io : this.serverSideClientIOList) {
			listOfUsers += "User " + userCounter + ": " + io.getUserName() + System.getProperty("line.separator");
			++userCounter;
		}
		return listOfUsers;
	}

	/**
	 * Scans the given argument for a port number, creates a server object, and
	 * calls its start() method.
	 * 
	 * 
	 * @param args
	 *            The argument for the port number that the server connects to.<br>
	 *            Format for input: java ClypeServer \<portnumber\> <br>
	 *            Format for input: java ClypeServer 12415
	 */
	public static void main(String[] args) {
		try {
			ClypeServer server;
			if (args.length > 0) {
				Scanner argScanner = new Scanner(args[0]);
				int port = argScanner.nextInt();
				argScanner.close();
				server = new ClypeServer(port);
			} else {
				server = new ClypeServer();
			}

			server.start();

		} catch (IllegalArgumentException iae) {
			System.err.println(iae.getMessage());
		}

	}
}
