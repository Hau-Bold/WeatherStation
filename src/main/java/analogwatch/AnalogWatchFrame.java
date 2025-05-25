package analogwatch;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import client.IOperatingSystemSettings;

public class AnalogWatchFrame extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	private long mySleepingTime = 10000L;

	private long myCurrentStartTimeInMillies;

	public static Boolean hasEnded = Boolean.FALSE;

	public AnalogWatchFrame(IOperatingSystemSettings operatingSystemSettings) {

		initComponent(operatingSystemSettings);
	}

	private void initComponent(IOperatingSystemSettings operatingSystemSettings) {
		setLookAndFeelOfApplication();

		Background background = new Background(operatingSystemSettings);

		setContentPane((Container) background);

		setLayout(new GridLayout(2, 5));
		setUndecorated(Boolean.TRUE.booleanValue());
		setAlwaysOnTop(Boolean.TRUE.booleanValue());

		adaptFrameToScreen();

		setTitle("WeatherStation");

		getContentPane().setBackground(Color.DARK_GRAY);
		setDefaultCloseOperation(3);

		add(new AnalogWatch("Europe/London"));
		add(new AnalogWatch("Europe/Berlin"));
		add(new AnalogWatch("America/Nome"));
		add(new AnalogWatch("America/Panama"));
		add(new AnalogWatch("Asia/Shanghai"));
		add(new AnalogWatch("Asia/Bangkok"));
		add(new AnalogWatch("Africa/Nairobi"));
		add(new AnalogWatch("Africa/Cairo"));
		add(new AnalogWatch("Australia/Kingston"));
		add(new AnalogWatch("Australia/Brisbane"));
	}

	private void adaptFrameToScreen() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, screensize.width, screensize.height);
	}

	private void showFrame() {
		setVisible(true);
	}

	private void hideFrame() {
		setVisible(false);
	}

	private void setLookAndFeelOfApplication() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			e2.printStackTrace();
		}
	}

	public void run() {
		while (hasEnded.booleanValue()) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

		showFrame();
		myCurrentStartTimeInMillies = System.currentTimeMillis();

		try {
			Boolean doExecute = Boolean.TRUE;

			while (doExecute.booleanValue()) {

				Thread.sleep(100);
				repaint();

				if (System.currentTimeMillis() - myCurrentStartTimeInMillies >= mySleepingTime) {
					doExecute = Boolean.FALSE;
				}
			}

			hideFrame();

			hasEnded = Boolean.TRUE;

			run();
		} catch (InterruptedException interruptedException) {
		}
	}
}