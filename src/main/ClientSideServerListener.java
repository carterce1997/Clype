package main;

/**
 * 
 * @author Jared Heidt, Chris Carter
 *
 */
public class ClientSideServerListener implements Runnable {
	private ClypeClient client;

	/***
	 * A constructor taking a ClypeClient object.
	 * 
	 * @param client
	 */
	public ClientSideServerListener(ClypeClient client) {
		this.client = client;
	}

	/***
	 * Runs the listener.
	 */
	@Override
	public void run() {
		boolean closedSocket = false;
		while (client.connectionOpen() && !closedSocket) {
			closedSocket = client.recieveData();
			client.printData();
		}
	}

}
