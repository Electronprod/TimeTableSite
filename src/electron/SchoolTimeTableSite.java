package electron;

import java.net.MalformedURLException;

import electron.console.logger;
import electron.data.HTMLGen;
import electron.data.database;
import electron.net.APIServer;
import electron.net.SiteServer;
import library.electron.updatelib.ActionListener;
import library.electron.updatelib.UpdateLib;

public class SchoolTimeTableSite {
	static final Double version = 1.1;
	public static void main(String[] args) throws MalformedURLException {
		//Program settings loading
		logger.log("Loading TimeTableSite...");
		logger.enDebug=false;
		checkUpdates();
		//Resources loading
		database.load();
		HTMLGen.load();
		//Starting site
		if(Boolean.parseBoolean(String.valueOf(database.getApiSettings().get("enabled")))) {
			new APIServer();
		}
		if(Boolean.parseBoolean(String.valueOf(database.getSiteSettings().get("enabled")))) {
			new SiteServer();
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
            		logger.error("Release notes:");
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
