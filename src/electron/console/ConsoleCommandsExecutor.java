package electron.console;

import java.net.MalformedURLException;
import java.util.Scanner;

import electron.SchoolTimeTableSite;
import electron.data.SimpleTimeTableGen;
import electron.data.TimeTableGen;
import electron.data.config;
import electron.data.database;
import electron.net.RemoteUploader;

public class ConsoleCommandsExecutor extends Thread{

	public ConsoleCommandsExecutor() {}
	public void run() {
		while(true) {
			Scanner sc = new Scanner(System.in);
			String command = sc.nextLine();
			logger.debug("[CCExecutor]: executing command: "+command);
			if(command.contains(" ")) {
				logger.debug("[CCExecutor]: defined as command with args.");
			}else {
				logger.debug("[CCExecutor]: defined as command without args.");
				command = command.toLowerCase();
				switch(command) {
				case "help":
					logger.cmd("------------------[HELP]------------------");
					logger.cmd("==> 'help' - show this message");
					logger.cmd("==> 'debug' - toggle debug mode");
					logger.cmd("==> 'remoteuploader' - start RemoteUploader's thread");
					logger.cmd("==> 'updates' - check for updates");
					logger.cmd("==> 'reload' - reload all resources");
					logger.cmd("==> 'exit' - exit the program");
					logger.cmd("------------------[HELP]------------------");
					continue;
				case "debug":
					logger.enDebug=!logger.enDebug;
					logger.cmd("==> Toggled debug's state to "+logger.enDebug);
					continue;
				case "remoteuploader":
					new RemoteUploader(Integer.parseInt(String.valueOf(config.getRemoteUploaderSettings().get("port"))));
					logger.cmd("==> Started RemoteUploader.");
					continue;
				case "updates":
					try {
						SchoolTimeTableSite.checkUpdates();
						logger.cmd("==> Updates requested.");
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					continue;
				case "reload":
					database.load();
					config.load();
				    if(Boolean.parseBoolean(String.valueOf(config.getSiteSettings().get("enabled")))) {
				    TimeTableGen.load();
				    SimpleTimeTableGen.load();
				    }
				    logger.cmd("==> Done.");
				    continue;
				case "exit":
					logger.cmd("Thanks for using Electron_prod's software. Bye.");
					System.exit(0);
				default:
				}
			}
			logger.cmd("==> Command not found. Use 'help' for help.");
		}
	}
	private static boolean isMultiCommand(String commandname,String command) {
		if(!command.contains(" ")) {return false;}
		if(!command.contains(commandname)) {return false;}
		if(!command.contains(commandname+" ")) {return false;}
		return true;
	}
	private static String[] getCommandArgs(String in) {
		String[] spl = in.split(" ");
		return spl;
	}
}
