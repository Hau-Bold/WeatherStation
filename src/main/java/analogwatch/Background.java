package analogwatch;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import client.IOperatingSystemSettings;

class Background extends JPanel {
	private static final long serialVersionUID = 1L;
	BufferedImage myBufferedImage;

	Background(IOperatingSystemSettings operatingSystemSettings) {

		setLayout(new BorderLayout());
		try {
			myBufferedImage = ImageIO.read(new File(operatingSystemSettings.getPathToImageForBackground()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			g.drawImage(resizeImage(myBufferedImage, getWidth(), getHeight()), 0, 0, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight)
			throws IOException {
		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, 1);
		Graphics2D graphics2D = resizedImage.createGraphics();
		graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		graphics2D.dispose();
		return resizedImage;
	}
}
