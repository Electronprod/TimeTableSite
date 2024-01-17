package electron.data;

import java.io.File;

import org.json.simple.JSONObject;

import electron.console.logger;

public class config {
	//Configuration file object
	private static File settingsFile = new File("settings.json");
	private static JSONObject settings;
	/**
	 * Load method.
	 * Must be called before using other methods from this class.
	 */
	public static void load() {
		logger.debug("[RESOURCE_SYSTEM]: loading settings...");
		FileOptions.loadFile(settingsFile);
	    if (FileOptions.getFileLines(settingsFile.getPath().toString()).isEmpty()) {
	    	logger.debug("[RESOURCE_SYSTEM]: writing default settings.");
	    	writeDefaultSettings();
	    	logger.warn("[RESOURCE_SYSTEM]: don't forget to configure file settings.json");
	    	return;
	    }
	    //Loading all file data in memory for speed
	    settings = (JSONObject) FileOptions.ParseJs(FileOptions.getFileLine(settingsFile));
	    logger.debug("RESOURCE_SYSTEM]: done.");
	}
	public static void writeDefaultSettings() {
		settings=new JSONObject();
		//API section
		JSONObject apisection = new JSONObject();
		apisection.put("port", "81");
		apisection.put("enabled", "true");
		settings.put("api", apisection);
		//Site section
		JSONObject sitesection = new JSONObject();
		sitesection.put("port", "80");
		sitesection.put("enabled", "true");
		settings.put("site", sitesection);
		//RemoteUploader section
		JSONObject rusection = new JSONObject();
		rusection.put("port", "90");
		rusection.put("enabled", "true");
		rusection.put("login", "electron");
		rusection.put("password", "TimeTableSite");
		settings.put("remote_uploader", rusection);
		FileOptions.writeFile(settings.toJSONString(), settingsFile);
	}
	public static JSONObject getAPISettings() {
		return (JSONObject) settings.get("api");
	}
	public static JSONObject getSiteSettings() {
		return (JSONObject) settings.get("site");
	}
	public static JSONObject getRemoteUploaderSettings() {
		return (JSONObject) settings.get("remote_uploader");
	}
}
