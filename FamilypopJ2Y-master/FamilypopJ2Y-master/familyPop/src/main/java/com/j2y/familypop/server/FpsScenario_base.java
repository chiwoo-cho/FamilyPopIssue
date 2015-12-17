package com.j2y.familypop.server;

import java.util.ArrayList;

import com.j2y.network.server.FpNetServer_client;
import com.j2y.processing.Attractor;
import com.j2y.processing.Mover;

import android.R.integer;
import android.content.Intent;
import processing.core.PApplet;
import shiffman.box2d.Box2DProcessing;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsScenario_base
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsScenario_base
{
	PApplet _scPApplet;
	Box2DProcessing _box2d;
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpsScenario_base()
	{	
	}	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnActivated()
	{	
	}	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnDeactivated()
	{	

	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnSetup(PApplet scPApplet, Box2DProcessing box2d)
	{	
		_scPApplet = scPApplet;
		_box2d = box2d;
	}	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnDraw()
	{
        if(_scPApplet == null || _box2d == null)
            return;
        _scPApplet.background(0);
		_box2d.step();
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnInteractionEventOccured(int speakerID, int eventType, long timestamp)
	{
		
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnDisplayMessageArrived(int type, String message) 
	{
		
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnTurnDataReceived(int speakerID)
	{
		
	}
}
