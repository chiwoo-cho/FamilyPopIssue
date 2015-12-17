package com.j2y.familypop.server;

import processing.core.PApplet;
import shiffman.box2d.Box2DProcessing;

import com.j2y.network.server.FpNetFacade_server;
import com.j2y.network.server.FpNetServer_client;
import com.j2y.processing.Mover;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsScenario_talk
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsScenario_talk extends FpsScenario_base
{
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnActivated()
	{	
		
	}	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnDeactivated()
	{	
//		for(FpNetServer_client clinet : FpNetFacade_server.Instance._clients)
//		{
//			for(Mover move : clinet._mover)
//				move.DestroyMover();
//
//			clinet._mover.clear();
//		}
	}
		
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnSetup(PApplet scPApplet, Box2DProcessing box2d) 
	{
		super.OnSetup(scPApplet, box2d);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnDraw() 
	{
		
	}
}
