package electron.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import electron.console.logger;

public class database {
	//Configuration file object
	private static File confFile = new File("config.json");
	private static JSONObject config;
	/**
	 * Load method.
	 * Must be called before using other methods from this class.
	 */
	public static void load() {
		logger.debug("[RESOURCE_SYSTEM]: loading database...");
		FileOptions.loadFile(confFile);
	    if (FileOptions.getFileLines(confFile.getPath().toString()).isEmpty()) {
	    	System.err.println("[RESOURCE_SYSTEM]: database isn't configured. Please, use configurator application.");
	    	confFile.delete();
	    	System.exit(0);
	    }
	    //Loading all file data in memory for speed
	    config = (JSONObject) FileOptions.ParseJs(FileOptions.getFileLine(confFile));
	    //Checking config for errors
	    if(Integer.parseInt(String.valueOf(getSiteSettings().get("port"))) == Integer.parseInt(String.valueOf(getApiSettings().get("port")))) {
	    	if(Boolean.parseBoolean(String.valueOf(getSiteSettings().get("enabled"))) & Boolean.parseBoolean(String.valueOf(getApiSettings().get("port")))) {
	    		logger.error("[RESOURCE_SYSTEM]: incorrect data loaded from config: API port equals SITE port.");
	    		System.exit(1);
	    	}
	    	logger.debug("[RESOURCE_SYSTEM]: API port = SITE port, but one of them disabled.");
	    }
	    logger.debug("RESOURCE_SYSTEM]: done.");
	}
	/**
	 * Get SITE settings
	 * @return JSONObject
	 */
	public static JSONObject getSiteSettings() {
		if(!config.containsKey("site")) {
			logger.error("[RESOURCE_SYSTEM]: error loading data from config: site section missing.");
			System.exit(1);
		}
		return (JSONObject) config.get("site");
	}
	/**
	 * Get API settings
	 * @return JSONObject
	 */
	public static JSONObject getApiSettings() {
		if(!config.containsKey("api")) {
			logger.error("[RESOURCE_SYSTEM]: error loading data from config: api section missing.");
			System.exit(1);
		}
		return (JSONObject) config.get("api");
	}
	/**
	 * Get all classes
	 * @return List<String>
	 */
	public static List<String> getClasses(){
		Set<String> keys = new HashSet();
		keys.addAll(config.keySet());
		keys.remove("site");
		keys.remove("api");
		List<String> classes = new ArrayList();
		classes.addAll(keys);
		return classes;
	}
	/**
	 * Get all classes
	 * @return JSONAray
	 */
	public static JSONArray getJSONClasses() {
		JSONArray arr = new JSONArray();
		arr.addAll(getClasses());
		return arr;
	}
	/**
	 * Get all data for day
	 * @param int day
	 * @param String classname
	 * @return JSONArray lessons
	 */
	public static JSONArray getDay(int day,String classname) {
		JSONObject days =(JSONObject) config.get(classname.toLowerCase());
		JSONArray result = JSONSort.sort((JSONArray) days.get(String.valueOf(day)));
		return result;
	}
	/**
	 * Get all config data
	 * @return JSONObject
	 */
	public static JSONObject get() {
		return config;
	}
	/**
	 * Get teacher's lessons for day and class
	 * @param teacher -teacher name
	 * @param dayID - day ID (1-7)
	 * @param classname
	 * @return JSONArray teacherlessons
	 */
	public static JSONArray getTeacherLessons(String teacher,int dayID,String classname) {
				JSONArray arr = new JSONArray();
				//Parsing all lessons
				JSONArray lessons = getDay(dayID, classname);
				//Checking for teacher in this day
				if(!lessons.toString().toLowerCase().contains(teacher.toLowerCase())) {return null;}
				//Checking lessons for teacher
				for(int lessonID = 0;lessonID<lessons.size();lessonID++) {
					JSONObject lesson = (JSONObject) lessons.get(lessonID);
					if(!lesson.containsValue(teacher.toLowerCase())) {continue;}
					arr.add(lesson);
				}
		return arr;
	}
	/**
	 * Bring the name back to normal
	 * @param name (for example "alexander d.")
	 * @return formatted name (example - "Alexander D.")
	 */
	public static String getNormalName(String name) {
		String[] words = name.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        
        for (String word : words) {
            stringBuilder.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1))
                         .append(" ");
        }
        return stringBuilder.toString().trim();
	}
	/**
	 * Get teacher's lessons for day
	 * @param dayID
	 * @param teacher
	 * @return JSONArray lessons
	 */
	public static JSONArray getDayLessonsTeacher(int dayID,String teacher) {
		JSONArray arr = new JSONArray();
		//Checking all classes
		for(String classname : database.getClasses()) {
			//Parsing all lessons
			JSONArray lessons = database.getDay(dayID, classname);
			//Checking for teacher in this day
			if(!lessons.toString().toLowerCase().contains(teacher.toLowerCase())) {
				continue;
			}
			//Adding all lessons to array
			for(Object obj : database.getTeacherLessons(teacher, dayID, classname)) {
				JSONObject lesson = (JSONObject) obj;
				lesson.put("class", classname);
				arr.add(lesson);
			}
		}
		return JSONSort.sort(arr);
	}
}
