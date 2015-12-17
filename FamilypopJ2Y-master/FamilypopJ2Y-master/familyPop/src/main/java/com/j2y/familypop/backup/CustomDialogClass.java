package com.j2y.familypop.backup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.j2y.familypop.activity.lobby.Activity_talkHistory;
import com.j2y.familypop.MainActivity;
import com.nclab.familypop.R;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// CustomDialogClass
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener
{
    public Activity _activity;
    //public Dialog _dialog;
    public Button _yes, _no;

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public CustomDialogClass(Activity activity)
    {
        super(activity);

        _activity = activity;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        _yes = (Button) findViewById(R.id.button_custom_dialog_ok);
        _no = (Button) findViewById(R.id.button_custom_dialog_cancel);
        _yes.setOnClickListener(this);
        _no.setOnClickListener(this);

        //getWindow().setGravity(Gravity.TOP);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.button_custom_dialog_ok:
                //_activity.finish();
                cancel();
                break;
            case R.id.button_custom_dialog_cancel:
                //dismiss();
                cancel();
                break;
            default:
                break;
        }
        dismiss();
    }

    public static class DialogueActivity extends Activity implements View.OnClickListener
    {
        ImageButton _button_home;
        //------------------------------------------------------------------------------------------------------------------------------------------------------
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_dialogue);

            //ui
            _button_home = (ImageButton) findViewById(R.id.button_dialogue_home);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------------------
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.button_dialogue_home:
                    startActivity(new Intent(MainActivity.Instance, Activity_talkHistory.class));
                    break;
            }
        }
    }
}
