package com.j2y.familypop.server;

import java.util.ArrayList;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsMobileDeviceManager
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsMobileDeviceManager
{
	public static FpsMobileDeviceManager Instance;
	public ArrayList<FpsMobileDevice> _mobileDevices = new ArrayList<FpsMobileDevice>();

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpsMobileDeviceManager()
	{
		Instance = this;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void AddDevice(FpsMobileDevice device)
	{
		_mobileDevices.add(device);	
	}
		
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void RemoveDevice(FpsMobileDevice device)
	{
		_mobileDevices.remove(device);	
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public int GetDeviceCount()
	{
		return _mobileDevices.size();	
	}
	
}
