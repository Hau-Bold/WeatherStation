package analogwatch;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import client.IOperatingSystemSettings;

final class Background extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage originalImage;
	private BufferedImage scaledImage;

	Background(IOperatingSystemSettings operatingSystemSettings) {
		try {
			originalImage = ImageIO.read(new File(operatingSystemSettings.getPathToImageForBackground()));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load background image from path: "
					+ operatingSystemSettings.getPathToImageForBackground(), e);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (scaledImage == null && originalImage != null) {
			scaledImage = resizeImage(originalImage, getWidth(), getHeight());
		}

		if (scaledImage != null) {
			g.drawImage(scaledImage, 0, 0, this);
		}
	}

	private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resizedImage.createGraphics();
		g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		g2d.dispose();
		return resizedImage;
	}
}
