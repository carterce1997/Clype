package main;

public class ClientSideServerListener implements Runnable {
	private ClypeClient client;

	public ClientSideServerListener(ClypeClient client) {
		this.client = client;
	}

	@Override
	public void run() {
		boolean closedSocket = false;
		while (client.connectionOpen() && !closedSocket) {
			closedSocket = client.recieveData();
			client.printData();
		}
	}

}
