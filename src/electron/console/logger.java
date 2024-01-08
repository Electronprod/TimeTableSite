package electron.console;

import java.util.Scanner;

public class logger {
	//Enable/Disable debug
	public static boolean enDebug = true;
	
	public static void log(String msg) {
		System.out.println(msg);
	}
	public static void debug(String msg) {
		if(!enDebug) {return;}
		System.out.println("[DEBUG]"+msg);
	}
	public static void error(String msg) {
		System.err.println(msg);
	}
	public static boolean askBool(String msg) {
		Scanner sc = new Scanner(System.in);
		System.out.println(msg);
		String answer = sc.nextLine();
		sc.close();
		if(answer.contains("y")) {
			return true;
		}else{
			return false;
		}
	}
}
