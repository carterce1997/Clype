package main;

import data.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/** 
 * A class representing the user client.
 * @author Chris
 *
 */
public class ClypeClient {
	public static final int DEFAULT_PORT = 7000;
	
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
	 * @param userName The client username.
	 * @param hostName The hostname.
	 * @param port The port.
	 */
	public ClypeClient( String userName, String hostName, int port ) {
		this.userName =  userName;
		this.hostName = hostName;
		this.port = port;
		this.inFromServer = null;
		this.outToServer = null;
	}
	
	/**
	 * A constructor taking the username and hostname, with default port set to 7000.
	 * @param userName The client username.
	 * @param hostName The hostname.
	 */
	public ClypeClient( String userName, String hostName ) {
		this( userName, hostName, DEFAULT_PORT );
	}
	
	/**
	 * A constructor taking a username and setting the default hostname to "localhost" and the default port to 7000.
	 * @param userName The client username.
	 */
	public ClypeClient( String userName ) {
		this(userName, "localhost");
	}
	
	/**
	 * A default constructor setting the username to "Anon", the hostname to "localhost", and the port to 7000.
	 */
	public ClypeClient() {
		this( "anon" );
	}
	
	/**
	 * Starts the client.
	 */
	public void start() {
		try {		
			this.inFromStd = new java.util.Scanner(System.in);
			System.out.println("Read client data:");
			this.readClientData();
			
			Socket socket = new Socket( this.hostName, this.port );
			
			this.outToServer = new ObjectOutputStream( socket.getOutputStream() );
			this.inFromServer = new ObjectInputStream( socket.getInputStream() );
			
			Thread listener = new Thread( new ClientSideServerListener( this ) );
			listener.start();
			
			while ( ! this.closeConnection ) {
				if ( this.dataToSendToServer != null ) {
					this.sendData();
				} else {
					this.readClientData();
				}
			}
					
			this.outToServer.close();
			this.inFromServer.close();
			this.inFromStd.close();
			socket.close();			
		} catch (IOException ioe) {
			System.err.println( "Issue with starting client side" );
		} 

	}
	
	/**
	 * Reads client data from standard input.
	 */
	public void readClientData() {
		try {
			String doWhat = this.inFromStd.next().toString();
			if (doWhat.equals("DONE")) {
				this.closeConnection = true;
				this.inFromStd.close();
			} else if (doWhat.equals("SENDFILE")) {
				String filename = this.inFromStd.next().toString();
				this.dataToSendToServer  = new FileClypeData( userName, filename, 2 );
				((FileClypeData) this.dataToSendToServer).readFileContents();
			} else if (doWhat.equals("LISTUSERS")) {
			} else {
				this.inFromStd.nextLine();
				String message = this.inFromStd.nextLine();
				this.dataToSendToServer = new MessageClypeData( userName, message, 3 );
			}
		} catch (IOException ioe) {
			System.err.println( "Issue reading client data" );
		}	
	}
	
	/**
	 * Sends client data.
	 */
	public void sendData() {
		try {
			this.outToServer.writeObject( this.dataToSendToServer );
			this.dataToSendToServer = null;
		} catch (IOException ioe) {
			System.err.println( "Issue sending data" );
		}
	}
	
	/**
	 * Receives client data from the server.
	 */
	public void recieveData() {
		try {
			this.dataToRecieveFromServer = (ClypeData) this.inFromServer.readObject();
		} catch ( IOException ioe ) {
			System.err.println( "Issue recieving data client side" );
			this.closeConnection = true;
	    } catch ( ClassNotFoundException cnf ) {
			System.err.println( "Class not found" );
		}
	}
	
	/**
	 * Prints the client data to standard output.
	 */
	public void printData() {
//		if ( this.dataToSendToServer != null ) {
//			System.out.println("Data to send to server:");
//			System.out.println("Username: " + this.dataToSendToServer.getUserName());
//			System.out.println("Date: " + this.dataToSendToServer.getDate());
//			System.out.println("Data: " + this.dataToSendToServer.getData());
//		} 
//		if ( this.dataToRecieveFromServer != null ) {
//			System.out.println("Data to recieve from server:");
//			System.out.println("Username: " + this.dataToRecieveFromServer.getUserName());
//			System.out.println("Date: " + this.dataToRecieveFromServer.getDate());
//			System.out.println("Data: " + this.dataToRecieveFromServer.getData());
//		}
//		if (this.dataToRecieveFromServer == null && this.dataToSendToServer == null) {
//			System.out.println( "no data" );
//		}
		
		if (this.dataToRecieveFromServer != null) {
			System.out.println( this.dataToRecieveFromServer.getData() );
		}
	}
	
	/**
	 * Return the username.
	 * @return The client username.
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * Returns the hostname.
	 * @return The hostname.
	 */
	public String getHostName() {
		return this.hostName;
	}
	
	/**
	 * Returns the port.
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
		
		ClypeClient otherClient = (ClypeClient)other;
		return this.userName == otherClient.getUserName() && 
				this.hostName == otherClient.getHostName() && 
				this.port == otherClient.getPort();
	}
	
	public String toString() {
		String result = "Username: " + this.userName + 
				"\nHostname: " + this.hostName + 
				"\nPort: " + this.port + 
				"\nClose Connection: " + this.closeConnection +
				"\nData to send: " + this.dataToSendToServer.getData() +
				"\nData to recieve: " + this.dataToRecieveFromServer.getData();
		return result;
	}
	
	public boolean connectionOpen() {
		return !this.closeConnection;
	}
	
	public static void main( String[] args ) {
		ClypeClient client;
		if (args.length > 0) {
			Scanner argScanner = new Scanner( args[0] );
			argScanner.useDelimiter("[@:]");
						
			String username = null;
			String hostname = null;
			int port = -1;
			
			if ( argScanner.hasNext() ) {
				username = argScanner.next();
			} 
			if ( argScanner.hasNext() ) {
				hostname = argScanner.next();
			} 
			if ( argScanner.hasNext() ) {
				port = argScanner.nextInt();
			}
			argScanner.close();
			
			if ( hostname == null ) {
				client = new ClypeClient( username );
			} else if ( port == -1 ) {
				client = new ClypeClient( username, hostname );
			} else {
				client = new ClypeClient( username, hostname, port );
			}
		} else {
			client = new ClypeClient();
		}
		client.start();	
	}
}
