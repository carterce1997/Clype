package data;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class PhotoClypeData extends ClypeData {
	private String fileName;
	private BufferedImage image;
	private byte[] buffered_image;

	public PhotoClypeData(String userName, String fileName, int type) {
		super(userName, type);
		this.fileName = fileName;

		try {
			BufferedImage originalImage = ImageIO.read(new File(fileName));

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "jpg", byteStream);
			byteStream.flush();
			buffered_image = byteStream.toByteArray();

			byteStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
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
	public void readData() {
		try {
			this.image = ImageIO.read(new File(this.fileName));
			// buffered_image= image.r
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Returns stored image
	 * 
	 * @return the image
	 */
	public BufferedImage getData() {
		try {
			InputStream in = new ByteArrayInputStream(buffered_image);
			BufferedImage bImageFromConvert = ImageIO.read(in);
			return bImageFromConvert;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

	/**
	 * Writes photo to fileName
	 */
	public void writeData(String fileName) {
		try {
			InputStream in = new ByteArrayInputStream(buffered_image);
			BufferedImage bImageFromConvert = ImageIO.read(in);
			
			File outputFile = new File(fileName);
			ImageIO.write(bImageFromConvert, "png", outputFile);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
