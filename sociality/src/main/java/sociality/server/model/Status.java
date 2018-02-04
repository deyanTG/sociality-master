package sociality.server.model;

public class Status {

	public boolean connected = false;

	public Status(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

}
