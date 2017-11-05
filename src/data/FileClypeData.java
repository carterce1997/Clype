package data;

import java.io.*;

/***
 * A class containing file data for Clype.
 * 
 * @authors Chris Carter, Jared Heidt
 *
 */
public class FileClypeData extends ClypeData {
	private String fileName;
	private String fileContents;

	/***
	 * A constructor for FileClypeData.
	 * 
	 * @param userName
	 *            The client's username.
	 * @param fileName
	 *            The filename for the data.
	 * @param type
	 *            The connection type.
	 */
	public FileClypeData(String userName, String fileName, int type) {
		super(userName, type);
		this.fileName = fileName;
		this.fileContents = null;
	}

	/***
	 * A default constructor that sets default userName to "Anon", fileName to "",
	 * fileContents to null, and connection type to 0.
	 */
	public FileClypeData() {
		super();
		this.fileName = "";
		this.fileContents = null;
	}

	/***
	 * Sets the data fileName.
	 * 
	 * @param fileName
	 *            The filename for the data.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/***
	 * Returns filename.
	 * 
	 * @return The data filename.
	 */
	public String getFileName() {
		return this.fileName;
	}

	/***
	 * Returns the file contents.
	 * 
	 * @return Plaintext file contents.
	 */
	public String getData() {
		return this.fileContents;
	}

	/***
	 * Returns encrypted file contents in plaintext with appropriate key.
	 * 
	 * @param The
	 *            encryption key.
	 * @return Decrypted file contents.
	 */
	public String getData(String key) {
		return super.decrypt(this.fileContents, key);
	}

	/***
	 * Reads file contents into the class from the given filename.
	 * 
	 * @throws IOException
	 */
	public void readFileContents() throws IOException {
		final String EOS = null;

		try {
			BufferedReader breader = new BufferedReader(new FileReader(fileName));
			StringBuilder fileData = new StringBuilder();
			String input;
			while ((input = breader.readLine()) != EOS) {
				fileData.append(input);
				fileData.append(System.getProperty("line.separator"));
			}
			breader.close();

			fileContents = fileData.toString();

		} catch (FileNotFoundException fnfe) {
			System.err.println("File not found");
		} catch (IOException ioe) {
			throw new IOException("Unable to read file");
		}
	}

	/***
	 * Reads file contents into the class from the given filename, and encrypts
	 * them.
	 * 
	 * @param key
	 *            The encryption key.
	 * @throws IOException
	 */
	public void readFileContents(String key) throws IOException {
		final String EOS = null;

		try {
			BufferedReader breader = new BufferedReader(new FileReader(fileName));
			StringBuilder fileData = new StringBuilder();
			String input;
			while ((input = breader.readLine()) != EOS) {
				fileData.append(input);
				fileData.append(System.getProperty("line.separator"));
			}
			breader.close();

			fileContents = super.encrypt(fileData.toString(), key);

		} catch (FileNotFoundException fnfe) {
			System.err.println("File not found");
		} catch (IOException ioe) {
			throw new IOException("Unable to read file");
		}
	}

	/***
	 * Writes file contents to the given filename.
	 */
	public void writeFileContents() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(fileContents);
			writer.close();
		} catch (FileNotFoundException fnfe) {
			System.err.println("File not found");
		} catch (IOException ioe) {
			System.err.println("Unable to write file");
		}
	}

	/***
	 * Encrypts file contents, and writes them to the given filename.
	 * 
	 * @param key
	 *            The encryption key.
	 */
	public void writeFileContents(String key) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			fileContents = super.decrypt(fileContents, key);

			writer.write(fileContents);
			writer.close();
		} catch (FileNotFoundException fnfe) {
			System.err.println("File not found");
		} catch (IOException ioe) {
			System.err.println("Unable to write file");
		}
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + this.fileName.hashCode();
		result = 37 * result + this.fileContents.hashCode();
		return result;
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (!(other instanceof FileClypeData)) {
			return false;
		}

		FileClypeData otherFile = (FileClypeData) other;
		return this.fileContents == otherFile.getData() && this.fileName == otherFile.getFileName();
	}

	public String toString() {
		return "Username: " + super.getUserName() + "\nDate: " + super.getDate() + "\nConnection Type: "
				+ super.getType() + "\nFile Name: " + this.fileName + "\nFile Content: " + this.fileContents;
	}

}
