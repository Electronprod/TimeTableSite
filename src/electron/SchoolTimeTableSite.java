package electron;

import electron.console.logger;
import electron.data.HTMLGen;
import electron.data.database;
import electron.net.APIServer;
import electron.net.SiteServer;

public class SchoolTimeTableSite {

	public static void main(String[] args) {
		//Program settings loading
		logger.log("Loading TimeTableSite...");
		if(args.length>0 && args[0].toLowerCase().equals("-debug")) {
			logger.enDebug=true;
			logger.log("[LOGGER]: debug enabled.");
		}
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

}
