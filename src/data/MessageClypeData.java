package data;

/***
 * A class containing message data for Clype.
 * 
 * @author Chris
 *
 */
public class MessageClypeData extends ClypeData {
	private String message;

	/***
	 * A constructor to initialize the class. Takes the user's username, message,
	 * and connection type.
	 * 
	 * @param userName
	 *            The client username.
	 * @param message
	 *            The message to send.
	 * @param type
	 *            The connection type.
	 */
	public MessageClypeData(String userName, String message, int type) {
		super(userName, type);
		this.message = message;
	}
	
	/***
	 * A delegating default constructor. Defaults to username "Anon", connection
	 * type 0, and no message data.
	 */
	public MessageClypeData() {
		super();
		this.message = "";
	}

	/***
	 * A constructor that initializes the class by encrypting the message with a
	 * key.
	 * 
	 * @param userName
	 *            The client username.
	 * @param message
	 *            The message to encrypt and send.
	 * @param key
	 *            The encryption key.
	 * @param type
	 *            The connection type.
	 */
	public MessageClypeData(String userName, String message, String key, int type) {
		super(userName, type);
		this.message = super.encrypt(message, key);
	}

	/***
	 * Accessor for message data.
	 * 
	 * @return The message.
	 */
	public String getData() {
		return this.message;
	}

	/***
	 * Accessor for encrypted message data in plaintext.
	 * 
	 * @return The decrypted message.
	 */
	public String getData(String key) {
		return super.decrypt(this.message, key);
	}

	public int hashCode() {
		int result = 17;
		result = result + 37 * this.message.hashCode();
		return result;
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (!(other instanceof MessageClypeData)) {
			return false;
		}

		MessageClypeData otherMessage = (MessageClypeData) other;
		return this.message == otherMessage.getData();
	}

	public String toString() {
		return "Username: " + super.getUserName() + "\nDate: " + super.getDate() + "\nConnection Type: "
				+ super.getType() + "\nMessage: " + this.message;
	}

}
