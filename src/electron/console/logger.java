package electron.console;

import static org.fusesource.jansi.Ansi.*;

import java.io.File;
import java.util.Date;

import electron.data.FileOptions;

public class logger {
	//Enable/Disable debug
	public static boolean enDebug = false;
	private static Date date = new Date();
	public static void log(Object msg) {
		System.out.println(getTime()+" INFO: "+msg);
	}
	public static void warn(Object msg) {
		System.out.println(ansi().fgYellow().a(getTime()+" WARN: "+msg).reset());
	}
	public static void debug(Object msg) {
		if(!enDebug) {return;}
		System.out.println(ansi().fgGreen().a(getTime()+" DEBUG: "+msg).reset());
	}
	public static void error(Object msg) {
		System.out.println(ansi().fgRed().a(getTime()+" ERROR: "+msg).reset());
	}
	private static String getTime() {
		date.setTime(System.currentTimeMillis());
		return date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
	}
}
