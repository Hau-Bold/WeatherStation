package display.Html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dht22_bmp180.services.OpenWeatherService;

public class HtmlExecutorOverviewImpl implements HtmlExecutor {
	private BufferedWriter myBufferedWriter;
	private CSS myCSS;
	private LocalDate localDate;
	private int myDayOfMonth;
	private String myMonth;
	private int myYear;
	private String myDayOfWeek;
	private String myNameOfdataToDisplay;
	private String myColorOfDataToDisplayOut;
	private String myColorOfDataToDisplayIn;
	private List<JSData> myJSDatasOut = null;
	private List<JSData> myJSDatasIn = null;
	private Canvas myCanvas;

	public HtmlExecutorOverviewImpl(Path path, String pathToBackgroundImage, List<JSData> jsDatasOut,
			List<JSData> jsDatasIn, Date date, String nameOfdataToDisplay, String colorOfDataToDisplayOut,
			String colorOfDataToDisplayIn) throws IOException {
		localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		myDayOfMonth = localDate.getDayOfMonth();
		myYear = localDate.getYear();
		myMonth = localDate.getMonth().toString();
		myDayOfWeek = localDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH));

		myJSDatasOut = jsDatasOut;
		myJSDatasIn = jsDatasIn;

		myNameOfdataToDisplay = nameOfdataToDisplay;
		myColorOfDataToDisplayOut = colorOfDataToDisplayOut;
		myColorOfDataToDisplayIn = colorOfDataToDisplayIn;

		File file = path.toFile();
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		}

		myBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		myCSS = new CSS(myBufferedWriter, pathToBackgroundImage);

		myCanvas = new Canvas(myBufferedWriter, myColorOfDataToDisplayOut, myColorOfDataToDisplayIn,
				myCSS.getCurrentFontColorFromNameOfImage(), myJSDatasOut, myJSDatasIn);
	}

	private void writeHead() throws IOException {
		AppendNewLine("<!doctype html>");
		AppendNewLine("<html>");
		AppendNewLine("<head>");

		AppendNewLine("<script src=\"https://cdn.jsdelivr.net/npm/chart.js@3.5.0/dist/chart.min.js\"></script>");

		AppendNewLine("<link href=\"");

		AppendNewLine("Assets/Font/font.css\"");

		AppendNewLine(" rel='stylesheet' type='text/css'>");
		AppendNewLine("<meta charset=\"UTF-8\">");
		AppendNewLine("<title>WeatherStation</title>");
	}

	private void writeBody() throws IOException {
		AppendNewLine("<body id=\"body\">");
		AppendNewLine("<img src=\"");
		AppendNewLine("Images/Alma.jpeg\"");
		AppendNewLine(" class=\"Logo\" alt = \"Logo_blank\" align=\"left\"/>");
		AppendNewLine("</br>");
		AppendNewLine("</br>");
		AppendNewLine("<h1>");
		AppendNewLine(String.format("%s, the %sth %s of %s",
				new Object[] { myDayOfWeek, Integer.valueOf(myDayOfMonth), myMonth, Integer.valueOf(myYear) }));
		AppendNewLine("</h1>");
		writeCounter();

		AppendNewLine("<h1 id=\"headingOfDataToDisplay\">");
		AppendNewLine(myNameOfdataToDisplay);
		AppendNewLine("</br>");

		writeCurrentWeatherData();

		AppendNewLine("</h1>");

		AppendNewLine("</br>");
		AppendNewLine("</br>");

		myCanvas.write();

		myBufferedWriter.newLine();

		AppendNewLine("</body>");
		AppendNewLine("</html>");
	}

	private void writeCounter() throws IOException {
		AppendNewLine("<div id=\"counter\">");
		AppendNewLine("<small>");
		AppendNewLine("New data in <span id=\"minutes\">" + getMinutesFormatted() + "</span> :");
		AppendNewLine("<span id=\"seconds\">" + getSecondsFormatted() + "</span>");
		AppendNewLine("</small>");
		AppendNewLine("</div>");
	}

	private void writeStyle() throws IOException {
		AppendNewLine("<style>");
		myCSS.forHTML();
		myCSS.forBody();

		myCSS.forH1();
		myCSS.forCounter();

		myCSS.forLogo();
		myCSS.forArrowLogo();

		myCSS.forBodyId();

		myCSS.forHeadingOfDataToDisplay(this.myColorOfDataToDisplayOut);

		myCSS.forCurrentDataToDisplay();

		AppendNewLine("#tableWrapperId {");
		AppendNewLine("}");
		myBufferedWriter.newLine();

		AppendNewLine("#tableId {");
		AppendNewLine("text-align: center;");
		AppendNewLine("width:80%;");
		AppendNewLine("height:80%;");
		AppendNewLine("table-spacing: 2px;");
		AppendNewLine("}");
		myBufferedWriter.newLine();

		AppendNewLine(".tableheader {");
		AppendNewLine("}");
		myBufferedWriter.newLine();

		AppendNewLine(".tableRow {");

		AppendNewLine("}");
		myBufferedWriter.newLine();

		AppendNewLine("tr:nth-child(even) {");
		AppendNewLine("background-color:#FFFFFF;");
		AppendNewLine("}");
		myBufferedWriter.newLine();

		AppendNewLine("tr:nth-child(odd) {");
		AppendNewLine("background-color:#F0F0F0;");
		AppendNewLine("}");
		myBufferedWriter.newLine();

		AppendNewLine(".tableDataTime {");
		AppendNewLine("text-align: center;");
		AppendNewLine("width: 5%;");
		myBufferedWriter.newLine();
		AppendNewLine("border: 2px solid black;");
		AppendNewLine("}");
		myBufferedWriter.newLine();

		AppendNewLine(".tableData {");
		AppendNewLine("text-align: center;");
		AppendNewLine("width: 35%;");
		AppendNewLine("border: 2px solid black;");
		AppendNewLine("filter:alpha(opacity=90);");
		AppendNewLine("-moz-opacity: 0.90;");
		AppendNewLine("opacity: 0.90;");
		AppendNewLine("}");
		myBufferedWriter.newLine();

		AppendNewLine(".appointmentNotEmpty {");
		AppendNewLine("background-color: #F86A6A;");
		AppendNewLine("text-align: left;");
		AppendNewLine("padding: 6px;");
		AppendNewLine("}");
		myBufferedWriter.newLine();

		AppendNewLine("</style>");
		myBufferedWriter.newLine();

		AppendNewLine("</head>");
		myBufferedWriter.newLine();
	}

	private void writeScript() throws IOException {
		AppendNewLine("<script type=\"text/javascript\">");

		AppendNewLine("var timeleft = " + secondsTillNextCall() + ";");
		AppendNewLine("var downloadTimer = setInterval(function(){");
		AppendNewLine("timeleft--;");
		AppendNewLine("var seconds = timeleft % 60;");
		AppendNewLine("var minutes = (timeleft - seconds) / 60;");
		AppendNewLine("document.getElementById(\"minutes\").textContent = minutes;");
		AppendNewLine("document.getElementById(\"seconds\").textContent = seconds;");
		AppendNewLine(" if(timeleft <= 0)");
		AppendNewLine("clearInterval(downloadTimer);");
		AppendNewLine("},1000);");
		AppendNewLine("</script>");
	}

	public void write() throws IOException {
		writeHead();
		writeStyle();
		writeScript();
		writeBody();

		this.myBufferedWriter.close();
	}

	private void AppendNewLine(String line) throws IOException {
		myBufferedWriter.write(line);
		myBufferedWriter.newLine();
	}

	private void writeCurrentWeatherData() throws IOException {
		int countOfEntriesOut = myJSDatasOut.size();
		int countOfEntriesIn = myJSDatasOut.size();

		if (countOfEntriesOut > 0) {

			AppendNewLine("<small id=\"currentDataToDisplay\">");

			JSData current = myJSDatasOut.get(countOfEntriesOut - 1);
			double valueOut = Double.valueOf(current.getValue());

			double valueIn = countOfEntriesIn > 0 ? Double.valueOf(myJSDatasOut.get(countOfEntriesOut - 1).getValue())
					: null;

			AppendNewLine(Helper.getCurrentDataToDisplayString(valueOut, valueIn, myNameOfdataToDisplay));

			if (countOfEntriesOut > 1) {
				JSData predecessor = myJSDatasOut.get(countOfEntriesOut - 2);
				appendTendence(valueOut, Double.valueOf(predecessor.getValue()));
			}

			AppendNewLine("</small>");
		}
	}

	private void appendTendence(double currentValue, double predecessorValue) throws IOException {
		if (predecessorValue != currentValue) {
			boolean isRising = (predecessorValue < currentValue);

			AppendNewLine("<img src=\"");
			AppendNewLine(isRising ? "Images/arrowUp.png\"" : "Images/arrowDown.png\"");
			AppendNewLine("class=\"ARROWlogo\" alt = \"Intersport_Logo_blank\" align=\"right\"/>");
		}
	}

	private int getMinutesFormatted() {
		int delay = secondsTillNextCall();
		return (delay - getSecondsFormatted()) / 60;
	}

	private int getSecondsFormatted() {
		return secondsTillNextCall() % 60;
	}

	private int secondsTillNextCall() {
		long delay = System.currentTimeMillis() - OpenWeatherService.LastCallTime;

		return (int) ((900000L - delay) / 1000L);
	}
}