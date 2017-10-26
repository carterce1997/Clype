package test;
import main.ClypeServer;

public class TestClypeServer {
	public static void main( String[] args ) {
		// test constructor
		ClypeServer server = new ClypeServer( 22 );
		System.out.println(server.toString());
		
		// test default constructor
		ClypeServer server2 = new ClypeServer();
		System.out.println(server2.toString());
		
		// test get methods
		System.out.println(server.getPort());
		
		// test hashCode();
		System.out.println(server.hashCode());
		
		// test equals();
		ClypeServer server3 = new ClypeServer( 22 );
		
		System.out.println(server.equals(server));
		System.out.println(server.equals(server2));
		System.out.println(server.equals(server3));
		
	}
}
