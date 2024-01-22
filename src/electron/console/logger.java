package electron.console;

import static org.fusesource.jansi.Ansi.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import electron.data.FileOptions;

public class logger {
	//Enable/Disable debug
	public static boolean enDebug = false;
	private static Date date = new Date();
	private static final Logger logger = Logger.getLogger("LoggerSystem");
	private static boolean initialized = false;
	
	public static void log(Object msg) {
		initialize();
		System.out.println(getTime()+" INFO: "+msg);
		logger.info(String.valueOf(msg));
	}
	public static void warn(Object msg) {
		initialize();
		System.out.println(ansi().fgYellow().a(getTime()+" WARN: "+msg).reset());
		logger.warning(String.valueOf(msg));
	}
	public static void debug(Object msg) {
		if(!enDebug) {return;}
		initialize();
		System.out.println(ansi().fgGreen().a(getTime()+" DEBUG: "+msg).reset());
		logger.log(Level.FINER, (String.valueOf(msg)));
	}
	public static void error(Object msg) {
		initialize();
		System.out.println(ansi().fgRed().a(getTime()+" ERROR: "+msg).reset());
		logger.severe(String.valueOf(msg));
	}
	public static void cmd(Object msg) {
		initialize();
		System.out.println(ansi().fgBlue().a(getTime()+" CONSOLE: "+msg).reset());
		logger.log(Level.FINE, (String.valueOf(msg)));
	}
	private static String getTime() {
		date.setTime(System.currentTimeMillis());
		return date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
	}
	private static void initialize() {
		if(initialized) {return;}
		FileHandler fileHandler;
		new File("log.txt").delete();
        try {
            fileHandler = new FileHandler("log.txt",false);
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.setUseParentHandlers(false);
            logger.addHandler(fileHandler);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Critical error while starting logger.");
            System.exit(1);
        }
	}
}
