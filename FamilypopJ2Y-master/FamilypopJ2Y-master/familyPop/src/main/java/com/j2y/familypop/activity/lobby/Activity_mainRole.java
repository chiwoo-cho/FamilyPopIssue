package com.j2y.familypop.activity.lobby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.client.Activity_clientStart;
import com.j2y.familypop.activity.server.Activity_serverStart;
import com.nclab.familypop.R;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_mainRole
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_mainRole extends Activity implements View.OnClickListener
{

    public enum eRoleButtons
    {   NON(-1), CLIENT(0), LOCATOR(1), SERVER(2), MAX(3);

        private int value;
        eRoleButtons(int i)
        {
            value = i;
        }
        public int getValue()
        {
            return value;
        }

    }

    // top menu
    ImageButton _button_home;
    // role buttons
    eRoleButtons _selectRoleButton;
    ImageButton[] _roleButtons;

    ImageButton _button_next;
//    Button _client;
//    Button _location;
//    Button _server;


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i("[J2Y]", "Activity_mainRole:onCreate");


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialogue_start_role);

        // ui
//        _client = (Button) findViewById(R.id.button_role_client);
//        _location = (Button) findViewById(R.id.button_role_locator);
//        _server = (Button) findViewById(R.id.button_role_server);
//
//        _client.setOnClickListener(this);
//        _location.setOnClickListener(this);
//        _server.setOnClickListener(this);

        // top menu
        _button_home = (ImageButton) findViewById(R.id.button_start_role_topmenu_home);
        _button_home.setOnClickListener(this);

        // role buttons
        _selectRoleButton = eRoleButtons.NON;
        _roleButtons = new ImageButton[eRoleButtons.MAX.getValue()];

        _roleButtons[eRoleButtons.CLIENT.getValue()] = (ImageButton) findViewById(R.id.button_role_client);
        _roleButtons[eRoleButtons.CLIENT.getValue()].setOnClickListener(this);

        _roleButtons[eRoleButtons.LOCATOR.getValue()] = (ImageButton) findViewById(R.id.button_role_locator);
        _roleButtons[eRoleButtons.LOCATOR.getValue()].setOnClickListener(this);

        _roleButtons[eRoleButtons.SERVER.getValue()] = (ImageButton) findViewById(R.id.button_role_server);
        _roleButtons[eRoleButtons.SERVER.getValue()].setOnClickListener(this);


        _button_next = (ImageButton) findViewById(R.id.button_start_role_next);
        _button_next.setOnClickListener(this);

        check_role_allNotSelect();
    }
    @Override
    protected void onDestroy() {
        Log.i("[J2Y]", "Activity_mainRole:onDestroy");
        super.onDestroy();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            // top menu
            case R.id.button_start_role_topmenu_home:
                startActivity(new Intent(MainActivity.Instance, Activity_talkHistory.class));
                break;

            // role buttons
            case R.id.button_role_client:

                _button_next.setImageResource(R.drawable.button_next);

                check_role_allNotSelect();
                check_role(_roleButtons[eRoleButtons.CLIENT.getValue()], true);
                _selectRoleButton = eRoleButtons.CLIENT;
                break;

            case R.id.button_role_locator:

                _button_next.setImageResource(R.drawable.button_next);

                check_role_allNotSelect();
                check_role(_roleButtons[eRoleButtons.LOCATOR.getValue()], true);
                _selectRoleButton = eRoleButtons.LOCATOR;
                break;
            case R.id.button_role_server:

                _button_next.setImageResource(R.drawable.button_next);

                check_role_allNotSelect();
                check_role(_roleButtons[eRoleButtons.SERVER.getValue()], true);
                _selectRoleButton = eRoleButtons.SERVER;
                break;

            case R.id.button_start_role_next:
                onNextButton();
                break;
        }




//        if( v.getId() == R.id.button_role_client)
//        {
//            startActivity(new Intent(MainActivity.Instance, Activity_clientStart.class));
//        }
//        if( v.getId() == R.id.button_role_locator)
//        {
//
//        }
//        if( v.getId() == R.id.button_role_server)
//        {
//            startActivity(new Intent(MainActivity.Instance, Activity_serverStart.class));
//        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void onNextButton()
    {

        if(_selectRoleButton ==  eRoleButtons.CLIENT)
        {
            startActivity(new Intent(MainActivity.Instance, Activity_clientStart.class));

        }
        if(_selectRoleButton ==  eRoleButtons.LOCATOR)
        {
            startActivity(new Intent(MainActivity.Instance, Activity_locatorStart.class));

        }
        if(_selectRoleButton ==  eRoleButtons.SERVER)
        {
            startActivity(new Intent(MainActivity.Instance, Activity_serverStart.class));

        }

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void check_role_allNotSelect()
    {
        for( int i=0; i<eRoleButtons.MAX.getValue(); i++)
        {
            check_role( _roleButtons[i] , false);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void check_role(ImageButton imagebutton,  boolean check)
    {
        //Drawable backgruond = imagebutton.getBackground();
        if( check)
        {
            imagebutton.setImageResource(R.drawable.button_base_on);
        }
        else
        {
            imagebutton.setImageResource(R.drawable.button_base_off);
        }
    }
}

