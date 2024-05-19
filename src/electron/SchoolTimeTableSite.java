package electron;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

import electron.console.ConsoleCommandsExecutor;
import electron.console.logger;
import electron.data.SimpleTimeTableGen;
import electron.data.TimeTableGen;
import electron.data.config;
import electron.data.database;
import electron.net.APIServer;
import electron.net.RemoteUploader;
import electron.net.SiteServer;
import library.electron.updatelib.ActionListener;
import library.electron.updatelib.UpdateLib;

public class SchoolTimeTableSite {
	static final Double version = 1.5;
	public static void main(String[] args) throws MalformedURLException {
		AnsiConsole.systemInstall();
		logger.log("Loading TimeTableSite v"+version);
		//Parsing arguments
        Map<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    arguments.put(args[i].substring(1), args[i + 1]);
                    i++;
                } else {
                    arguments.put(args[i].substring(1), null);
                }
            }
        }
        if(arguments.containsKey("debug")) {
        	logger.enDebug=true;
        	logger.debug("DEBUG enabled.");
		}
		//Checking updates
		checkUpdates();
		//Resources loading
		database.load();
		config.load();
		//Starting console
		Thread consoleThread = new ConsoleCommandsExecutor();
		consoleThread.start();
		//Starting network functions
		if(Boolean.parseBoolean(String.valueOf(config.getAPISettings().get("enabled")))) {
			new APIServer(Integer.parseInt(String.valueOf(config.getAPISettings().get("port"))));
		}
		if(Boolean.parseBoolean(String.valueOf(config.getSiteSettings().get("enabled")))) {
			//Loading HTML generators.
			TimeTableGen.load();
			SimpleTimeTableGen.load();
			new SiteServer(Integer.parseInt(String.valueOf(config.getSiteSettings().get("port"))));
		}
		if(Boolean.parseBoolean(String.valueOf(config.getRemoteUploaderSettings().get("enabled")))) {
			new RemoteUploader(Integer.parseInt(String.valueOf(config.getRemoteUploaderSettings().get("port"))));
		}
	}
	public static void checkUpdates() throws MalformedURLException {
		UpdateLib updater = new UpdateLib("https://api.github.com/repos/Electronprod/TimeTableSite/releases");
		updater.setActionListener(new ActionListener() {
            @Override
            public void reveivedUpdates() {
            	String versionobj = updater.getLastVersionJSON();
            	String lastversion = updater.getTagName(versionobj);
            	double newversion = Double.parseDouble(lastversion.replace("v", ""));
            	if(newversion>version) {
            		logger.warn("-----------------[Update]-----------------");
            		logger.warn("New version available: "+lastversion);
            		logger.warn("");
            		logger.warn(updater.getBody(versionobj));
            		logger.warn("");
            		logger.warn("Publish date: "+updater.getPublishDate(versionobj));
            		logger.warn("");
            		logger.warn("Download it:");
            		logger.warn(updater.getReleaseUrl(versionobj));
            		logger.warn("-----------------[Update]-----------------");
            	}else {
            		logger.log("[Updater] it's latest version.");
            	}
            }

			@Override
			public void updateFailed() {
				logger.warn("[Updater] error checking updates.");
			}
        });
	}

}
