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

/**
* This is just a rough draft of the executable jar that will be called by the FCO trigger 
* when a VM is created by a user with certain key.
* 
* Use case yet to be considered - handling multiple creation of VMs
*/
public class ClientSocket {

	private static final String FILE_PATH = "/home/mramannavar/file.txt";
	
	private static final LogManager LOG_MANAGER = LogManager.getLogManager();
	private static final Logger LOGGER = Logger.getLogger("confLogger");
	
	private static final String SCANNER_IP = "109.231.126.199";
	private static final int PORT = 8341;

	// Fetch the log configuration
	static{
		try {
			LOG_MANAGER.readConfiguration(new FileInputStream("src/logging.properties"));
		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, "Error in loading Logger configuration", exception);
		}	
	}

	public static void main(String[] args) {
		LOGGER.log(Level.INFO, "This will be good to see");
		if (args.length > 0) {
			String serverUUID = args[0];
			String serverIP = args[1];
			LOGGER.log(Level.INFO, "Execuatble has been passed with following args: " 
					+ serverUUID + " and " + serverIP);
			Map<String, String> map = new HashMap<String, String>();
			map.put("ServerUUID", serverUUID);
			map.put("IP", serverIP);
			writeToFile(map);
			sendFileOverSocket();
		} else {
			LOGGER.log(Level.SEVERE, "Error - No arguments passed");
		}
	}
	
	static void sendFileOverSocket() {
		LOGGER.log(Level.FINE, "Attempt to send the file");
		try {
			Socket socket = null;
		    String host = SCANNER_IP;
		    socket = new Socket(host, PORT);

		    byte[] bytes = new byte[16 * 1024];
		    InputStream inStream = new FileInputStream(FILE_PATH);
		    OutputStream outStream = socket.getOutputStream();
		
		    int count;
		    while ((count = inStream.read(bytes)) > 0) {
		        outStream.write(bytes, 0, count);
		    }
		    LOGGER.log(Level.INFO, "The file has been sent to the scanner VM");
		    outStream.close();
		    inStream.close();
		    socket.close();
	
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e );
		}
	}
	
	static synchronized void writeToFile(Map<String, String> content) {
		BufferedWriter writer = null;
		
		try {
			LOGGER.log(Level.FINE, "Attempt to write to file");
			File file = new File(FILE_PATH);
			writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			for (Map.Entry<String, String> entry : content.entrySet()) {
				writer.write(entry.getKey());
				writer.write(":");
				writer.write(entry.getValue());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e );
		}
	}
}

