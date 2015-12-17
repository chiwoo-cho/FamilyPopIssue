package com.j2y.familypop.activity.server;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.Activity_serverMain;
import com.j2y.familypop.activity.lobby.Activity_talkHistory;
import com.j2y.network.server.FpNetFacade_server;
import com.nclab.familypop.R;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_serverStart
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_serverStart extends Activity implements View.OnClickListener
{
    ImageButton _button_home;
    ImageButton _button_next;
    TextView _serverIP;

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i("[J2Y]", "Activity_serverStart:onCreate");

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialogue_start_server);

        //ui
        _button_next = (ImageButton) findViewById(R.id.button_start_server_next);
        _button_next.setOnClickListener(this);

        _button_home = (ImageButton) findViewById(R.id.button_start_server_topmenu_home);
        _button_home.setOnClickListener(this);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ipAddressStr = String.format("%d.%d.%d.%d",(ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        _serverIP = (TextView) findViewById(R.id.ServerIPText);
        _serverIP.setText(ipAddressStr);

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_start_server_next:
                //startActivity(new Intent(MainActivity.Instance, Activity_serverCalibrationLocation.class));

                //FpNetFacade_server.Instance.StartServer(7778);
                //MainActivity.Instance._socioPhone.isServer = true;
                //MainActivity.Instance._socioPhone.openServer();
                //ChangeProcessingActivity();
                startActivity(new Intent(this, Activity_serverMain.class));

                break;

            case R.id.button_start_role_topmenu_home: startActivity(new Intent(this, Activity_talkHistory.class)); break;
        }
    }

//    //------------------------------------------------------------------------------------------------------------------------------------------------------
//    public void ChangeProcessingActivity()
//    {
//
//        new Thread()
//        {
//            @Override
//            public void run()
//            {
//                while(true)
//                {
//                    if(FpNetFacade_server.Instance.IsConnected())
//                    {
//                        startActivity(new Intent(MainActivity.Instance, Activity_serverMain.class));
//                        return;
//                    }
//                }
//            }
//        }.start();
//    }
}
