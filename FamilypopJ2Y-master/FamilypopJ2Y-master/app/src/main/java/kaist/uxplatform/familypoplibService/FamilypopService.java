package kaist.uxplatform.familypoplibService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONException;

import symphonyService.SymphonyService;

import maestro.CCommands;
import maestro.CCoordResult;
import maestro.CCtrlParam;
import maestro.CPlayer;
import maestro.CSignalSensor;
import maestro.CUtils;
import maestro.ClientControlListener;
import maestro.ClientControlSender;
import maestro.ServerConnection;
import maestro.ServerControlListener;
import maestro.ServerControlSender;
import kaist.uxplatform.familypoplibService.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

@SuppressLint({ "HandlerLeak", "SdCardPath" })
public abstract class FamilypopService extends Activity implements ServiceConnection {
	IntentFilter intentfilter = null;
	static Context context;

	private final CPlayer FPserver = new CPlayer("Server");

	// network
	private String ipAddr = null;
	private String serverIpAddr = null;
	private int port = 35500;

	// mode
	private volatile int currentMode;
	private final int server = 4403;
	private final int client = 4406;
	private final int notRunning = -1;

	private ArrayList<CPlayer> pList = new ArrayList<CPlayer>();

	// play
	private SoundPool soundPool = null;
	private int soundId = -1;
	private int mode;

	// control
	private int NTPsyncPlayerCnt;
	private int resultCnt;
	private int idNo;
	private long clockOffset = 0;
	private boolean synched;
	private boolean delayed;

	// Threads
	private Thread serverControlThread = null;
	private ClientControlListener cclThread = null;

	// distance
	private CCoordResult[] prevResult = null;
	private CCoordResult[] curResult = null;
	private double[][] distance;
	private double[][] location;
	protected Coordinate[][] deviceDist;
	protected Coordinate[] deviceLocations;

	// Symphony-related
	public static List<String> registeredContexts = new ArrayList<String>();
	public static List<Integer> maestroQueryIds = new ArrayList<Integer>();
	public static List<Integer> QueryIds = new ArrayList<Integer>();
	
	private int player = 0;
	private List<Integer> peakIdx = new ArrayList<Integer>();
	private volatile boolean symphonyServiceConnected = false;
	private final ReentrantLock symphonyLock = new ReentrantLock();
	private final Condition symphonyConnected = symphonyLock.newCondition();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        soundId = soundPool.load(this, R.raw.imls_10_5, 1);
        
		// Register the broadcast receiver, symphonyReceiver (defined at the bottom)
		if (symphonyReceiver != null) {
			IntentFilter symphonyFilter = new IntentFilter(
					"com.nclab.partitioning.DEFAULT");
			registerReceiver(symphonyReceiver, symphonyFilter);
		}
	}

	private void sendControlInfo(String type, int no, String str) {
		Intent intent = new Intent("kaist.uxplatform.familypoplib");
		intent.putExtra("mode", type);
		intent.putExtra("control", no);
		intent.putExtra("msg", str);
		context.sendBroadcast(intent);
		Log.d("fpLib", type + " sends broadcast: " + str);
	}

	/* ***********************************************
	 * Maestro Server-side communication handler *****
	 * **********************************************
	 */
	public Handler serverHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // Error from everywhere
				sendControlInfo("serverControl", 0, msg.obj.toString());
				resetPlayerList();
				break;

			case 1: // start server from 'ServerControlListener'
				ServerConnection tmpConn = (ServerConnection) msg.obj;
				ipAddr = tmpConn.ipAddr;
				sendControlInfo("serverControl", 0, " - IP Addr: " + ipAddr
						+ " - Port: " + String.valueOf(port));
				
				FPserver.setIpAddr(ipAddr);
				
				break;

			case 2: // add client from ServerControlHandler
				CPlayer player = (CPlayer) msg.obj;
				addPlayerList(player);
				break;

			case 3: // delete client from ServerControlHandler
				/* get client id */
				CPlayer playerBeingDisconnected = getPlayerFromIPAddr(msg.obj.toString());
				if (playerBeingDisconnected != null)
					delPlayerList(playerBeingDisconnected.getPlayerId());
				
				break;

			case 4: // NTP time sync result from ServerControlHandler
				if (NTPsyncPlayerCnt < CCtrlParam.numPlayer)
					doNTPTimesync(NTPsyncPlayerCnt++);
				else {
					synched = true;
					sendControlInfo("serverControl", 1,	"Players are synchronized.\n");
					doServerPlay();
				}

				break;

			case 5: // sensing result from ServerControlHandler
				CCommands distanceCmd = (CCommands) msg.obj;

				if (!delayed) {
					try {
						int playerId = distanceCmd.jsonCmd.getInt("idNo");
						JSONArray idxArray = distanceCmd.jsonCmd.getJSONArray("peakIdx");
	
						curResult[playerId] = new CCoordResult(CCtrlParam.numPlayer);
						for (int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++) {
							curResult[playerId].peakCnt[cnt] = idxArray.getInt(cnt)
									+ (cnt * CCtrlParam.calcMSToSample(CCtrlParam.interval));
							sendControlInfo("serverControl", 10, " (" + playerId
									+ ", " + cnt + "): "
									+ curResult[playerId].peakCnt[cnt] + " ");
						}
						sendControlInfo("serverControl", 10, "\n");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
					resultCnt++;
					calcDistance();
				}
				
				break;

			case 10: // Sensing result from TAllMlsAnalyzer
				curResult[0] = (CCoordResult) msg.obj;
				
				if (!delayed) {
					sendControlInfo("serverControl", 1, " Result\n");
	
					for (int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++) {
						curResult[0].peakCnt[cnt] += (cnt * CCtrlParam.calcMSToSample(CCtrlParam.interval));
						sendControlInfo("sesrverControl", 10, " (0, " + cnt + "): "
								+ curResult[0].peakCnt[cnt] + " ");
					}
					sendControlInfo("serverControl", 10, "\n");
	
					resultCnt++;
					calcDistance();
				}
				
				break;

			case 11: //delayed from TCoordControlHandler or CSingnalSensor
				sendControlInfo("serverControl", 1, " Network delayed!!\n");
				delayed = true;
				
				if(prevResult != null)
				{
					resultCnt = CCtrlParam.numPlayer;
					curResult = prevResult;
					calcDistance();
				}
				
				break;
				
			default:
				break;
			}
		}
	};

	/* ***********************************************
	 * Maestro Client-side communication handler *****
	 * **********************************************
	 */
	public Handler clientHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // error from everywhere
				sendControlInfo("clientControl", 0, null);

				if (cclThread != null) {
					cclThread.interrupt();
					cclThread = null;
				}

				break;

			case 1: // start client from ClientControlHandler
				sendControlInfo("clientControl", 1, " - IP Addr: "
						+ serverIpAddr + "\n - Port: 35500\n - ID: " + idNo);
				break;

			case 2: // start NTP time sync from ClientControlHandler
				NTPTimesync();
				break;

			case 3: // save NTP Offset from ClientControlHandler
				clockOffset = (Long) msg.obj;
				break;

			case 4: // player cmds from ClientControlHandler
				CCommands playerCmd = (CCommands) msg.obj;
				try {
					idNo = playerCmd.jsonCmd.getInt("id");
					mode = playerCmd.jsonCmd.getInt("mode");
					CCtrlParam.numPlayer = playerCmd.jsonCmd
							.getInt("numPlayer");

					doClientPlay(playerCmd.jsonCmd.getLong("starttime")
							- clockOffset);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				break;

			case 10: // Result from TAllMlsAnalyzer
				CCoordResult result = (CCoordResult) msg.obj;

				for(int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++)
		        {
					System.out.println("Jungi: [" + idNo + "] sending arrival time: " + result.peakCnt[cnt]);
		        }
				
				try {
					CCommands distanceCmd = new CCommands();
					distanceCmd.beDistanceCmd(idNo, result);

					ClientControlSender ccsThread = new ClientControlSender(
							clientHandler, FPserver, distanceCmd);
					ccsThread.start();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;

			case 11:
				try {
					CCommands delayedCmd = new CCommands();
					delayedCmd.beDelayedCmd(idNo);
					
			        ClientControlSender ccsThread = new ClientControlSender(clientHandler, FPserver, delayedCmd);
			        ccsThread.start();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
				
			default:
				break;
			}
		}
	};

	public boolean startConversation() throws RemoteException {
		// TODO Auto-generated method stub
		Log.d("FamilyPopLib", "Start symphony service.");

		bindSymphoneyService();

		registeredContexts.clear();
		final String[] queries = { "SPEAKER 10000 16000 6000" };

		registerQuery(queries, QueryIds);
		registeredContexts.add("SPEAKER");

		return false;
	}

	public boolean stopConversation() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	// //////////////////////////////////////////////////////////////////
	// //////////////////// Server-side API //////////////////////////
	// //////////////////////// Start //////////////////////////////
	// //////////////////////////////////////////////////////////////////
	public boolean startServer() {
		Log.d("fpLib", "familypop server starts.");

		/* start familyPop as server mode */
		currentMode = server;

		/* initialize player list */
		initPlayerList();

		try {
			/* start server connection listener thread */
			serverControlThread = new ServerControlListener(serverHandler);
			serverControlThread.start();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean stopServer() {
		Log.d("fpLib", "familypop server stops.");

		/* stop familyPop server */
		currentMode = notRunning;

		CCommands disconnCmd = new CCommands();
		try {
			disconnCmd.beDisconnectStoCCmd();

			for (int cnt = 1; cnt < CCtrlParam.numPlayer; cnt++) {
				/*
				 * send disconnect msg to all connected clients clientHandler of
				 * '0' will handle this
				 */
				ServerControlSender scsThread = new ServerControlSender(
						pList.get(cnt), disconnCmd);
				scsThread.start();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		serverControlThread.interrupt();
		serverControlThread = null;

		return false;
	}

	public boolean initDevicesLocation(int x, int y) {
		return true;
	}
	
	public boolean getDevicesLocation() {
		/* only server does initialize coordinated location detection */
		if (this.currentMode == server)
			doInitMaestro();
		return true;
	}
	
	//abstract public void deviceLocationResult(double distance);
	abstract public void deviceLocationResult(Coordinate[] coordResults);
	
	// ////////////////////////////////////////////////////
	// //////////// Misc. (Server) ////////////////////////
	// ////////////////////////////////////////////////////
	private void doInitMaestro() {
		if (emptyPlayerList())
			return;

		NTPsyncPlayerCnt = 1;
		resultCnt = 0;
		delayed = false;

		if(curResult != null)
			prevResult = curResult;
		
		curResult = new CCoordResult[CCtrlParam.numPlayer];

		if (!synched)
			doNTPTimesync(NTPsyncPlayerCnt++);
		else
			doServerPlay();
	}
	
	private void doServerPlay() {
		long starttime = CUtils.gettime() + CCtrlParam.netDelay;

		for (int cnt = 1; cnt < CCtrlParam.numPlayer; cnt++) {
			CPlayer curPlayer = pList.get(cnt);
			try {
				CCommands playCmd = new CCommands();
				playCmd.bePlayCmd(starttime, cnt, mode, CCtrlParam.numPlayer);

				/* send play command to clients */
				ServerControlSender ccsThread = new ServerControlSender(
						curPlayer, playCmd);
				ccsThread.start();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Register the broadcast receiver, symphonyReceiver (defined at the
		// bottom)
		if (symphonyReceiver != null) {
			IntentFilter symphonyFilter = new IntentFilter(
					"com.nclab.partitioning.DEFAULT");
			registerReceiver(symphonyReceiver, symphonyFilter);
		}

		SymphonyMaestro smThread = new SymphonyMaestro(starttime, server);
		smThread.start();
	}

	private void doNTPTimesync(int cnt) {
		CCommands startNTPTimesyncCmd = new CCommands();
		CPlayer curPlayer = pList.get(cnt);

		try {
			startNTPTimesyncCmd.beStartNTPTimesyncCmd();
			ServerControlSender scsThread = new ServerControlSender(curPlayer,
					startNTPTimesyncCmd);
			scsThread.start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void calcDistance() {
		System.out.println("Jungi: resultCnt - " + resultCnt + " numPlayer - " + CCtrlParam.numPlayer);
		if (resultCnt < CCtrlParam.numPlayer)
			return;

		distance = new double[CCtrlParam.numPlayer][CCtrlParam.numPlayer];
		deviceDist = new Coordinate[CCtrlParam.numPlayer][CCtrlParam.numPlayer];;
		
		CCoordResult res_i, res_j;
		double tmp_i_j, tmp_j_i;
		
		for (int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++) {
			res_i = curResult[cnt];
			for (int cnt2 = 0; cnt2 < CCtrlParam.numPlayer; cnt2++) {
				System.out.println("Ryosu: (" + cnt + ", " + cnt2 + "): "
						+ res_i.peakCnt[cnt2]);

				res_j = curResult[cnt2];

				tmp_i_j = (double) (res_i.peakCnt[cnt2] - res_i.peakCnt[cnt])
						/ (double) CCtrlParam.sampleRate * 1000.f;
				tmp_j_i = (double) (res_j.peakCnt[cnt2] - res_j.peakCnt[cnt])
						/ (double) CCtrlParam.sampleRate * 1000.f;

				distance[cnt][cnt2] = (double) (tmp_i_j - tmp_j_i) / 2.f
						* (double) CCtrlParam.soundCMperMS
						+ (double) CCtrlParam.spkMicCM;
				
				deviceDist[cnt][cnt2] = new Coordinate((int)distance[cnt][cnt2]);
			}
		}

		/*String resultTv = "Distance (cm)\n";

		for (int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++) {
			for (int cnt2 = cnt + 1; cnt2 < CCtrlParam.numPlayer; cnt2++)
				resultTv = resultTv.concat("(" + cnt + ", " + cnt2 + "): "
						+ distance[cnt][cnt2] + "\n");
		}*/
		//sendControlInfo("serverControl", 1, resultTv);
		//deviceLocationResult(deviceDist[0]);
				
		calcLocation();
	}

	private void calcLocation() {		
		location = new double[CCtrlParam.numPlayer][2];
		deviceLocations = new Coordinate[CCtrlParam.numPlayer];

		location[0][0] = location[0][1] = 0; // server is located on (0,0)
		deviceLocations[0] = new Coordinate (0, 0);

		location[1][0] = distance[0][1];
		location[1][1] = 0; // another server is located on (0, X)
		deviceLocations[1] = new Coordinate ((int)distance[0][1], 0); 

		if (CCtrlParam.numPlayer <= 2) {
			deviceLocationResult(deviceLocations);
			return;
		}

		for (int cnt = 2; cnt < CCtrlParam.numPlayer; cnt++) {
			location[cnt][0] = (Math.pow(distance[0][cnt], 2)
					- Math.pow(distance[1][cnt], 2) + Math.pow(distance[0][1],
					2)) / (2.f * distance[0][1]);
			location[cnt][1] = Math.sqrt(Math.pow(distance[0][cnt], 2)
					- Math.pow(location[cnt][0], 2));
			deviceLocations[cnt] = new Coordinate((int)location[cnt][0], (int)location[cnt][1]);
		}

		/*String resultTv = "Location (cm)\n";

		for (int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++)
			resultTv = resultTv.concat(cnt + ": (" + location[cnt][0] + ", "
					+ location[cnt][1] + ")\n");

		sendControlInfo("serverControl", 10, resultTv);*/
		deviceLocationResult(deviceLocations);
	}

	// //////////////////////////////////////////////////////////////////////////
	// //////////////////////// Client-side API //////////////////////////
	// ///////////////////////////// Start //////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////
	public boolean connectToServer(String serverIp) throws JSONException,
			IOException {
		/* client id is the number of player connected */
		//String id;
		//synchronized (pList) {
		//	this.idNo = pList.size();
		//	id = String.valueOf(idNo);
		//}
		this.serverIpAddr = serverIp;

		/* set familyPop server connection */
		FPserver.setServerConnection(null, this.serverIpAddr, port);

		CCommands connCmd = new CCommands();
		connCmd.beConnectCmd(CUtils.getLocalIpAddr());

		/*
		 * send connection request to FPserver serverHandler of '2' will handle
		 * this request
		 */
		ClientControlSender ccsThread = new ClientControlSender(clientHandler, FPserver, connCmd);
		ccsThread.start();

		cclThread = new ClientControlListener(clientHandler);
		cclThread.start();

		currentMode = client;
		
		return true;
	}

	public boolean disconnectFromServer() throws JSONException {

		CCommands disconnCmd = new CCommands();
		disconnCmd.beDisconnectCtoSCmd(CUtils.getLocalIpAddr());

		/*
		 * send disconnect request to FPserver serverHandler of '3' will handler
		 * this request
		 */
		ClientControlSender ccsThread = new ClientControlSender(clientHandler,
				FPserver, disconnCmd);
		ccsThread.start();

		currentMode = notRunning;
		
		return true;
	}

	// //////////////////////////////////////////////////////////////
	// ////////////////////// Misc. (Client) ////////////////////////
	// //////////////////////////////////////////////////////////////
	private void doClientPlay(long starttime) {
		// Register the broadcast receiver, symphonyReceiver (defined at the bottom)
		if (symphonyReceiver != null) {
			IntentFilter symphonyFilter = new IntentFilter(
					"com.nclab.partitioning.DEFAULT");
			registerReceiver(symphonyReceiver, symphonyFilter);
		}

		SymphonyMaestro smThread = new SymphonyMaestro(starttime, client);
		smThread.start();
	}

	private void NTPTimesync() {
		Log.d("fpLib", "NTP Time Sync - Client");
		CCommands NTPTimesyncCmd = new CCommands();
		try {
			NTPTimesyncCmd.beNTPTimesyncCmd(false, 0);
			ClientControlSender ccsThread = new ClientControlSender(
					clientHandler, FPserver, NTPTimesyncCmd);
			ccsThread.start();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ////////////////////////////////////////////////////
	// ///////////////// Misc. (Common) ///////////////////
	// ////////////////////////////////////////////////////
	

	// ////////////////////////////////////////////////////
	// //////////// Misc. (Control) ///////////////////////
	// ////////////////////////////////////////////////////
	private void initPlayerList() {
		pList.clear();
		pList.add(FPserver);
		updateListView();
	}

	private void resetPlayerList() {
		pList.clear();
		pList.add(FPserver);
		updateListView();
	}

	private void addPlayerList(CPlayer player) {
		Log.d("FamilyPopLib" , " new player connected: " + player.getIpAddr());
		player.setPlayerId(String.valueOf(pList.size()));
		pList.add(player);
		updateListView();
	}

	private void delPlayerList(String id) {
		Log.d ("FamilyPopLib", " player disconnected: " + id);
		int idNo = getIdNoById(id);

		if (idNo > -1) {
			pList.remove(idNo);
			updateListView();
		}
	}

	private int getIdNoById(String id) {
		for (int cnt = 0; cnt < pList.size(); cnt++)
			if (pList.get(cnt).getPlayerId().equals(id))
				return cnt;

		return -1;
	}

	private CPlayer getPlayerFromIPAddr(String ip) {
		for (int i = 0; i < pList.size(); i++) {
			if (pList.get(i).getIpAddr().equals(ip))
				return pList.get(i);
		}

		return null;
	}

	private boolean emptyPlayerList() {
		return (pList.size() == 1);
	}

	private void updateListView() {
		CCtrlParam.numPlayer = pList.size();

		String playerDetails = "";
		if (pList.size() > 0) {
			playerDetails = playerDetails.concat(" [" + pList.get(0).getPlayerId());
			for (int cnt = 1; cnt < pList.size(); cnt++)
				playerDetails = playerDetails.concat(", " + pList.get(cnt).getPlayerId());

			playerDetails = playerDetails.concat("] is connected.");
		} else
			playerDetails = playerDetails.concat("No Player Found.");

		sendControlInfo("serverControl", 9, playerDetails);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////Symphoney-related methods///////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////Start////////////////////////////////////////////////////

	private class SymphonyMaestro extends Thread {
		private long starttime;
		private int mode;

		public SymphonyMaestro(long starttime, int mode) {
			this.starttime = starttime;
			this.mode = mode;
		}

		public void run() {
			// SymPhony
			Log.d("FamilyPopLib", "Start symphony service.");

			bindSymphoneyService();

			symphonyLock.lock();
			try {
				while (!symphonyServiceConnected) {
					symphonyConnected.await();
				}
			} catch (InterruptedException ie) {
			} finally {
				symphonyLock.unlock();
			}

			while (CUtils.gettime() < starttime)
				;
			
			registeredContexts.clear();
			final String[] queries = { "DISTANCE 10000 16000 6000" };

			registerQuery(queries, maestroQueryIds);
			registeredContexts.add("DISTANCE");

			CSignalSensor sSensor = null;
			if (this.mode == server) {
				Log.d("FamilyPopLib", "Server signal sensor initialized.");
				idNo = 0;
				sSensor = new CSignalSensor(soundId, soundPool, serverHandler);
			} else if (this.mode == client) {
				Log.d("FamilyPopLib", "Client signal sensor initialized.");
				sSensor = new CSignalSensor(soundId, soundPool, clientHandler);
			}
			
			Log.d("FamilyPopLib", "Start play sound.");
			sSensor.playback(idNo);
		}
	}

	private void bindSymphoneyService() {
		if (SymphonyService.getInstance().isBinded())
			return;

		// set connectivity to SymphonyService
		SymphonyService.getInstance().setServiceConnection(this);

		// start Symphony Service
		SymphonyService.getInstance().startService(this);
	}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		// TODO Auto-generated method stub
		if (SymphonyService.getInstance().isBinded() == false)
			return;

		Log.d("FamilyPopLib", "SymphoneyService connected.");

		// set task type
		SymphonyService.getInstance().updateTaskType("MW");

		SymphonyService.getInstance().startLogging("/sdcard/maestro_client");

		symphonyLock.lock();
		try {
			symphonyConnected.signal();
		} finally {
			symphonyLock.unlock();
		}
		symphonyServiceConnected = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		// TODO Auto-generated method stub
		Log.d("FamilyPopLib", "SymphoneyService disconnected.");
	}

	private void registerQuery(String[] queries, List<Integer> queryIdsList) {
		try {
			// for all queries
			for (String query : queries) {
				final String[] queryTokens = query.split(" ");

				// construct the query structure
				final String _query = "CONTEXT " + queryTokens[0]
						+ " INTERVAL " + queryTokens[1] + " " + queryTokens[2]
						+ " DELAY " + queryTokens[3];

				// register query to SymphoneyService
				final int queryId = SymphonyService.getInstance()
						.registerQuery(_query);

				Log.d("FamilyPop Client", "RegisterQuery: " + _query + " => "
						+ queryId);

				// add queryId to the list
				queryIdsList.add(queryId);
			}
		} catch (Exception e) {
			Log.d("FamilyPop Client", e.toString());
		}
	}

	private static void stopRecording() {
		Log.d("FamilyPopLib", "Disconnect from Symponey Service");
		deregisterQuery(maestroQueryIds);
	}

	private static void deregisterQuery(List<Integer> queryIdsList) {
		try {
			// for all registered queries
			for (int queryId : queryIdsList) {
				// deregister the query
				SymphonyService.getInstance().deregisterQuery(queryId);

				Log.d("FamilyPopLib", "DeregisterQuery: " + queryId);
			}

			// clear the queryId list
			queryIdsList.clear();
		} catch (Exception e) {
			Log.d("FamilyPopLib", e.toString());
		}
	}

	private BroadcastReceiver symphonyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String ctxName = intent.getStringExtra("context");
			String ctxVal = intent.getStringExtra("result");

			if (ctxName != null) {
				Log.d("FamilyPopLib", "context: " + ctxName + ", result: "
						+ ctxVal);

				try {
					peakIdx.add(Integer.parseInt(ctxVal.substring(0,
							ctxVal.length() - 2)));
				} catch (NumberFormatException nfe) {

				}

				player++;
				if (player == CCtrlParam.numPlayer) {
					Log.d("FamilyPopLib", 
							"All recording done - size of values " + peakIdx.size());
					preProcCalcDistance();
					stopRecording();

					/* set back to initial state */
					player = 0;
					peakIdx.clear();
				}
			}
		}
	};

	private void preProcCalcDistance() {
		if (currentMode == server) {
			curResult[0] = new CCoordResult(CCtrlParam.numPlayer);
			sendControlInfo("serverControl", 1, " Result\n");

			for (int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++) {
				curResult[0].peakCnt[cnt] = peakIdx.get(cnt) + (cnt * CCtrlParam.calcMSToSample(CCtrlParam.interval));
				sendControlInfo("serverControl", 10, " (0, " + cnt + "): " + curResult[0].peakCnt[cnt] + " ");
			}
				
			sendControlInfo("serverControl", 10, "\n");
			resultCnt++;
			calcDistance();
		} else if (currentMode == client) {			
			CCoordResult result = new CCoordResult(CCtrlParam.numPlayer);
			sendControlInfo("clientControl", 10, "\n Result\n");
			
	        for(int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++)
	        {
	    		result.peakCnt[cnt] = peakIdx.get(cnt);	
	        	result.peakVal[cnt] = 0;
	        	sendControlInfo("clientControl", 10, cnt + ": " + result.peakCnt[cnt] );
	        }

			CUtils.sendMsg(clientHandler, 10, result);
		}
	}
	// ////////////////////////////////////////////////////////////
	// ///////////////////////Finish///////////////////////////////
	// ////////////////Symphoney-related methods///////////////////
	// ////////////////////////////////////////////////////////////
}