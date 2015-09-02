package com.flexiant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ClientSocket {

	/**
	 * Currently consider just one file - later we can decide whether we need to create a different file 
	 */
	private static final String filePath = "/home/mramannavar/file.txt";
	
	private static final LogManager logManager = LogManager.getLogManager();
	private static final Logger logger = Logger.getLogger("confLogger");
	static{
		try {
			logManager.readConfiguration(new FileInputStream("src/logging.properties"));
		} catch (IOException exception) {
			logger.log(Level.SEVERE, "Error in loading configuration", exception);
		}
	}
	
	public static void main(String[] args) {
		if (args.length > 0) {
			String serverUUID = args[0];
			String serverIP = args[1];
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("ServerUUID", serverUUID);
			map.put("IP", serverIP);
			
			writeToFile(map);
			sendFileOverSocket();
		} else {
			logger.log(Level.SEVERE, "Error - No arguments passed");
		}
	}
	
	static void sendFileOverSocket() {
		logger.log(Level.FINEST, "Attempt to send the file");
		try {
			Socket socket = null;
		    String host = "109.231.126.199";
		    socket = new Socket(host, 8341);
		    
		    byte[] bytes = new byte[16 * 1024];
		    InputStream in = new FileInputStream(filePath);
		    OutputStream out = socket.getOutputStream();

		    int count;
		    while ((count = in.read(bytes)) > 0) {
		        out.write(bytes, 0, count);
		    }
		    logger.log(Level.INFO, "The file has been sent now");
		    out.close();
		    in.close();
		    socket.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e );
		}
	}
	
	static synchronized void writeToFile(Map<String, String> content) {
		BufferedWriter writer = null;
		try {
			logger.log(Level.FINE, "Attempt to write to file");
			File file = new File(filePath);
			writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			
			for (Map.Entry<String, String> entry : content.entrySet()) {
				writer.write(entry.getKey());
				writer.write(":");
				writer.write(entry.getValue());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e );
		}
	}

}
