package com.j2y.familypop.server;

import java.util.ArrayList;

import com.j2y.familypop.activity.Activity_serverMain;
import com.j2y.network.server.FpNetFacade_server;
import com.j2y.network.server.FpNetServer_client;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsRoot
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsRoot
{
	public static FpsRoot Instance;
	public static FpNetFacade_server _server;
	
	public FpsMobileDeviceManager _mobileDeviceManager;
	public FpsScenarioDirector _scenarioDirector;
	public FpsTableDisplyer _tableDisplayer;
	//public ArrayList<FpNetServer_client> _clients = new ArrayList<FpNetServer_client>();
    public String _room_user_names = "";


    //------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpsRoot()
	{
		Instance = this;
		
		_server = new FpNetFacade_server();
		
		_mobileDeviceManager = new FpsMobileDeviceManager();
		_scenarioDirector = new FpsScenarioDirector();
		_tableDisplayer = new FpsTableDisplyer();	
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void CloseServer()
	{
        _room_user_names = "";
        _scenarioDirector.CloseServer();
        if(Activity_serverMain.Instance != null)
            Activity_serverMain.Instance.CloseServer();
    }
}


