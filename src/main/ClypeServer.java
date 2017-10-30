package main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import data.ClypeData;

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

	public synchronized void broadcast(ClypeData dataToBroadcastToClients) {
		for (ServerSideClientIO io : this.serverSideClientIOList) {
			io.setSendDataToClient(dataToBroadcastToClients);
			io.sendData();
		}
	}

	public synchronized void remove(ServerSideClientIO serverSideClientToRemove) {
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
