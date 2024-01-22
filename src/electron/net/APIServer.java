package electron.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import electron.console.logger;
import electron.data.database;

public class APIServer {

	public APIServer(int port) {
		try {
			start(port);
		} catch (NumberFormatException | IOException e) {
			logger.error("[APIServer]: error message: "+e.getMessage());
			logger.error("[PROGRAM]: ApiServer has crashed. Program is shutting down.");
			System.exit(1);
		}
	}
	private void start(int port) throws NumberFormatException, IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new ApiHandler());
		server.setExecutor(null);	
        server.start();
        logger.log("[APIServer]: started on "+server.getAddress());
	}
}

class ApiHandler implements HttpHandler {
    public void handle(HttpExchange exchange) {
    	//Parsing request
    	String url = NetUtils.getUrl(exchange).toLowerCase();
    	logger.debug("[APIServer]: request from "+exchange.getRemoteAddress()+": "+url);
    	url=url.replaceFirst("/", "");
    	url=url.toLowerCase();
    	if(url.contains("favicon.ico")) {return;}
    	//Commands with arguments
    	if(url.contains("/")) {
    		String[] multirequest = url.split("/");
    		switch(multirequest[0]) {
    		case "getday":
    			NetUtils.sendResponse(exchange, genHTML(database.getDay(Integer.parseInt(multirequest[1]), multirequest[2]).toJSONString()), 200);
    		case "getdayraw":
    			NetUtils.sendResponse(exchange, database.getDay(Integer.parseInt(multirequest[1]), multirequest[2]).toJSONString(), 200);
    		case "getteacherlessonsraw":
    			NetUtils.sendResponse(exchange, database.getTeacherLessons(multirequest[3], Integer.parseInt(multirequest[1]), multirequest[2]).toJSONString(), 200);
    		case "getdaylessonsteacherraw":
    			NetUtils.sendResponse(exchange, database.getDayLessonsTeacher(Integer.parseInt(multirequest[1]), multirequest[2]).toJSONString(), 200);
    		case "getteacherlessons":
    			NetUtils.sendResponse(exchange, genHTML(database.getTeacherLessons(multirequest[3], Integer.parseInt(multirequest[1]), multirequest[2]).toJSONString()), 200);
    		case "getdaylessonsteacher":
    			NetUtils.sendResponse(exchange, genHTML(database.getDayLessonsTeacher(Integer.parseInt(multirequest[1]), multirequest[2]).toJSONString()), 200);
    		default:
    			NetUtils.sendResponse(exchange, "Unknown command.", 200);
    		}
    		return;
    	}
    	//Commands without arguments
    	switch(url) {
    	case "getclasses":
    		NetUtils.sendResponse(exchange, genHTML(database.getJSONClasses().toJSONString()), 200);
    	case "get":
    		NetUtils.sendResponse(exchange, genHTML(database.get().toJSONString()), 200);
    	case "getraw":
    		NetUtils.sendResponse(exchange, database.get().toJSONString(), 200);
    	case "getclassesraw":
    		NetUtils.sendResponse(exchange, database.getJSONClasses().toJSONString(), 200);
    	default:
    		NetUtils.sendResponse(exchange, "Unknown command.", 200);
    	}
    }
    private String genHTML(String data) {
    	String page = "<!DOCTYPE html>\r\n"
    			+ "<html lang=\"en\">\r\n"
    			+ "<head>\r\n"
    			+ "    <meta charset=\"UTF-8\">\r\n"
    			+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
    			+ "    <title>API</title>\r\n"
    			+ "</head><body><p>"+data+"</p></body></html>";
    	return page;
    }
}