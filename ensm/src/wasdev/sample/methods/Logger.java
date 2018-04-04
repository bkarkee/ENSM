package wasdev.sample.methods;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

public class Logger {
	
	private Logger() { throw new IllegalStateException("Logger class"); }
	
	public static void writeToErrorLogs(String message){
		String filename ="WEB-INF/classes/wasdev/sample/methods/errorLogs";
		File myFile = new File(filename);
		FileWriter fw = null;
		try {
			fw = new FileWriter(myFile,true);
			fw.write("\n");
			fw.write(message);
			fw.write("\n");
			fw.flush();
		} catch (IOException e) {
			System.err.println(e);
		} finally{
			try {
				fw.close();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
	
	public static void writeErrorStackTrace(Exception e){
		String filename ="WEB-INF/classes/wasdev/sample/methods/errorLogs";
		File myFile = new File(filename);
		PrintStream ps = null;
		try {
			ps = new PrintStream(myFile);
			e.printStackTrace(ps);
		} catch (Exception ex) {
		    ex.printStackTrace(ps);
		} finally{
			if(ps != null){
				ps.close();
			}
		}
	}

	public static void writeToInfoLogs(String message){
		String filename ="WEB-INF/classes/wasdev/sample/methods/infoLogs";
		File myFile = new File(filename);
		FileWriter fw = null;
		try {
			fw = new FileWriter(myFile,true);
			fw.write("\n");
			fw.write(message);
			fw.write("\n");
			fw.flush();
		} catch (IOException e) {
			System.err.println(e);
		} finally{
			try {
				fw.close();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
