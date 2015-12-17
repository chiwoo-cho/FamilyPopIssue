package com.nclab.sociophone;

public class SocioPhoneConstants {
	public final static int BT_EXCEPTION = 10000;
	public final static int BT_CONNECTED = 10001;
	public final static int BT_DISCONNECTED = 10002;
	
	public final static int BT_ACCEPT = 10004;
	public final static int BT_ = 10005;
	
	public final static int PROCESS_TRAINING_FINISHED = 20000;
	public final static int PROCESS_FINISHED = 20001;
	
	/**
	 * A turn information is received (from server)
	 */
	public final static int SIGNAL_INFORMATION_RECEIVED = 30000;
	/**
	 * Start record signal
	 */
	public final static int SIGNAL_START_RECORD = 30021;
	/**
	 * Stop record signal
	 */
	public final static int SIGNAL_STOP_RECORD = 30022;
	/**
	 * Signal that contains raw data (volume, ...) 
	 */
	public final static int SIGNAL_DATA = 30041;
	/**
	 * Signal that contains time sync data
	 */
	public final static int SIGNAL_TIME_ARRANGEMENT = 30001;
	/**
	 * When the new volume window is ready
	 */
	public final static int SIGNAL_VOLUME_WINDOW = 30020;
	
	public final static int SIGNAL_USER_ID = 30021;
	
	public final static int PARAMETER_LOG = 997;
	public final static int PARAMETER_LOG_MULTI = 999;
	
	public static long deviceTimeOffset = -1;
	
	
	public static int id = 0;
	public static int numberOfPhones = 0;
	public static int windowSize = 300;
	
	public final static String MODEL_FILE_NAME = "SocioPhone_SVM_MODEL";
	
	public final static int DISPLAY_LOG = 998;
	public final static int DISPLAY_VOLUME_STATUS = 900;
	public final static int DISPLAY_VOLUME_STATUS_MULTI = 901;
	
	
	public final static boolean debug = false;
	
}
