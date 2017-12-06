package data;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PhotoClypeData extends ClypeData {
	private String fileName;
	private BufferedImage image;
	
	public PhotoClypeData(String userName, String fileName, int type) {
		super(userName, type);
		this.fileName = fileName;
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
		try {
			this.image = ImageIO.read( new File( this.fileName ));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Returns stored image
	 * @return the image
	 */
	public RenderedImage getData() {
		return this.image;
	}
	
	/**
	 * Writes photo to fileName
	 */
	public void writeData( String fileName) {
		try {
			File outputFile = new File(fileName);
			ImageIO.write(this.image, "png", outputFile);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}


}
