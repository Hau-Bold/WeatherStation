package dht22_bmp180.services;

import java.io.File;
import java.util.concurrent.TimeoutException;

class Utils {

	static void waitUntilFileExists(File file, long timeout) throws TimeoutException {
		Boolean fileExists = file.exists();

		long start = System.currentTimeMillis();

		while (!fileExists && System.currentTimeMillis() - start < timeout) {
			/** give Python some time to react */
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fileExists = file.exists();
		}

		if (!fileExists) {
			throw new TimeoutException("File " + file + " is not available");
		}
	}

	static void waitUntilFileIsNotEmpty(File file, long timeout) throws TimeoutException {
		long fileContent = file.length();

		long start = System.currentTimeMillis();

		while (fileContent == 0 && System.currentTimeMillis() - start < timeout) {
			/** give Python some time to react */
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fileContent = file.length();
		}

		if (fileContent == 0) {
			throw new InternalError("Sensor that writes " + file + " is not working");
		}
	}

}
