package main;

import java.io.*;
import java.net.Socket;
import data.*;

public class ServerSideClientIO implements Runnable {
	private ClypeServer server;
	private Socket clientSocket;
	
	private boolean closeConnection;
	
	private ClypeData dataToRecieveFromClient;
	private ClypeData dataToSendToClient;
	
	private ObjectInputStream inFromClient;
	private ObjectOutputStream outToClient;
	
	
	public ServerSideClientIO( ClypeServer server, Socket clientSocket ) {
		this.server = server;
		this.clientSocket = clientSocket;
		
		this.closeConnection = false;
		
		this.dataToRecieveFromClient = this.dataToSendToClient = null;
		this.inFromClient = null;
		this.outToClient = null;	
	}


	@Override
	public void run() {
		try {			
			this.inFromClient = new ObjectInputStream( this.clientSocket.getInputStream() );
			this.outToClient = new ObjectOutputStream( this.clientSocket.getOutputStream() );

			while ( ! this.closeConnection ) {
				this.recieveData();
				if (this.dataToRecieveFromClient != null) {
					this.setSendDataToClient( this.dataToRecieveFromClient );
					this.server.broadcast( this.dataToSendToClient );
					this.dataToSendToClient = null;
				}
			}
			
			this.inFromClient.close();
			this.outToClient.close();
			this.clientSocket.close();
			this.server.remove( this );
			
		} catch ( IOException ioe ) {
			System.err.println( "Issue running server side IO" );
		}
	}
	
	public void recieveData() {
		try {
			this.dataToRecieveFromClient = (ClypeData) this.inFromClient.readObject();
		} catch (IOException ioe ) {
			System.err.println( "Issue recieving data server side" );
			this.closeConnection = true;
		} catch (ClassNotFoundException cnf) {
			System.err.println( "Class not found server side" );
		}
	}
	
	public void sendData() {
		try {
			this.outToClient.writeObject( this.dataToSendToClient );
		} catch (IOException ioe ) {
			System.err.println( "Issue sending data server side" );
		}
	}
	
	public void setSendDataToClient( ClypeData dataToSendToClient ) {
		this.dataToSendToClient = dataToSendToClient;
	}
	
}
