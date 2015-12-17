package com.j2y.familypop.server;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsTableDisplyer
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

import com.j2y.familypop.activity.Activity_serverMain;
import com.j2y.network.base.FpNetConstants;
import com.j2y.network.server.FpNetServer_client;
import com.j2y.processing.Attractor;
import com.j2y.processing.Mover;

import java.util.ArrayList;

import shiffman.box2d.Box2DProcessing;

public class FpsTalkUser
{
    public FpNetServer_client _net_client;

    // Game(FamilyBomb)
    public ArrayList<Mover> _mover;
    public Attractor _attractor;
    public ArrayList<Integer> _smile_events = new ArrayList<Integer>();

    // Calibration
    public float _calibrationX;
    public float _calibrationY;
    public boolean _isSetCalibration;


    //------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpsTalkUser(FpNetServer_client net_client)
	{
        _net_client = net_client;

        _calibrationX = 0.0f;
        _calibrationY = 0.0f;
        _isSetCalibration = false;
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void ResetMovers()
    {
        for(Mover move : _mover)
            move.DestroyMover();
        _mover.clear();
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void CreateAttractor(Box2DProcessing box2d, int width, int height)
    {
        if(_attractor == null)
        {
            switch(_net_client._clientID)
            {
                case 0:
                    _calibrationX = width / 10.0f;
                    _calibrationY = height / 5.0f;
                    break;
                case 1:
                    _calibrationX = 9 * width / 10.0f;
                    _calibrationY = height / 5.0f;
                    break;
                case 2:
                    _calibrationX = 9 * width / 10.0f;
                    _calibrationY = 4 * height / 5.0f;
                    break;
                case 3:
                    _calibrationX = width / 10.0f;
                    _calibrationY = 4 * height / 5.0f;
                    break;
            }

            //Random random = new Random();
            //int color = random.nextInt(FpNetConstants.ColorSize - 1);
            _attractor = new Attractor(box2d, 10, _calibrationX, _calibrationY, FpNetConstants.ColorArray[_net_client._clientID]);
            _mover = new ArrayList<Mover>();
        }
    }

}
