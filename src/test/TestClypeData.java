package test;
import data.MessageClypeData;

import java.io.IOException;

import data.FileClypeData;

public class TestClypeData {
	public static void main( String[] args ) {
		// TEST MessageClypeData
		
		// constructor
		MessageClypeData messageData = new MessageClypeData("Chris", "Hello world", "key", 0);
		
		// test encrypt, decrypt
		String encrypted = messageData.getData();
		System.out.println(encrypted);
		String decrypted = messageData.getData("key");
		System.out.println(decrypted);
		
//		System.out.println(messageData.toString());
//		
//		// default constructor
//		MessageClypeData messageDataDefault = new MessageClypeData();
//		System.out.println(messageDataDefault.toString());
//		
//		// getData method
//		System.out.println(messageData.getData());
//		
//		// equals method
//		System.out.println(messageData.equals(messageData));
//		System.out.println(messageData.equals(messageDataDefault));
//		
//		// hashcode method
//		System.out.println(messageData.hashCode());
//		
//		// TEST FileClypeData
//		
//		// default constructor
//		FileClypeData fileDataDefault = new FileClypeData();
//		System.out.println(fileDataDefault.toString());
//		
//		// constructor
		FileClypeData fileData = new FileClypeData("Chris", "file1.txt", 0);
		FileClypeData fileData2 = new FileClypeData("Chris", "file2.txt", 0);

		// test file I/O
		try {
			fileData.readFileContents();
			System.out.println(fileData.getData());
			
			fileData.writeFileContents();
			
			fileData2.readFileContents("key");
			System.out.println(fileData2.getData());
			
			fileData2.writeFileContents("key");
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		
//		System.out.println(fileData.toString());
//		
//		// setFileName method
//		fileData.setFileName("file2.csv");
//		System.out.println(fileData.toString());
//		
//		// getFileName method
//		System.out.println(fileData.getFileName());
//		
//		// equals method
//		System.out.println(fileData.equals(fileDataDefault));
//		System.out.println(fileData.equals(fileData));
//		
//		// hashcode method
//		System.out.println(fileData.hashCode());

	}
}
