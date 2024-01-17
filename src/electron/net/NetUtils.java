package electron.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import electron.console.logger;
import electron.data.FileOptions;
import electron.data.database;

public class NetUtils {
	/**
	 * Get formatted input url
	 * @param exchange
	 * @return url or "/" if error will happen
	 */
	public static String getUrl(HttpExchange exchange) {
		URI uri = exchange.getRequestURI();
        String request = uri.toString();
        try {
        	//Format URI to UTF8
            request = java.net.URLDecoder.decode(request, StandardCharsets.UTF_8.name());
            return request;
        } catch (UnsupportedEncodingException e) {
            // not going to happen - value came from JDK's own StandardCharsets
        	logger.warn("[NetUtils]: Error decoding incoming url.");
        	return "/";
        }
   }
	/**
	 * Send answer to browser
	 * @param exchange - client info
	 * @param response - data to send
	 * @param code - HTTP code
	 * @throws IOException
	 */
	public static void sendResponse(HttpExchange exchange, String response,int code) {
	    try {
			exchange.sendResponseHeaders(code, response.getBytes().length);
		    OutputStream outputStream = exchange.getResponseBody();
		    outputStream.write(response.getBytes());
		    outputStream.close();
	    } catch (IOException e) {
			logger.warn("[NetUtils]: "+e.getMessage());
		}
	}
	/**
	 * Get file data
	 * @param fname - name of file
	 * @return String data
	 */
	public static String getResource(String fname) {
		String r;
		if(!new File(fname).exists()) {
			r = FileOptions.getFileLine(new File("404.html"));
		}else {
		r = FileOptions.getFileLine(new File(fname));
		}
		//r.replace("/", "");
		return r;
	}
	
}