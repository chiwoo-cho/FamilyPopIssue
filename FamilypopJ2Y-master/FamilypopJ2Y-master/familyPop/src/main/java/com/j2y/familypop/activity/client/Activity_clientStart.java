package com.j2y.familypop.activity.client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.Activity_clientMain;
import com.j2y.network.client.FpNetFacade_client;
import com.nclab.familypop.R;

import org.json.JSONException;

import java.io.IOException;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_clientStart
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_clientStart extends Activity implements View.OnClickListener
{
    private EditText _ipText;
    private ImageButton _nextBtn;
    private EditText _user_name;
    private int _bubble_color_type;

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i("[J2Y]", "Activity_clientStart:onCreate");

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialogue_start_client);

        _ipText = (EditText) findViewById(R.id.ClientIPText);
        _user_name = (EditText) findViewById(R.id.Text_userName);
        _nextBtn = (ImageButton) findViewById(R.id.ClientNextButton);
        _nextBtn.setOnClickListener(this);


        load_client_information();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        save_client_information();

        if( MainActivity.Instance._virtualServer) {

            startActivity(new Intent(MainActivity.Instance, Activity_clientMain.class));
        }
        else {

            FpNetFacade_client.Instance.ConnectServer(_ipText.getText().toString());
            try {
                MainActivity.Instance.connectToServer(_ipText.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ChangeScenarioActivity();
        }

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void ChangeScenarioActivity()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    if(FpNetFacade_client.Instance.IsConnected())
                    {
                        FpNetFacade_client.Instance.SendPacket_setUserInfo(_user_name.getText().toString(), _bubble_color_type);

                        startActivity(new Intent(MainActivity.Instance, Activity_clientMain.class));
                        finish();
                        return;
                    }
                }
            }
        }.start();
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 클라이언트 정보 저장/읽기
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private  void save_client_information() {

        SharedPreferences prefs = getSharedPreferences("FamilypopClient", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ServerIP", _ipText.getText().toString());
        editor.putString("Username", _user_name.getText().toString());
        editor.commit();

    }
    private  void load_client_information() {

        SharedPreferences prefs = getSharedPreferences("FamilypopClient", MODE_PRIVATE);
        String text_ip = prefs.getString("ServerIP", "192.168.0.44");
        String text_username = prefs.getString("Username", "UserName");

        _ipText.setText(text_ip);
        _user_name.setText(text_username);
    }
}