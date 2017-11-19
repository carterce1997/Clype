package application;

import java.util.LinkedList;
import java.util.Queue;

import data.ClypeData;

public class ConvoBuffer {
	private Queue<String> buffer; 
	private int maxSize;
	
	public ConvoBuffer(int maxSize) {
		this.maxSize = maxSize;
		this.buffer = new LinkedList<>();
	}
	
	public String getBufferString() {
		String outputString = "";
		for (String s : this.buffer) {
			outputString += "\n" + s;
		}
		
		return outputString;
	}
	
	public void add(String userName, ClypeData data) {
		buffer.add( userName + ": " + data.getData() );
		while (buffer.size() > maxSize) {
			buffer.remove();
		}
	}
	
}
