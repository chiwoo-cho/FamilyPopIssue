package com.j2y.familypop.server;

import java.util.Random;

import org.jbox2d.common.Vec2;

import com.j2y.network.base.FpNetConstants;
import com.j2y.network.server.FpNetFacade_server;
import com.j2y.network.server.FpNetServer_client;
import com.j2y.processing.FpsBombMover;
import com.j2y.processing.Mover;

import shiffman.box2d.Box2DProcessing;
import processing.core.*;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsScenario_game
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsScenario_game extends FpsScenario_base
{
    PImage _bombImage = null;
    PImage _explosionImage = null;
    PImage _cannonImage = null;

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnActivated()
	{
        // 폭탄
        if(_bombImage == null)
            _bombImage = _scPApplet.loadImage("bomb.png");

        // 폭발
        if(_explosionImage == null)
            _explosionImage = _scPApplet.loadImage("explosive.png");

        // 대포
        if(_cannonImage == null)
            _cannonImage = _scPApplet.loadImage("cannon.png");
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
		super.OnDraw();

        // 대포 이미지
        _scPApplet.image(_cannonImage, (_scPApplet.width  - _cannonImage.width) / 2, (_scPApplet.height - _cannonImage.height) / 2);

//		for(FpNetServer_client client : FpNetFacade_server.Instance._clients)
//		{
//            if(null == client._attractor)
//                continue;
//
//            client._attractor.display(_scPApplet, _box2d);
//
//			for (int i = client._mover.size () - 1; i > -1; i--)
//			{
//				Vec2 force = client._attractor.attract(client._mover.get(i));
//                client._mover.get(i).applyForce(force, client._attractor.GetAttractorPos(_box2d));
//                client._mover.get(i).display(_scPApplet);
//			}
//		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void StartGame() 
	{
		int clientCount = FpNetFacade_server.Instance._clients.size();
		
//		Random random = new Random();
//		int atrtIdx = random.nextInt(clientCount);
//		float radius = 1;
//		int color = random.nextInt(4);
//
//		float offsetRadius = (float)(radius * 50.0 + 5);
//		FpsBombMover bombMover = new FpsBombMover(_bombImage, _explosionImage);
//		boolean res = bombMover.CreateMover(_box2d, offsetRadius, _scPApplet.width/2, _scPApplet.height/2, FpNetConstants.ColorArray[color]);
//        bombMover.StartMover();
//
//		if(res != false)
//		{
//			FpNetServer_client clinet = FpNetFacade_server.Instance._clients.get(atrtIdx);
//			clinet._mover.add(bombMover);
//		}
	}	
}
