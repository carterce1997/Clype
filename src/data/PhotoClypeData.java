package data;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;

public class PhotoClypeData extends ClypeData {
	private String fileName;
	private Image image;
	
	public PhotoClypeData(String userName, String fileName, int type) {
		super(userName, type);
		this.fileName = "file://" + fileName;
	}

	public PhotoClypeData(String userName, int type) {
		super(userName, type);
	}

	public PhotoClypeData(int type) {
		super(type);
	}
	
	public PhotoClypeData() {
		
	}
	
	/**
	 * Reads photo from input filename
	 */
	public void readClientData() {
		this.image = new Image( this.fileName );
	}
	
	/**
	 * Returns stored image
	 * @return the image
	 */
	public Image getData() {
		return this.image;
	}
	
	/**
	 * Writes photo to fileName
	 */
	public void writeData( String fileName) {
		try {
			File outputFile = new File(fileName);
			ImageIO.write((RenderedImage) this.image, "png", outputFile);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}


}
