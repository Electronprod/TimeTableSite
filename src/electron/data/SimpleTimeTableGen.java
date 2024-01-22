package electron.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import electron.console.logger;

public class SimpleTimeTableGen {
	private static String index="";
	public static void load() {
		try {
			index = generatePage();
			logger.debug("[SimpleTimeTableGen] generated page.");
		} catch (IOException e) {
			logger.error("[SimpleTimeTableGen] error generating page: "+e.getMessage());
		}
	}
	public static String get() {
		return index;
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
		index = index.replace("%viewlink%", "/");
		index = index.replaceAll("null", "");
		return index;
	}
	private static String generateTable(String classname) throws IOException {
		String table = FileOptions.getFileLine(new File("lessonstable.html"));
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
		Map<String,List<String>> lessons = new HashMap();
		List<String> times = new ArrayList();
		for(int i=0;i<lessonsarr.size();i++) {
			JSONObject lesson = (JSONObject) lessonsarr.get(i);
			String lessonname = String.valueOf(lesson.get("lesson"));
			String time = String.valueOf(lesson.get("time"));
			if(lessons.containsKey(time)) {
				List<String> ls = lessons.get(time);
				ls.add(lessonname);
				lessons.put(time, ls);
			}else {
				List<String> ls = new ArrayList();
				ls.add(lessonname);
				times.add(time);
				lessons.put(time, ls);
			}
		}
		String html = "";
		int num=1;
		Collections.sort(times);
		for(int i = 0;i<times.size();i++) {
			List<String> ls = lessons.get(times.get(i));
			ls=removeDuplicates(ls);
			String lsname="";
			for(int a=0;a<ls.size();a++) {
				if(ls.size()>1) {
					lsname=lsname+ls.get(a)+"/";
					continue;
				}
				lsname=lsname+ls.get(a);
			}
			if(lsname.endsWith("/")) {
			lsname = lsname.substring(0,lsname.length() - 1);
			}
			html = html+"<tr><th>"+num+"</th><th>"+times.get(i)+"</th><th>"+lsname+"</th></tr>";
			num++;
		}
		return html;
	}
	/**
	 * From Project0_Server
	 * Duplicate deleter
	 * @param echos - list with duplicates
	 * @return list without duplicates
	 * @author Electron_prod
	 */
	  public static List<String> removeDuplicates(List<String> echos) {
		    Map<String, Integer> letters = new HashMap<>();
		    for (int i = 0; i < echos.size(); i++) {
		      String tempChar = echos.get(i);
		      if (!letters.containsKey(tempChar)) {
		        letters.put(tempChar, Integer.valueOf(1));
		      } else {
		        letters.put(tempChar, Integer.valueOf(((Integer)letters.get(tempChar)).intValue() + 1));
		      } 
		    } 
		    List<String> ans = new ArrayList<>();
		    for (Map.Entry<String, Integer> entry : letters.entrySet())
		      ans.add(entry.getKey()); 
		    
		    return ans;
		  }
}
