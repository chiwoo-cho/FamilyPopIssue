package com.j2y.familypop.activity.server;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.j2y.familypop.MainActivity;
import com.nclab.familypop.R;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_serverCalibrationLocation
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_serverCalibrationLocation extends Activity implements View.OnClickListener
{
    public enum eLocationButtons
    {
        NON(-1),LEFT(0), TOP(1), RIGHT(2), BOTTOM(3),MAX(4);

        private int value;
        eLocationButtons(int i){value = i;}

        public int getValue(){return value;}
    }


    Button[] _button_location;
    ImageButton _button_next;

    eLocationButtons _selectLocationButton;
    boolean _selectServer;

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //

        setContentView(R.layout.activity_calibration_location);

        //ui
        _button_next = (ImageButton) findViewById(R.id.button_calibrationlocation_next);
        _button_next.setOnClickListener(this);

        _button_location = new Button[eLocationButtons.MAX.getValue()];
        _button_location[eLocationButtons.LEFT.getValue()] = (Button) findViewById(R.id.button_calibration_location_left);
        _button_location[eLocationButtons.TOP.getValue()] = (Button) findViewById(R.id.button_calibration_location_top);
        _button_location[eLocationButtons.BOTTOM.getValue()] = (Button) findViewById(R.id.button_calibration_location_bottom);
        _button_location[eLocationButtons.RIGHT.getValue()] = (Button) findViewById(R.id.button_calibration_location_right);

        _button_location[eLocationButtons.LEFT.getValue()].setOnClickListener(this);
        _button_location[eLocationButtons.TOP.getValue()].setOnClickListener(this);
        _button_location[eLocationButtons.BOTTOM.getValue()].setOnClickListener(this);
        _button_location[eLocationButtons.RIGHT.getValue()].setOnClickListener(this);



        _selectLocationButton = eLocationButtons.NON;
        _selectServer = false;


    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        if( v.getId() == R.id.button_calibrationlocation_next)
        {
            startActivity( new Intent(MainActivity.Instance, Activity_serverCalibration.class));
        }

        switch (v.getId())
        {
            case R.id.button_calibrationlocation_next: startActivity( new Intent(MainActivity.Instance, Activity_serverCalibration.class)); break;

            case R.id.button_calibration_location_left:

                setLocationButtonState(eLocationButtons.LEFT, 1);

                break;
            case R.id.button_calibration_location_top:

                setLocationButtonState(eLocationButtons.TOP, 1);

                break;
            case R.id.button_calibration_location_bottom:

                setLocationButtonState(eLocationButtons.BOTTOM, 1);

                break;
            case R.id.button_calibration_location_right:

                setLocationButtonState(eLocationButtons.RIGHT, 1);


                break;


        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 0 idle, 1 select, 2 active
    private void setLocationButtonState(eLocationButtons ebutton, int state)
    {
        String str;
        if( !_selectServer)
        {
            _selectServer = true;
            str = "SERVER";
        }
        else
        {
            str = "Locator";
        }

        if(_selectLocationButton != eLocationButtons.NON)  _button_location[_selectLocationButton.getValue()].setBackgroundResource(R.drawable.button_box_active);

        switch (state)
        {
            case 0: //idel
                _button_location[ebutton.getValue()].setText("");
                _button_location[ebutton.getValue()].setBackgroundResource(R.drawable.button_box_none);
                break;
            case 1: // select
                _button_location[ebutton.getValue()].setText(str);
                _button_location[ebutton.getValue()].setBackgroundResource(R.drawable.button_box_select);
                break;
            case 2: // active
                //_button_location[ebutton.getValue()].setText(str);
                _button_location[ebutton.getValue()].setBackgroundResource(R.drawable.button_box_active);
                break;
        }

        _selectLocationButton = ebutton;

    }

}