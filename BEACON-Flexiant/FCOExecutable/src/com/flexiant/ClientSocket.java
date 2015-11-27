package com.flexiant;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
			String emailID = args[2];
			LOGGER.log(Level.INFO, "Execuatble has been passed with following args: " 
					+ serverUUID + " " + serverIP + " " + emailID);

			VMDetails details = new VMDetails(serverIP, serverUUID, emailID);
			
			try {
				// Added sleep here till the VM actually starts running
				// However this is not 100% reliable, maybe try SSH the VM TODO
				Thread.sleep(180000); // 3 minutes
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
			sendDataOverSocket(details);
		} else {
			LOGGER.log(Level.SEVERE, "Error - No arguments passed");
		}
	}
	
	static void sendDataOverSocket(VMDetails details) {
		LOGGER.log(Level.FINE, "Attempt to send the file");
		Socket socket = null;
		ObjectOutputStream oos = null;
		
		try {
		    String host = SCANNER_IP;
		    socket = new Socket(host, PORT);
		    oos = new ObjectOutputStream(socket.getOutputStream());
		    oos.writeObject(details);
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

