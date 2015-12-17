package com.j2y.familypop.backup;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.Activity_clientMain;
import com.j2y.familypop.activity.Activity_serverMain;
import com.j2y.network.client.FpNetFacade_client;
import com.j2y.network.server.FpNetFacade_server;
import com.nclab.familypop.R;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_mainRoleBackup
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_mainRoleBackup extends Activity implements View.OnClickListener
{
    public static Activity_mainRoleBackup instance;

    public enum eSelectMode { CLIENT , SERVER, LOCATOR, MAX}
    private Button _btClient;
    private Button _btServer;
    private Button _btLocator;
    private Button _btOk;

    private Button start;
	private EditText ipText;
	private TextView myIP;


    public eSelectMode _selectMode;

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        instance = this;

        _btClient = (Button) findViewById(R.id.button_role_client);
        _btServer = (Button) findViewById(R.id.button_role_server);
        _btLocator = (Button) findViewById(R.id.button_role_locator);
        _btOk = (Button) findViewById(R.id.button_role_ok);

		ipText = (EditText) findViewById(R.id.ipText);

		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ipAddressStr = String.format("%d.%d.%d.%d",(ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
		myIP = (TextView) findViewById(R.id.myiptext);
		myIP.setText(ipAddressStr);

        _btClient.setOnClickListener(this);
        _btServer.setOnClickListener(this);
        _btLocator.setOnClickListener(this);
        _btOk.setOnClickListener(this);


        _selectMode = eSelectMode.CLIENT;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.button_role_server:  _selectMode = eSelectMode.SERVER; break;
            case R.id.button_role_locator: _selectMode = eSelectMode.LOCATOR; break;
            case R.id.button_role_client: _selectMode = eSelectMode.CLIENT; break;
            case R.id.button_role_ok: onClick_ok(); break;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void onClick_ok()
    {
        OnConnectButton();

        new Thread()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    if(_selectMode == eSelectMode.SERVER && FpNetFacade_server.Instance.IsConnected())
                    {
                        startActivity(new Intent(MainActivity.Instance, Activity_serverMain.class));
                        return;
                    }
                    else if(_selectMode == eSelectMode.CLIENT && FpNetFacade_client.Instance.IsConnected())
                    {
                        startActivity(new Intent(MainActivity.Instance, Activity_clientMain.class));
                        return;
                    }
                }
            }
        }.start();

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void OnConnectButton()
    {
        if(_selectMode == eSelectMode.SERVER && !FpNetFacade_server.Instance.IsConnected())
        {
            FpNetFacade_server.Instance.StartServer(7778);
            MainActivity.Instance._socioPhone.isServer = true;
            MainActivity.Instance._socioPhone.openServer();
        }
        else if(_selectMode == eSelectMode.CLIENT && !FpNetFacade_client.Instance.IsConnected())
        {
            FpNetFacade_client.Instance.ConnectServer(ipText.getText().toString());
            MainActivity.Instance._socioPhone.connectToServer(ipText.getText().toString());
        }
    }
}
