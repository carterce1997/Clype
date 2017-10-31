package main;

import data.*;
import java.io.*;
import java.net.BindException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * A class representing the user client.
 * 
 * @author Chris
 *
 */
public class ClypeClient {
	private final static int DEFAULT_PORT = 7000;
	private final static int MININMUM_PORT_NUM = 1024;
	private final static String ANONYMOUS_USER = "Anon";
	private final static String LOCAL_HOST = "localhost";

	private String userName;
	private String hostName;
	private int port;
	private boolean closeConnection;

	private ClypeData dataToSendToServer;
	private ClypeData dataToRecieveFromServer;
	private java.util.Scanner inFromStd;

	private ObjectInputStream inFromServer;
	private ObjectOutputStream outToServer;

	/**
	 * A constructor taking the username, hostname, and port.
	 * 
	 * @param userName
	 *            The client username.
	 * @param hostName
	 *            The hostname.
	 * @param port
	 *            The port.
	 */
	public ClypeClient(String userName, String hostName, int port) throws IllegalArgumentException {
		if (port < MININMUM_PORT_NUM) {
			throw new IllegalArgumentException("Illegal port number given to ClypeClient constructor");
		}
		if (userName == null) {
			throw new IllegalArgumentException("null user name given to ClypeClient constructor");
		}
		if (hostName == null) {
			throw new IllegalArgumentException("null host namegiven to ClypeClient constructor");
		}
		this.userName = userName;
		this.hostName = hostName;
		this.port = port;
		this.inFromServer = null;
		this.outToServer = null;
	}

	/**
	 * A constructor taking the username and hostname, with default port set to
	 * 7000.
	 * 
	 * @param userName
	 *            The client username.
	 * @param hostName
	 *            The hostname.
	 */
	public ClypeClient(String userName, String hostName) {
		this(userName, hostName, DEFAULT_PORT);
	}

	/**
	 * A constructor taking a username and setting the default hostname to
	 * "localhost" and the default port to 7000.
	 * 
	 * @param userName
	 *            The client username.
	 */
	public ClypeClient(String userName) {
		this(userName, LOCAL_HOST);
	}

	/**
	 * A default constructor setting the username to "Anon", the hostname to
	 * "localhost", and the port to 7000.
	 */
	public ClypeClient() {
		this(ANONYMOUS_USER);
	}

	/**
	 * Starts the client.
	 */
	public void start() {
		try {
			this.inFromStd = new java.util.Scanner(System.in);
			System.out.println("Attempting to connect to server.");
			Socket socket = new Socket(this.hostName, this.port);

			this.outToServer = new ObjectOutputStream(socket.getOutputStream());
			this.inFromServer = new ObjectInputStream(socket.getInputStream());
			System.out.println("Connected to server.");

			Thread listener = new Thread(new ClientSideServerListener(this));
			listener.start();

			sendUserName();
			System.out.println("Enter a message to send to other users: ");

			while (!this.closeConnection) {
				readClientData();
				sendData();
			}

			try {
				listener.join();
			} catch (InterruptedException ie) {
				System.err.println(ie.getMessage());
			}

			this.outToServer.close();
			this.inFromServer.close();
			this.inFromStd.close();
			socket.close();
		} catch (BindException be) {
			System.err.println("ClypeClient - Unable to bind a socket to a port: " + be.getMessage());
		} catch (ConnectException ce) {
			System.err.println("ClypeClient - Unable to connect to port: " + ce.getMessage());
		} catch (NoRouteToHostException nrthe) {
			System.err.println("ClypeClient - No route to the host . . .");
		} catch (UnknownHostException uhe) {
			System.err.println("ClypeClient - Unknown host. . .");
		} catch (SocketException se) {
			System.err.println("ClypeClient - Socket exception: " + se.getMessage());
		} catch (IOException ioe) {
			System.err.println("ClypeClient - IO Exception: " + ioe.getMessage());
		}

	}

	private void sendUserName() {
		this.dataToSendToServer = new MessageClypeData(userName, userName, ClypeData.SEND_MESSAGE);
		sendData();
	}

	/**
	 * Reads client data from standard input.
	 */
	public void readClientData() {
		String command = this.inFromStd.next();
		if (command.equals("DONE")) {
			System.out.println("Closing connection ");
			this.dataToSendToServer = new MessageClypeData(userName, "LOG OUT", ClypeData.LOG_OUT);
			this.closeConnection = true;
			this.inFromStd.close();
		} else if (command.equals("SENDFILE")) {

			String fileName = this.inFromStd.next().toString();
			try {
				this.dataToSendToServer = new FileClypeData(userName, fileName, ClypeData.SEND_FILE);
				((FileClypeData) (this.dataToSendToServer)).readFileContents();

			} catch (FileNotFoundException fnfe) {
				this.dataToSendToServer = null;
				System.err.println("Client end - File was not found while reading client data.");
			} catch (IOException ioe) {
				this.dataToSendToServer = null;
				System.err.println("Client end - Input output error (exception) while reading client data.");
			}
		} else if (command.equals("LISTUSERS")) {
			this.dataToSendToServer = new MessageClypeData(userName, "list users", ClypeData.LIST_USERS);
		} else {
			String message = this.userName + ": " + command + this.inFromStd.nextLine();
			this.dataToSendToServer = new MessageClypeData(userName, message, ClypeData.SEND_MESSAGE);
		}

	}

	/**
	 * Receives client data from the server.
	 */
	public boolean recieveData() {
		try {
			boolean socketClosed = false;
			// System.err.println("closeConnection: " + this.closeConnection);
			if (!this.closeConnection) {
				this.dataToRecieveFromServer = (ClypeData) this.inFromServer.readObject();
				if (this.dataToRecieveFromServer.getType() == ClypeData.LOG_OUT) {
					socketClosed = true;
				}
			} else {
				this.dataToRecieveFromServer = null;
			}
			return socketClosed;
		} catch (IOException ioe) {
			System.err.println("closeConnection: " + this.closeConnection);
			System.err.println("Issue recieving data client side: " + ioe.getMessage());

			ioe.printStackTrace();
		} catch (ClassNotFoundException cnf) {
			System.err.println("Class not found");
			return false;
		}
		return false;
	}

	/**
	 * Sends client data.
	 */
	public void sendData() {
		try {
			if (this.dataToSendToServer != null) {
				outToServer.writeObject(this.dataToSendToServer);
			}
			this.dataToSendToServer = null;
		} catch (IOException ioe) {
			System.err.println("Issue sending data");
		}
	}

	/**
	 * Prints the client data to standard output.
	 */
	public void printData() {
		// if ( this.dataToSendToServer != null ) {
		// System.out.println("Data to send to server:");
		// System.out.println("Username: " + this.dataToSendToServer.getUserName());
		// System.out.println("Date: " + this.dataToSendToServer.getDate());
		// System.out.println("Data: " + this.dataToSendToServer.getData());
		// }
		// if ( this.dataToRecieveFromServer != null ) {
		// System.out.println("Data to recieve from server:");
		// System.out.println("Username: " +
		// this.dataToRecieveFromServer.getUserName());
		// System.out.println("Date: " + this.dataToRecieveFromServer.getDate());
		// System.out.println("Data: " + this.dataToRecieveFromServer.getData());
		// }
		// if (this.dataToRecieveFromServer == null && this.dataToSendToServer == null)
		// {
		// System.out.println( "no data" );
		// }

		if (this.dataToRecieveFromServer != null) {
			System.out.println(this.dataToRecieveFromServer.getData());
		}
	}

	/**
	 * Return the username.
	 * 
	 * @return The client username.
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Returns the hostname.
	 * 
	 * @return The hostname.
	 */
	public String getHostName() {
		return this.hostName;
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
		result = result * 37 + this.userName.hashCode();
		result = result * 37 + this.hostName.hashCode();
		result = result * 37 + this.port;
		return result;
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (!(other instanceof ClypeClient)) {
			return false;
		}

		ClypeClient otherClient = (ClypeClient) other;
		return this.userName == otherClient.getUserName() && this.hostName == otherClient.getHostName()
				&& this.port == otherClient.getPort();
	}

	public String toString() {
		String result = "Username: " + this.userName + "\nHostname: " + this.hostName + "\nPort: " + this.port
				+ "\nClose Connection: " + this.closeConnection + "\nData to send: " + this.dataToSendToServer.getData()
				+ "\nData to recieve: " + this.dataToRecieveFromServer.getData();
		return result;
	}

	public boolean connectionOpen() {
		return !this.closeConnection;
	}

	/**
	 * The main method scans the input arguments for a user name, host name, and
	 * port number. After obtaining this information, a ClypeClient object is
	 * created and its start() method is called.
	 * 
	 * @param args
	 *            Case (i): <username> e.g., java ClypeClient Sherlock Case (ii):
	 *            <username>@<hostname> e.g., java ClypeClient
	 *            Sherlock@192.168.23.45 Case (iii):
	 *            <username>@<hostname>:<portnumber> e.g., java ClypeClient
	 *            Sherlock@192.168.23.45:12415
	 */
	public static void main(String[] args) {
		ClypeClient client;
		if (args.length > 0) {
			Scanner argScanner = new Scanner(args[0]);
			argScanner.useDelimiter("[@:]");

			String username = null;
			String hostname = null;
			int port = -1;

			if (argScanner.hasNext()) {
				username = argScanner.next();
			}
			if (argScanner.hasNext()) {
				hostname = argScanner.next();
			}
			if (argScanner.hasNext()) {
				port = argScanner.nextInt();
			}
			argScanner.close();

			if (hostname == null) {
				client = new ClypeClient(username);
			} else if (port == -1) {
				client = new ClypeClient(username, hostname);
			} else {
				client = new ClypeClient(username, hostname, port);
			}
		} else {
			client = new ClypeClient();
		}
		client.start();
	}
}
