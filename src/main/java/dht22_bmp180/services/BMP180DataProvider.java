package dht22_bmp180.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeoutException;

import client.IOperatingSystemSettings;
import dht22_bmp180.weatherDataSets.BMP180SensorObject;

public class BMP180DataProvider {

	private String myPathToBMP180Data;
	private long myTimeout = Duration.ofSeconds(30).toMillis();
	SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public BMP180DataProvider(IOperatingSystemSettings operatingSystemSettings) {
		myPathToBMP180Data = operatingSystemSettings.getPathToBMP180Data();
	}

	public BMP180SensorObject getLatest(long currentTimeInMillies)
			throws IOException, InterruptedException, ParseException, TimeoutException {
		List<BMP180SensorObject> bMP180Data = readData();

		BMP180SensorObject result = null;

		Collections.sort(bMP180Data, new Comparator<BMP180SensorObject>() {
			@Override
			public int compare(BMP180SensorObject o1, BMP180SensorObject o2) {
				return Long.compare(o1.getTime(), o2.getTime());
			}
		});

		for (var dHT22 : bMP180Data) {

			if (dHT22.getTime() <= currentTimeInMillies) {
				result = dHT22;
			} else {
				break;
			}

		}

		return result;

	}

	private ArrayList<BMP180SensorObject> readData()
			throws IOException, InterruptedException, ParseException, TimeoutException {

		File bMP180File = new File(myPathToBMP180Data);
		Utils.waitUntilFileExists(bMP180File, myTimeout);

		Utils.waitUntilFileIsNotEmpty(bMP180File, myTimeout);

		var br = new BufferedReader(new FileReader(bMP180File));

		ArrayList<BMP180SensorObject> data = new ArrayList<BMP180SensorObject>();

		try {
			var line = br.readLine();

			while (line != null) {

				String[] dataEntry = line.split(",");

				if (dataEntry[0].equals("NAN")) {
					continue;
				}
				var pressure = dataEntry[0];
				var timestamp = dataEntry[1];

				var date = mySimpleDateFormat.parse(timestamp);

				data.add(new BMP180SensorObject(date.getTime(), Double.valueOf(pressure)));
				line = br.readLine();
			}

		} finally {
			br.close();
		}

		return data;
	}

}
