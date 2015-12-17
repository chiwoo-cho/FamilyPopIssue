package com.j2y.familypop.backup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nclab.familypop.R;

import org.w3c.dom.Text;

/**
 * Created by gmpguru on 2015-05-27.
 */
public class Dialog_MessageBox_ok_cancel extends Dialog implements android.view.View.OnClickListener
{
    public Activity _activity;

    public Button _ok;
    public Button _cancel;
    public TextView _content;
    public EditText _editText;

    private String _okTXT;
    private String _cancelTXT;



    public Dialog_MessageBox_ok_cancel(Activity activity)
    {
        super(activity,R.style.Theme_Dialog);

        _okTXT = new String("ok");
        _cancelTXT = new String("cancel");

        _activity = activity;
    }

    public Dialog_MessageBox_ok_cancel(Activity activity, String okTXT, String cancelTXT)
    {
        super(activity);

        _okTXT = okTXT;
        _cancelTXT = cancelTXT;
        _activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        _ok = (Button) findViewById(R.id.button_custom_dialog_ok);
        //_ok.setBackground(R.style.Theme_Dialog);

        _cancel = (Button) findViewById(R.id.button_custom_dialog_cancel);
        _content = (TextView) findViewById(R.id.txt_custom_dialog_content);
        _editText = (EditText) findViewById(R.id.editText_messageBox_ok_cancel);

        _ok.setText(_okTXT);
        _cancel.setText(_cancelTXT);

        _ok.setOnClickListener(this);
        _cancel.setOnClickListener(this);
    }
    @Override
    public void onClick(View v)
    {

    }
}
