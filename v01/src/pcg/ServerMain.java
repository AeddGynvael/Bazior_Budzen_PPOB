package pcg;

public class ServerMain {

	public static void main(String[] args) {
			int port=8828;
			Server server = new Server(port);
			server.start();
	}

}
