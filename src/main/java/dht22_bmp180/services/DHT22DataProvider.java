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
import dht22_bmp180.weatherDataSets.DHT22SensorObject;

public class DHT22DataProvider {

	private String myPathToDHT22Data;
	private long myTimeout = Duration.ofSeconds(30).toMillis();
	SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public DHT22DataProvider(IOperatingSystemSettings operatingSystemSettings) {
		myPathToDHT22Data = operatingSystemSettings.getPathToDHT22Data();
	}

	public DHT22SensorObject getLatest(long currentTimeInMillies)
			throws IOException, InterruptedException, ParseException, TimeoutException {
		List<DHT22SensorObject> dHT22Data = readData();

		DHT22SensorObject result = null;

		Collections.sort(dHT22Data, new Comparator<DHT22SensorObject>() {
			@Override
			public int compare(DHT22SensorObject o1, DHT22SensorObject o2) {
				return Long.compare(o1.getTime(), o2.getTime());
			}
		});

		for (var dHT22 : dHT22Data) {

			if (dHT22.getTime() <= currentTimeInMillies) {
				result = dHT22;
			} else {
				break;
			}

		}

		return result;

	}

	private ArrayList<DHT22SensorObject> readData()
			throws IOException, InterruptedException, ParseException, TimeoutException {

		File dHT22File = new File(myPathToDHT22Data);
		Utils.waitUntilFileExists(dHT22File, myTimeout);

		Utils.waitUntilFileIsNotEmpty(dHT22File, myTimeout);

		var br = new BufferedReader(new FileReader(dHT22File));

		ArrayList<DHT22SensorObject> data = new ArrayList<DHT22SensorObject>();

		try {
			var line = br.readLine();

			while (line != null) {

				String[] dataEntry = line.split(",");

				if (!dataEntry[0].equals("NAN")) {

					var temperature = dataEntry[0];
					var humidity = dataEntry[1];
					var timestamp = dataEntry[2];

					var date = mySimpleDateFormat.parse(timestamp);

					data.add(new DHT22SensorObject(Double.valueOf(temperature), Double.valueOf(humidity),
							date.getTime()));
				}
				line = br.readLine();
			}

		} finally {
			br.close();
		}

		return data;
	}

}
