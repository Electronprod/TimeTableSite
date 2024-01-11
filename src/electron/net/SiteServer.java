package electron.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import electron.console.logger;
import electron.data.HTMLGen;
import electron.data.database;

public class SiteServer {

	public SiteServer(int port) {
		try {
			start(port);
		} catch (NumberFormatException | IOException e) {
			// TODO Автоматически созданный блок catch
			e.printStackTrace();
		}
	}
	private void start(int port) throws NumberFormatException, IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new SiteHandler());
		server.setExecutor(null);	
        server.start();
        logger.log("[SiteServer]: started on "+server.getAddress());
	}
}
class SiteHandler implements HttpHandler {
    public void handle(HttpExchange exchange) {
    	String url = NetUtils.getUrl(exchange).toLowerCase();
    	logger.debug("[SiteServer]: request: "+url);
    	url=url.replaceFirst("/", "");
    	if(url.contains("teacher:")) {
    		teacherRequest(url,exchange);
    		return;
    	}
        if(url.contains("js") || url.contains("css") || url.contains("html") || url.contains("scss")) {
        	//Request type - resources for page from browser
        	NetUtils.sendResponse(exchange,NetUtils.getResource(url),200);
        	return;
        }
    	NetUtils.sendResponse(exchange, HTMLGen.getIndex(), 200);
    }
    private static void teacherRequest(String request,HttpExchange exchange) {
    	logger.debug("[TEACHER_REQUEST]: finding...");
    	//Find teacher name
        String teacher = request.replace("teacher:", "");
        //In URI appears automatically "?". We need to delete it.
        teacher=teacher.replace("?", "");
        //Is teacher exists
        if(!database.get().toString().contains(teacher.toLowerCase())) {
        	//teacher not exists - sending 404 page
        	NetUtils.sendResponse(exchange,NetUtils.getResource("404.html"),404);
        	logger.debug("[TEACHER_REQUEST]: teacher not found");
        	return;
        }
        //teacher exists - sending teacher page
        String anser = HTMLGen.generateTeacher(teacher);
        NetUtils.sendResponse(exchange,anser,200);
        logger.debug("[TEACHER_REQUEST]: teacher found, sent page to remote user.");
    }
}
