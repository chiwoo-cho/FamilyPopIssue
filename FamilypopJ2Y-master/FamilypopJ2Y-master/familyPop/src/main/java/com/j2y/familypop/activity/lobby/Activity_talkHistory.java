package com.j2y.familypop.activity.lobby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.client.Activity_clientStart;
import com.j2y.familypop.activity.server.Activity_serverStart;
import com.j2y.familypop.client.FpcRoot;
import com.j2y.familypop.client.FpcTalkRecord;
import com.nclab.familypop.R;

import java.util.ArrayList;


//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_talkHistory
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_talkHistory extends Activity implements View.OnClickListener, AdapterView.OnItemLongClickListener
{
    public static Activity_talkHistory Instance;

    ImageButton _button_addHistory;


    private ListView _listView_talkRecord;
    private ListViewAdapter_talkHistory _listView_adapter;


    // todo: 액션바로 분리
    // topmenu_role
    ImageView _background_role;
    ImageButton _button_role;
    Button _button_client;
    Button _button_locator;
    Button _button_server;

    // topmenu_setting
    ImageView _background_setting;
    ImageButton _button_setting;
    Button _button_setting1;
    Button _button_setting2;
    Button _button_setting3;



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 초기화/종료
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i("[J2Y]", "Activity_talkHistory:onCreate");
        Instance = this;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialogue_history);

        init_actionBar();
        load_talkRecords();
    }


    @Override
    public void onDestroy()
    {
        Log.i("[J2Y]", "Activity_talkHistory:onDestroy");
        Instance = null;
        super.onDestroy();
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void load_talkRecords() {

        _listView_adapter = new ListViewAdapter_talkHistory(getApplicationContext());
        _listView_talkRecord = (ListView)findViewById(R.id.test_listview);

        _listView_talkRecord.setAdapter(_listView_adapter);
        _listView_talkRecord.setOnItemLongClickListener(this);

        if(FpcRoot.Instance._talk_records != null) // ??
        {
            FpcRoot.Instance._talk_records.clear();
            FpcRoot.Instance.LoadTalkRecords(this); //
        }

        // add data
        ArrayList<FpcTalkRecord> talk_records = FpcRoot.Instance._talk_records;
        if(talk_records.size() > 0) {

            for (int i = talk_records.size() - 1; i >=0; --i) {
                FpcTalkRecord temp = FpcRoot.Instance._talk_records.get(i);
                ListView_talkRecord item = new ListView_talkRecord();

                item._text_name = "name : " + temp._name;
                item._text_day = "day : " + temp._filename;
                item._text_playTime = "playTime : " + i;
                item._fpcTalkRecord = temp;

                _listView_adapter.AddItem(item, true);
            }
        }

        _listView_adapter.notifyDataSetChanged();
    }




    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Click events
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_history_topmenu_role:
                //Popup_TopMenu_Role cdd = new Popup_TopMenu_Role(this);
                //cdd.getWindow().setGravity(Gravity.TOP | Gravity.LEFT);
                //cdd.show();

                // View backgroundimage = findViewById(R.id.background_popup_topmenu_role);
                //Drawable background = backgroundimage.getBackground();
//                RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.layout_history_topmenu_role);
//                rLayout.setVisibility(View.VISIBLE);
                //rLayout.setVisibility(View.INVISIBLE);

                active_topmenu_setting(false);
                active_topmenu_role(true);

                break;
            case R.id.button_history_topmenu_setting:
                active_topmenu_setting(true);
                active_topmenu_role(false);
                break;
            // topmenu_role
            case R.id.button_history_topmenu_client: startActivity(new Intent(MainActivity.Instance, Activity_clientStart.class));   active_topmenu_role(false); break;
            case R.id.button_history_topmenu_locator: active_topmenu_role(false); break;
            case R.id.button_history_topmenu_server:  startActivity(new Intent(MainActivity.Instance, Activity_serverStart.class));  active_topmenu_role(false); break;
            case R.id.button_add_history:

                startActivity(new Intent(MainActivity.Instance, Activity_mainRole.class));
                break;

            // topmenu_setting
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        //Toast.makeText(this, "sdfsdfsdf", Toast.LENGTH_SHORT).show();

        // 선택된 리스트 아이템 얻오오기.
        //test_listView_User clickItem = (test_listView_User) view.getTag();
        //test_listView_User clickItem = (test_listView_User) adapter.getItem(position);
        //adapter.remove(clickItem);
        //adapter.notifyDataSetChanged();
        if( view.getId() == R.id.test_listview_layout )
        {
            Button delect = (Button) findViewById(R.id.button_listview_item_history_delect1);
            delect.setVisibility(View.VISIBLE);
        }

        return true;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        active_topmenu_role(false);
        active_topmenu_setting(false);
        return super.onTouchEvent(e);
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 액션바, GUI 세팅
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void init_actionBar() {
        //ui
        _button_addHistory = (ImageButton) findViewById(R.id.button_add_history);
        _button_addHistory.setOnClickListener(this);

        //top menu role
        _button_role = (ImageButton) findViewById(R.id.button_history_topmenu_role);
        _background_role = (ImageView) findViewById(R.id._background_history_topmenu_role);
        _button_client = (Button) findViewById(R.id.button_history_topmenu_client);
        _button_locator = (Button) findViewById(R.id.button_history_topmenu_locator);
        _button_server = (Button) findViewById(R.id.button_history_topmenu_server);

        _button_role.setOnClickListener(this);
        _button_client.setOnClickListener(this);
        _button_locator.setOnClickListener(this);
        _button_server.setOnClickListener(this);

        //top menu setting
        _button_setting = (ImageButton) findViewById(R.id.button_history_topmenu_setting);
        _background_setting = (ImageView) findViewById(R.id._background_history_topmenu_setting);
        _button_setting1 = (Button) findViewById(R.id.button_history_topmenu_setting1);
        _button_setting2 = (Button) findViewById(R.id.button_history_topmenu_setting2);
        _button_setting3 = (Button) findViewById(R.id.button_history_topmenu_listclear);

        _button_setting.setOnClickListener(this);
        _button_setting1.setOnClickListener(this);
        _button_setting2.setOnClickListener(this);
        _button_setting3.setOnClickListener(this);

        active_topmenu_role(false);
        active_topmenu_setting(false);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void active_topmenu_role(boolean active)
    {
        // RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.layout_history_topmenu_role);
        if( active)
        {
            //rLayout.setVisibility(View.VISIBLE);
            _background_role.setVisibility(View.VISIBLE);
            _button_client.setVisibility(View.VISIBLE);
            _button_locator.setVisibility(View.VISIBLE);
            _button_server.setVisibility(View.VISIBLE);
        }
        else
        {
            _background_role.setVisibility(View.INVISIBLE);
            _button_client.setVisibility(View.INVISIBLE);
            _button_locator.setVisibility(View.INVISIBLE);
            _button_server.setVisibility(View.INVISIBLE);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void active_topmenu_setting(boolean active)
    {
        // RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.layout_history_topmenu_role);
        if( active)
        {
            //rLayout.setVisibility(View.VISIBLE);
            _background_setting.setVisibility(View.VISIBLE);
            _button_setting1.setVisibility(View.VISIBLE);
            _button_setting2.setVisibility(View.VISIBLE);
            _button_setting3.setVisibility(View.VISIBLE);
        }
        else
        {
            _background_setting.setVisibility(View.INVISIBLE);
            _button_setting1.setVisibility(View.INVISIBLE);
            _button_setting2.setVisibility(View.INVISIBLE);
            _button_setting3.setVisibility(View.INVISIBLE);
        }
    }


}
