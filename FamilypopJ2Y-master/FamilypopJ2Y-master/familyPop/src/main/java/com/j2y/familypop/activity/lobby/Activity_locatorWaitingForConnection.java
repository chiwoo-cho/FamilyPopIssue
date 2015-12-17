package com.j2y.familypop.activity.lobby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.j2y.familypop.MainActivity;
import com.nclab.familypop.R;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_locatorWaitingForConnection
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_locatorWaitingForConnection extends Activity implements View.OnClickListener
{
    ImageButton _button_home;
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialogue_start_locator_waitingforconnection);


        _button_home = (ImageButton) findViewById(R.id.button_start_locator_waiting_topmenu_home);
        _button_home.setOnClickListener(this);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_start_locator_waiting_topmenu_home:  startActivity(new Intent(MainActivity.Instance, Activity_talkHistory.class)); break;
            case R.id.button_start_locator_next: startActivity(new Intent(MainActivity.Instance, Activity_locatorNowCalibrating.class)); break;
        }
    }
}
