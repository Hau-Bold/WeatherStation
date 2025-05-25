package display.Html;

import java.io.BufferedWriter;
import java.io.IOException;

public class CSS {
	private BufferedWriter myBufferedWriter;
	private String myPathToBackgroundImage;

	CSS(BufferedWriter bufferedWriter, String pathToBackgroundImage) {
		myBufferedWriter = bufferedWriter;
		myPathToBackgroundImage = pathToBackgroundImage;
	}

	void forLogo() throws IOException {
		AppendNewLine(".Logo {");
		AppendNewLine("float:left;");
		AppendNewLine("width: 5%;");
		AppendNewLine("display: block;");
		AppendNewLine("}");
		this.myBufferedWriter.newLine();
	}

	void forBody() throws IOException {
		AppendNewLine("body");
		AppendNewLine("{");
		AppendNewLine("min-height: 100%;");
		AppendNewLine("}");
		this.myBufferedWriter.newLine();
	}

	void forHTML() throws IOException {
		AppendNewLine("html");
		AppendNewLine("{");
		AppendNewLine("height: 100%;");
		AppendNewLine("}");
		this.myBufferedWriter.newLine();
	}

	private void AppendNewLine(String line) throws IOException {
		this.myBufferedWriter.write(line);
		this.myBufferedWriter.newLine();
	}

	void forArrowLogo() throws IOException {
		AppendNewLine(".ARROWlogo {");
		AppendNewLine("float:right;");
		AppendNewLine("width: 5%;");
		AppendNewLine("display: block;");
		AppendNewLine("}");
		this.myBufferedWriter.newLine();
	}

	void forBodyId() throws IOException {
		AppendNewLine("#body {");
		AppendNewLine("background-size: 100% 100%;");
		AppendNewLine("text-align: center;");
		AppendNewLine("background-image: url(\" " + this.myPathToBackgroundImage + "\");");

		AppendNewLine("font-family: Roboto, sans-serif;");
		AppendNewLine("background-repeat: no-repeat;");
		AppendNewLine("font-size: 20px;");
		AppendNewLine("margin: 0;");
		AppendNewLine("padding: 0;");
		AppendNewLine("min-height: 100%");
		AppendNewLine("}");
		this.myBufferedWriter.newLine();
	}

	void forH1() throws IOException {
		AppendNewLine("h1 {");
		AppendNewLine(determineColor());
		AppendNewLine("clear: right;");
		AppendNewLine("}");
		this.myBufferedWriter.newLine();
	}

	private String determineColor() {
		StringBuilder builder = new StringBuilder();

		builder.append("color: ");
		builder.append(getCurrentFontColorFromNameOfImage());
		builder.append(";");

		return builder.toString();
	}

	String getCurrentFontColorFromNameOfImage() {
		if (myPathToBackgroundImage.endsWith("_dark")) {
			return "#FFFFFF";
		}

		return "#001F4F";
	}

	void forCurrentDataToDisplay() throws IOException {
		AppendNewLine("#currentDataToDisplay {");

		AppendNewLine(determineColor());
		AppendNewLine("}");
	}

	void forCounter() throws IOException {
		AppendNewLine("#counter {");

		AppendNewLine(determineColor());
		AppendNewLine("}");
	}

	public void forHeadingOfDataToDisplay(String colorOfDataToDisplay) throws IOException {
		AppendNewLine("#headingOfDataToDisplay {");

		AppendNewLine("text-align: center;");
		AppendNewLine("font-family: Roboto, sans-serif;");
		AppendNewLine("color: " + colorOfDataToDisplay + ";");
		AppendNewLine("}");
	}
}