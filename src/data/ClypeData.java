package data;

import java.util.Date;
import java.awt.image.RenderedImage;
import java.io.Serializable;

/**
 * @author Chris Carter
 *
 *         A superclass representing data between the client and server.
 */
public abstract class ClypeData implements Serializable {
	public static final int LIST_USERS = 0;
	public static final int LOG_OUT = 1;
	public static final int SEND_FILE = 2;
	public static final int SEND_MESSAGE = 3;
	public static final int SEND_PHOTO = 4;

	public final static String alphabet = "abcdefghijklmnopqrstuvwxyz";
	public final static String alphabetUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final static String ANONYMOUS_USER = "Anon";

	private String userName;
	private int type;
	private Date date;

	/**
	 * A constructor taking a username and connection type.
	 * 
	 * @param userName
	 * @param type
	 */
	public ClypeData(String userName, int type) {
		this.userName = userName;
		this.type = type;
		this.date = new Date();
	}

	/**
	 * A constructor taking a connection type, that automatically sets username to
	 * "Anon".
	 * 
	 * @param type
	 */
	public ClypeData(int type) {
		this(ANONYMOUS_USER, type);
	}

	/**
	 * A default constructor that sets username to "Anon" and sets connection type
	 * to list all users.
	 */
	public ClypeData() {
		this(ANONYMOUS_USER, LIST_USERS);
	}

	/**
	 * Returns connection type.
	 * 
	 * @return type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns client username.
	 * 
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Returns date.
	 * 
	 * @return date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns data stored in subclass.
	 * 
	 * @return The data.
	 */
	public abstract String getData();
	
	/**
	 * Returns data stored in subclass.
	 * 
	 * @return The data.
	 */
	public abstract RenderedImage getData();

	/**
	 * Returns encrypted data stored in subclass in plaintext.
	 * 
	 * @param key
	 *            The encryption key.
	 * @return The data.
	 */
	public abstract String getData(String key);

	/**
	 * Encrypts a string using a key.
	 * 
	 * @param inputStringToEncrypt
	 *            The message to encrypt.
	 * @param key
	 *            The encryption key.
	 * @return The encrypted message.
	 */
	protected String encrypt(String inputStringToEncrypt, String key) {

		char[] arrayToEncrypt = inputStringToEncrypt.toCharArray();
		int arrayLength = arrayToEncrypt.length;

		char[] keyValues = key.toCharArray();

		char[] encryptedArray = new char[arrayLength];

		int j = 0;
		for (int i = 0; i < arrayLength; ++i) {
			if (j >= keyValues.length) {
				j = 0;
			}

			if (arrayToEncrypt[i] == ' ') {
				encryptedArray[i] = ' ';
			} else if (arrayToEncrypt[i] == '\n') {
				encryptedArray[i] = '\n';
			} else if (arrayToEncrypt[i] == '\r') {
				encryptedArray[i] = '\r';
			} else {
				char plaintextChar = arrayToEncrypt[i];
				char keyChar = keyValues[j];

				int keyLetter;
				if (Character.isLowerCase(plaintextChar)) {
					int plaintextLetter = (int) alphabet.indexOf(plaintextChar);

					if (Character.isLowerCase(keyChar)) {
						keyLetter = (int) alphabet.indexOf(keyChar);
					} else {
						keyLetter = (int) alphabetUpper.indexOf(keyChar);

					}
					encryptedArray[i] = alphabet.toCharArray()[(keyLetter + plaintextLetter) % 25];
				} else {
					int plaintextLetter = (int) alphabetUpper.indexOf(plaintextChar);

					if (Character.isLowerCase(keyChar)) {
						keyLetter = (int) alphabet.indexOf(keyChar);
					} else {
						keyLetter = (int) alphabetUpper.indexOf(keyChar);

					}
					encryptedArray[i] = alphabetUpper.toCharArray()[(keyLetter + plaintextLetter) % 25];
				}

				j += 1;
			}
		}
		return new String(encryptedArray);
	}

	/**
	 * Decrypts a string using a key.
	 * 
	 * @param inputStringToDecrypt
	 *            The message to decrypt.
	 * @param key
	 *            The encryption key.
	 * @return The decrypted message.
	 */
	protected String decrypt(String inputStringToDecrypt, String key) {

		char[] arrayToDecrypt = inputStringToDecrypt.toCharArray();
		int arrayLength = arrayToDecrypt.length;

		char[] keyValues = key.toCharArray();
		char[] decryptedArray = new char[arrayLength];

		int j = 0;
		for (int i = 0; i < arrayLength; ++i) {
			if (j >= keyValues.length) {
				j = 0;
			}

			if (arrayToDecrypt[i] == ' ') {
				decryptedArray[i] = ' ';
			} else if (arrayToDecrypt[i] == '\n') {
				decryptedArray[i] = '\n';
			} else if (arrayToDecrypt[i] == '\r') {
				decryptedArray[i] = '\r';
			} else {

				char ciphertextChar = arrayToDecrypt[i];
				char keyChar = keyValues[j];

				int keyLetter;
				if (Character.isLowerCase(ciphertextChar)) {
					int ciphertextLetter = (int) alphabet.indexOf(ciphertextChar);

					if (Character.isLowerCase(keyChar)) {
						keyLetter = (int) alphabet.indexOf(keyChar);
					} else {
						keyLetter = (int) alphabetUpper.indexOf(keyChar);

					}
					int new_index = (ciphertextLetter - keyLetter) % 25;
					if (new_index < 0)
						new_index += 25;
					decryptedArray[i] = alphabet.toCharArray()[new_index];
				} else {
					int ciphertextLetter = (int) alphabetUpper.indexOf(ciphertextChar);

					if (Character.isLowerCase(keyChar)) {
						keyLetter = (int) alphabet.indexOf(keyChar);
					} else {
						keyLetter = (int) alphabetUpper.indexOf(keyChar);

					}
					int new_index = (ciphertextLetter - keyLetter) % 25;
					if (new_index < 0)
						new_index += 25;
					decryptedArray[i] = alphabetUpper.toCharArray()[new_index];
				}

				j += 1;
			}
		}
		return new String(decryptedArray);
	}
}
