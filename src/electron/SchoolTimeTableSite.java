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
		logger.enDebug=false;
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
