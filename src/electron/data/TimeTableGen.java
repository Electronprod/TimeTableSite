package electron.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import electron.console.logger;

public class TimeTableGen {
	private static String lasttime;
	private static int counter;
	private static String index="";
	private static List<String> teachers  = new ArrayList();
	public static void load() {
		if(checkFiles()) {
			try {
				index = generatePage();
				teachers=SimpleTimeTableGen.removeDuplicates(teachers);
				logger.debug("[TimeTableGen] generated page.");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}else {
			logger.error("[TimeTableGen]: required files missing. Check your installation.");
			System.exit(1);
		}
	}
	public static String getIndex() {
		return index;
	}
	public static List<String> getTeachers(){
		return teachers;
	}
	private static String generatePage() throws IOException {
		String index = FileOptions.getFileLine(new File("index.html"));
		String gen="";
		for(int i=0;i<database.getClasses().size();i++) {
			gen=gen+"<div>";
			gen=gen+generateTable(database.getClasses().get(i));
			gen=gen+"</div>";
		}
		index = index.replace("%body%", gen);
		index = index.replace("%viewlink%", "simpleview");
		index = index.replaceAll("null", "");
		return index;
	}
	private static String generateTable(String classname) throws IOException {
		String table = FileOptions.getFileLine(new File("table.html"));
		table=table.replace("%classname%", classname);
		table=table.replace("%monday%", generateLessons(1,classname));
		table=table.replace("%tuesday%", generateLessons(2,classname));
		table=table.replace("%wednesday%", generateLessons(3,classname));
		table=table.replace("%thursday%", generateLessons(4,classname));
		table=table.replace("%friday%", generateLessons(5,classname));
		table=table.replace("%saturday%", generateLessons(6,classname));
		table=table.replace("%sunday%", generateLessons(7,classname));
		return table;		
	}
	
	private static String generateLessons(int dayID,String classname) {
		JSONArray lessonsarr = database.getDay(dayID, classname.toLowerCase());
		if(lessonsarr == null) {
			return "";
		}
		String lessons=null;
		counter=0;
		lasttime="";
		for(int i=0;i<lessonsarr.size();i++) {
			lessons=lessons+generateLesson((JSONObject) lessonsarr.get(i),i+1);
		}
		if(lessons==null) {return "";}
		return lessons;
	}
	
	private static String generateLesson(JSONObject lesson,int num) {
		String time = String.valueOf(lesson.get("time"));
		if(time.equals(lasttime)) {
			counter++;
		}else {
			lasttime=time;
		}
		num=num-counter;
		String name = String.valueOf(lesson.get("lesson"));
		String teacher = String.valueOf(lesson.get("teacher"));
		//For search system
		teachers.add(teacher);
		String btn="<form action=\"/teacher:"+teacher+" \">\r\n"
				+ "            <button  class=\"btn\" type=\"submit\">"+database.getNormalName(teacher)+"</button>\r\n"
				+ "        </form>";
		return "<tr><th>"+num+"</th><th>"+time+"</th><th>"+name+"</th><th>"+btn+"</th></tr>";
	}
	private static boolean checkFiles() {
		if(!new File("index.html").exists()) {
			return false;
		}
		if(!new File("table.html").exists()) {
			return false;
		}
		if(!new File("teacher.html").exists()) {
			return false;
		}
		if(!new File("404.html").exists()) {
			return false;
		}
		if(!new File("lessonstable.html").exists()) {
			return false;
		}
		return true;
	}
	public static String generateTeacher(String teacher) {
		//Getting teacher page template
		String teacherpage = FileOptions.getFileLine(new File("teacher.html"));
		//Adding all lessons
		//Checking all days
		for(int dayID=1;dayID<=7;dayID++) {
			String HTML = "";
			JSONArray arr = database.getDayLessonsTeacher(dayID, teacher);
			//Generating HTML
			for(Object obj : arr) {
				JSONObject lesson = (JSONObject) obj;
				HTML=HTML+"<tr><th>"+String.valueOf(lesson.get("time"))+"</th><th>"+String.valueOf(lesson.get("lesson"))+"</th><th>"+String.valueOf(lesson.get("class"))+"</th></tr>";
			}
			//Adding it to template
			if(HTML!="") {
			teacherpage = teacherpage.replaceAll(getPlaceholderByDayID(dayID), HTML);
			}else {
				teacherpage = teacherpage.replaceAll(getPlaceholderByDayID(dayID), "<th colspan=\"3\">There aren't lessons</th>");
			}
		}
		teacherpage = teacherpage.replaceAll("%teacher%", database.getNormalName(teacher));
		return teacherpage;
	}

	private static String getPlaceholderByDayID(int dayID) {
	switch(dayID) {
		case 1:
			return "%monday%";
		case 2:
			return "%tuesday%";
		case 3:
			return "%wednesday%";
		case 4:
			return "%thursday%";
		case 5:
			return "%friday%";
		case 6:
			return "%saturday%";
		case 7:
			return "%sunday%";
		default:
			return String.valueOf(dayID);
	}
	}
}
