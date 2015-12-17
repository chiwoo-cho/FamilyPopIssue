package com.j2y.familypop.activity.lobby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.j2y.familypop.MainActivity;
import com.nclab.familypop.R;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_title
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_title extends Activity implements View.OnClickListener
{

    public static Activity_title Instance;

    private Button _btStartDialogue;
    private Button _btHistory;
    private Button _btSetting;

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Instance = this;

        Log.i("[J2Y]", "Activity_title:onCreate");

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_title);

        //back
//        _btStartDialogue = (Button) findViewById(R.id.button_startDialogue);
//        _btHistory = (Button) findViewById(R.id.button_history);
//        _btSetting = (Button) findViewById(R.id.button_setting);
//
//        _btStartDialogue.setOnClickListener(this);
//        _btHistory.setOnClickListener(this);
//        _btSetting.setOnClickListener(this);

        // Touch screen operation.
//        ImageView iv = new ImageView(this);
//        iv.setBackgroundResource(R.drawable.color_background);
//        iv.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Intent i = new Intent(Activity_title.this, Activity_mainRoleBackup.class);
//                startActivity(i);
//                finish();
//            }
//        });
//
//        setContentView(iv);
        //


    }

    @Override
    protected void onDestroy() {
        Log.i("[J2Y]", "Activity_title:onDestroy");
        Instance = null;
        super.onDestroy();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view)
    {



        //back
//        if( view.getId() == R.id.button_startDialogue)
//        {
//            startActivity(new Intent(this, Activity_mainRoleBackup.class));
//        }
//        if( view.getId() ==  R.id.button_setting)
//        {
//
//            final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
//            dlgAlert.setTitle("title test");
//            dlgAlert.setMessage("test test test");
//
//
//            dlgAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    dialog.cancel();
//                }
//
//            });
//
//            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    //dialog.dismiss();
//                    dialog.cancel();
//
//                }
//            });
////            dlgAlert.setNeutralButton("33", new DialogInterface.OnClickListener()
////            {
////                public void onClick(DialogInterface dialog, int whichButton)
////                {
////                    //dialog.dismiss();
////                    dialog.cancel();
////
////                }
////            });
//
//            dlgAlert.setCancelable(true);
//            dlgAlert.create().show();
//        }
//        if( view.getId() == R.id.button_history)
//        {
////            Dialog dialog = new Dialog(this, R.style.testDialog);
////            dialog.setContentView(R.layout.my_dialog_layout);
////            dialog.show();
//
//
//            CustomDialogClass cdd = new CustomDialogClass(this);
//            cdd.show();
//
//        }

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        //startActivity(new Intent(MainActivity.Instance, Activity_talkHistory.class));

        if(e.getActionMasked() == MotionEvent.ACTION_UP) {
            Log.i("[J2Y]", "Activity_title:onTouchEvent");
            startActivity(new Intent(this, Activity_talkHistory.class));
        }

        return super.onTouchEvent(e);
    }
}
