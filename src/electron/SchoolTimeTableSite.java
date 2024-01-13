package electron;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

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
	static final Double version = 1.3;
	public static void main(String[] args) throws MalformedURLException {
		//Program settings loading
		logger.log("Loading TimeTableSite...");
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
		}
		//Checking updates
		checkUpdates();
		//Resources loading
		database.load();
		config.load();
		TimeTableGen.load();
		SimpleTimeTableGen.load();
		//Starting network functions
		if(Boolean.parseBoolean(String.valueOf(config.getAPISettings().get("enabled")))) {
			new APIServer(Integer.parseInt(String.valueOf(config.getAPISettings().get("port"))));
		}
		if(Boolean.parseBoolean(String.valueOf(config.getSiteSettings().get("enabled")))) {
			new SiteServer(Integer.parseInt(String.valueOf(config.getSiteSettings().get("port"))));
		}
		if(Boolean.parseBoolean(String.valueOf(config.getRemoteUploaderSettings().get("enabled")))) {
			new RemoteUploader(Integer.parseInt(String.valueOf(config.getRemoteUploaderSettings().get("port"))));
		}
	}
	private static void checkUpdates() throws MalformedURLException {
		UpdateLib updater = new UpdateLib("https://api.github.com/repos/Electronprod/TimeTableSite/releases");
		updater.setActionListener(new ActionListener() {
            @Override
            public void reveivedUpdates() {
            	String versionobj = updater.getLastVersionJSON();
            	String lastversion = updater.getTagName(versionobj);
            	double newversion = Double.parseDouble(lastversion.replace("v", ""));
            	if(newversion>version) {
            		logger.error("-----------------[Update]-----------------");
            		logger.error("New version available: "+lastversion);
            		logger.error("");
            		logger.error(updater.getBody(versionobj));
            		logger.error("");
            		logger.error("Publish date: "+updater.getPublishDate(versionobj));
            		logger.error("");
            		logger.error("Download it:");
            		logger.error(updater.getReleaseUrl(versionobj));
            		logger.error("-----------------[Update]-----------------");
            	}else {
            		logger.log("[Updater] it's latest version.");
            	}
            }

			@Override
			public void updateFailed() {
				System.err.println("[Updater] error checking updates.");
			}
        });
	}

}
