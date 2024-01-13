package electron.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

import org.json.simple.JSONObject;

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
	        while (true) {
	        	LockSupport.parkNanos(100);
	        	logger.log("[RemoteUploader]: opened port "+port);
	            Socket fromClientSocket = servSocket.accept();
	            logger.log("[RemoteUploader]: Connected to "+ fromClientSocket.getRemoteSocketAddress());
	            Thread t = new userThread(fromClientSocket);
	            t.start();
	            t.join();
	            fromClientSocket.close();
	            logger.log("[RemoteUploader]:  Disconnected from remote client.");
	        }
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
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
				logger.error("[RemoteUploader]: incorrect login data from "+client.getInetAddress());
				sendData("0");
				client.close();return;}
			sendData("1");
			DataInputStream in = new DataInputStream(this.client.getInputStream());
		    String str = in.readUTF();
		    JSONObject indata = (JSONObject) FileOptions.ParseJs(str);
		    String data = String.valueOf(indata.get("data"));
		    database.write(data);
		    logger.log("[RemoteUploader]: wrote: "+data);
		    database.load();
		    TimeTableGen.load();
		    SimpleTimeTableGen.load();
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
		    isConnected();
		    logger.debug("[RemoteUploader]: login & pass received: "+str);
		    JSONObject info = (JSONObject) FileOptions.ParseJs(str);
		    String login = (String) info.get("login");
		    String password = (String) info.get("password");
		    String correctLogin = String.valueOf(config.getRemoteUploaderSettings().get("login"));
		    String correctPassword = ""+ String.valueOf(config.getRemoteUploaderSettings().get("password")).hashCode();
		    if(correctLogin.equals(login)) {
		    	if(correctPassword.equals(password)) {
		    		return true;
		    	}
		    }
		    return false;
	}
	private void isConnected() throws IOException {
		if (!this.client.isConnected()) {
	        this.client.close();
	        Thread.currentThread().stop();
	      } 
	}
}
