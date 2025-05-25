package display.Html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import digitalweatherstation.Time;

public class Canvas {
	private BufferedWriter myBufferedWriter;
	private List<JSData> myJSDatasOut;
	private List<JSData> myJSDatasIn;
	private String myColorOfDataToDisplayOut;
	private String myColorOfDataToDisplayIn;
	private String myColorOfText;

	public Canvas(BufferedWriter bufferedWriter, String colorOfDataToDisplayOut, String colorOfDataToDisplayIn,
			String colorOfText, List<JSData> jsDataOut, List<JSData> jsDataIn) {
		myBufferedWriter = bufferedWriter;
		myColorOfDataToDisplayOut = colorOfDataToDisplayOut;
		myColorOfDataToDisplayIn = colorOfDataToDisplayIn;
		myColorOfText = colorOfText;
		myJSDatasOut = jsDataOut;
		myJSDatasIn = jsDataIn;
	}

	public void write() throws IOException {
		AppendNewLine("<div>");
		AppendNewLine("<canvas id=\"myChart\" width=\"50\" height=\"10\"></canvas>");
		AppendNewLine("</div>");

		writeChart();
	}

	private void writeChart() throws IOException {

		var dataSetStringOut = getDataSetString(myJSDatasOut);
		var dataSetStringIn = getDataSetString(myJSDatasIn);

		AppendNewLine("<script>");
		AppendNewLine("Chart.defaults.font.size = \"20\"");
		AppendNewLine("Chart.defaults.color =\"" + myColorOfText + "\"" + ";");

		AppendNewLine("var ctx = document.getElementById('myChart');");
		AppendNewLine("var myChart = new Chart(ctx, {");
		AppendNewLine("data: {");
		writeLabels();
		writeDataSets();
		writeDataSet(dataSetStringOut, myColorOfDataToDisplayOut, "Out");
		if (dataSetStringIn != StringUtils.EMPTY) {
			writeDataSet(dataSetStringIn, myColorOfDataToDisplayIn, "In");
		}
		writeDataSetsEnd();
		AppendNewLine("},");
		writeOptions();
		AppendNewLine(" });");
		AppendNewLine("</script>");
	}

	private void writeLabels() throws IOException {
		AppendNewLine("labels: [");

		String labelString = getJSDataLabels();

		AppendNewLine(labelString);

		AppendNewLine("],");
	}

	private String getJSDataLabels() {
		StringBuilder builder = new StringBuilder();

		for (int index = 0; index < myJSDatasOut.size(); index++) {

			if (index % 4 == 0) {
				JSData jsData = myJSDatasOut.get(index);
				Time time = jsData.getTime();

				if (index > 0) {
					builder.append(",");
				}
				builder.append("'" + time.toString() + "'");
			} else {
				builder.append(",''");
			}
		}

		return builder.toString();
	}

	private void writeDataSets() throws IOException {
		AppendNewLine("datasets: [");
	}

	private void writeDataSetsEnd() throws IOException {
		AppendNewLine("]");

	}

	private void writeDataSet(String dataSetValues, String colorOfDataToDisplay, String legend) throws IOException {
		AppendNewLine("{");
		AppendNewLine("type: 'line',");
		AppendNewLine("label: '" + legend + "',");
		AppendNewLine("data: [ " + dataSetValues + "]" + ",");
		AppendNewLine("borderColor: '" + colorOfDataToDisplay + "',");
		AppendNewLine("tension: 0.1");
		AppendNewLine("},");
	}

	private String getDataSetString(List<JSData> datas) {

		if (datas == null) {
			return StringUtils.EMPTY;
		}

		StringBuilder builder = new StringBuilder();

		for (int index = 0; index < datas.size(); index++) {
			JSData jsData = datas.get(index);

			String value = jsData.getValue();

			if (index > 0) {
				builder.append(",");
			}
			builder.append("'" + value + "'");
		}

		return builder.toString();
	}

	private void writeOptions() throws IOException {
		AppendNewLine("options: {");

		AppendNewLine("legend: {");
		AppendNewLine("labels: {");
		AppendNewLine("fontColor: \"white\",");
		AppendNewLine("fontSize: 18");
		AppendNewLine("}");
		AppendNewLine("},");

		AppendNewLine("scales: {");
		AppendNewLine("yAxes: {");
		AppendNewLine("beginAtZero: true");
		AppendNewLine("},");

		AppendNewLine(" xAxes: {");
		AppendNewLine("}");

		AppendNewLine("}");
		AppendNewLine("}");
	}

	private void AppendNewLine(String line) throws IOException {
		myBufferedWriter.write(line);
		myBufferedWriter.newLine();
	}
}