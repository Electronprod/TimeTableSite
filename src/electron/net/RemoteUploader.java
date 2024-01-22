package electron.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import electron.console.logger;
import electron.data.FileOptions;
import electron.data.SimpleTimeTableGen;
import electron.data.TimeTableGen;
import electron.data.config;
import electron.data.database;

public class RemoteUploader {

	public RemoteUploader(int port) {
		ServerSocket servSocket;
		try {
			servSocket = new ServerSocket(port);
        	logger.log("[RemoteUploader]: opened port "+port);
	        while (true) {
	        	LockSupport.parkNanos(100);
	            Socket fromClientSocket = servSocket.accept();
	            logger.log("[RemoteUploader]: Connected to "+ fromClientSocket.getRemoteSocketAddress());
	            Thread t = new userThread(fromClientSocket);
	            t.start();
	            t.join();
	            fromClientSocket.close();
	            logger.log("[RemoteUploader]: Disconnected from remote client.");
	        }
		} catch (IOException | InterruptedException e) {
			logger.error("[RemoteUploader]: error message: "+e.getMessage());
			logger.warn("[RemoteUploader]: program will continue to work without RemoteUploader function.");
			logger.warn("[RemoteUploader]: you can try to start RemoteUploader function with 'remoteuploader' command.");
		} 
	}

}
class userThread extends Thread{
	Socket client;
	public userThread(Socket client) {
		this.client=client;
	}
	public void run() {
		try {
			if(!logIn()) {
				logger.warn("[RemoteUploader]: incorrect login data from "+client.getInetAddress());
				sendData("0");
				client.close();return;}
			sendData("1");
			DataInputStream in = new DataInputStream(this.client.getInputStream());
		    String str = in.readUTF();
		    try {
				JSONObject indata = (JSONObject) FileOptions.ParseJsThrows(str);
			    String data = String.valueOf(indata.get("data"));
			    database.write(data);
			    logger.log("[RemoteUploader]: wrote: "+data);
			    database.load();
			    if(Boolean.parseBoolean(String.valueOf(config.getSiteSettings().get("enabled")))) {
			    TimeTableGen.load();
			    SimpleTimeTableGen.load();
			    }
			} catch (ParseException e) {
				logger.error("[RemoteUploader]: received data damaged or incorrect.");
				logger.error("[RemoteUploader]: Exeption message: "+e.getMessage());
			}
		    client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void sendData(String command) throws IOException {
		OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.writeUTF(command);
	}
	private boolean logIn() throws IOException {
			DataInputStream in = new DataInputStream(this.client.getInputStream());
		    String str = in.readUTF();
		    logger.debug("[RemoteUploader]: AUTH received: "+str);
		    try {
				JSONObject info = (JSONObject) FileOptions.ParseJsThrows(str);
				String login = (String) info.get("login");
			    String password = (String) info.get("password");
			    String correctLogin = String.valueOf(config.getRemoteUploaderSettings().get("login"));
			    String correctPassword = ""+ String.valueOf(config.getRemoteUploaderSettings().get("password")).hashCode();
			    if(correctLogin.equals(login)) {
			    	if(correctPassword.equals(password)) {
			    		return true;
			    	}
			    }
			} catch (ParseException e) {
				logger.error("[RemoteUploader]: received AUTH data damaged or incorrect.");
				logger.error("[RemoteUploader]: Exeption message: "+e.getMessage());
			}
		    return false;
	}
}
