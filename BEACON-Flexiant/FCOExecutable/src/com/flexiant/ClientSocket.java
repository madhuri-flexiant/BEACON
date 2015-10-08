package com.flexiant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
	private static final Logger LOGGER = Logger.getLogger("logger");
	
	private static final String SCANNER_IP = "109.231.126.249";
	private static final int PORT = 8341;

	// Fetch the log configuration
	static{
		try {
			LOG_MANAGER.readConfiguration(new FileInputStream("/home/mramannavar/logging.properties"));
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

			sendDataOverSocket(map);
		} else {
			LOGGER.log(Level.SEVERE, "Error - No arguments passed");
		}
	}
	
	static void sendDataOverSocket(Map<String, String> map) {
		LOGGER.log(Level.FINE, "Attempt to send the file");
		Socket socket = null;
		ObjectOutputStream oos = null;
		
		try {
		    String host = SCANNER_IP;
		    socket = new Socket(host, PORT);
		    oos = new ObjectOutputStream(socket.getOutputStream());
		    oos.writeObject(map);
		    LOGGER.log(Level.INFO, "The data has been sent to the scanner VM");
		
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e );
		
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			    if (socket != null) {
			    	socket.close();
			    }
			} catch (IOException e) {
        		LOGGER.log(Level.SEVERE, "Failed to close connection", e);
			}
		}
	}
}

