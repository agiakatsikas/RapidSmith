package unipi.sevax.analysis;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	private String fileName;
	private Calendar cal = Calendar.getInstance();
	//private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	
	public Logger(String logName) {
		String startDate = logDateFormat.format(cal.getTime());
		this.fileName = logName + "_" + startDate + ".txt";
	}
	
	public void filePrintln(String str) {
		PrintWriter pw = null;
		try {
			FileWriter fw = new FileWriter(fileName, true);
			pw = new PrintWriter(fw);
			pw.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
	
	public void filePrint(String str) {
		PrintWriter pw = null;
		try {
			FileWriter fw = new FileWriter(fileName, true);
			pw = new PrintWriter(fw);
			pw.print(str);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
}
