package com.j2y.familypop.activity.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.nclab.familypop.R;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_clientGame
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_clientGame extends Activity implements View.OnClickListener
{
    private Button _btGameBack;

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);;
        setContentView(R.layout.activity_client);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        _btGameBack = (Button) findViewById(R.id.button_game_back);
        _btGameBack.setOnClickListener(this);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view)
    {
        //game activity back
        if( view.getId() == R.id.button_game_back)
        {
            setContentView(R.layout.activity_client);
        }
    }
}
