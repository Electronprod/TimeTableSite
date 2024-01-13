package electron.data;

import java.util.List;

public class Search {
	/**
	 * @author OpenAI ChatGPT 3.5
	 */
	private static int findLCSLength(String str1, String str2) {
	    int m = str1.length();
	    int n = str2.length();
	    int[][] dp = new int[m + 1][n + 1];

	    for (int i = 0; i <= m; i++) {
	        for (int j = 0; j <= n; j++) {
	            if (i == 0 || j == 0) {
	                dp[i][j] = 0;
	            } else if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
	                dp[i][j] = dp[i - 1][j - 1] + 1;
	            } else {
	                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
	            }
	        }
	    }
	    return dp[m][n];
	}
	/**
	 * @author OpenAI ChatGPT 3.5
	 */
	public static String findMostSimilarString(String input, List<String> stringList) {
	    String mostSimilarString = "";
	    int maxLCSLength = 0;

	    for (String str : stringList) {
	        int lcsLength = findLCSLength(input, str);
	        if (lcsLength > maxLCSLength) {
	            maxLCSLength = lcsLength;
	            mostSimilarString = str;
	        }
	    }
	    return mostSimilarString;
	}
}
