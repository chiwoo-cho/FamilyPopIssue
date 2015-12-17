package com.j2y.familypop.activity.lobby;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.client.FpcRoot;
import com.nclab.familypop.R;

import java.util.ArrayList;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// ListViewAdapter_talkHistory
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
public class ListViewAdapter_talkHistory extends BaseAdapter implements View.OnClickListener
{
    private Context mContext;

    // ui
    private TextView _text_playTime;
    private TextView _text_name;
    private TextView _text_day;
    private ImageButton _button_play;
    private Button _button_delect;


    // list item
    private ArrayList<ListView_talkRecord> _talkRecord_items;
    private ListView_talkRecord _selectedItem;


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 초기화
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public ListViewAdapter_talkHistory(Context context)
    {
        super();
        mContext = context;
        _talkRecord_items = new ArrayList<ListView_talkRecord>();

        testpos = true;
        topitem = false;
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // BaseAdapter implement
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Override
    public int getCount()
    {
        return _talkRecord_items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return _talkRecord_items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // View
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    boolean testpos;
    boolean topitem;

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View v = convertView;

        TextView text_playTime_left;
        TextView text_name_left;
        TextView text_day_left;
        ImageButton button_play_left;

        TextView text_playTime_right;
        TextView text_name_right;
        TextView text_day_right;
        ImageButton button_play_right;



        if( v == null )
        {

//            if( !topitem )
//            {
//                v = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_item_history_toponce, null);
//
//                _text_playTime = (TextView) v.findViewById(R.id.listview_history_text_topitem_playtime);
//                _text_name = (TextView) v.findViewById(R.id.listview_history_text_topitem_name);
//                _text_day = (TextView) v.findViewById(R.id.listview_history_text_topitem_day);
//                _button_play = (ImageButton) v.findViewById(R.id.listview_history_button_topitem_play);
//
//                topitem = true;
//            }
//            else
            {
                v = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_item_history, null);

                text_playTime_left = (TextView) v.findViewById(R.id.listitem_history_text_playtime);
                text_name_left = (TextView) v.findViewById(R.id.listitem_history_text_name);
                text_day_left = (TextView) v.findViewById(R.id.listitem_history_text_day);
                button_play_left = (ImageButton) v.findViewById(R.id.button_listview_item_history_left);

                text_playTime_right = (TextView) v.findViewById(R.id.listitem_history_text_playtime_right);
                text_name_right = (TextView) v.findViewById(R.id.listitem_history_text_name_right);
                text_day_right = (TextView) v.findViewById(R.id.listitem_history_text_day_right);
                button_play_right = (ImageButton) v.findViewById(R.id.button_listview_item_history_right);

                _button_delect = (Button) v.findViewById(R.id.button_listview_item_history_delect1);


                text_playTime_left.setVisibility(View.GONE);
                text_name_left.setVisibility(View.GONE);
                text_day_left.setVisibility(View.GONE);
                button_play_left.setVisibility(View.GONE);

                text_playTime_right.setVisibility(View.GONE);
                text_name_right.setVisibility(View.GONE);
                text_day_right.setVisibility(View.GONE);
                button_play_right.setVisibility(View.GONE);

                if (testpos)
                {
                    //v = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.test_custom_listview, null);

                    _text_playTime = text_playTime_left;
                    _text_name = text_name_left;
                    _text_day = text_day_left;
                    _button_play = button_play_left;

                    testpos = false;
                }
                else
                {
                    //v = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.test_custom_listview2, null);

                    _text_playTime = text_playTime_right;
                    _text_name = text_name_right;
                    _text_day = text_day_right;
                    _button_play = button_play_right;

                    testpos = true;
                }


                _text_playTime.setVisibility(View.VISIBLE);
                _text_name.setVisibility(View.VISIBLE);
                _text_day.setVisibility(View.VISIBLE);
                _button_play.setVisibility(View.VISIBLE);
            } //top

//            imagUserIcon = (ImageView) v.findViewById(R.id.user_icon);
//            tvUserName = (TextView) v.findViewById(R.id.user_name);
//            tvUserPhoneNumber = (TextView) v.findViewById(R.id.user_phone_number);
//            btnSend = (ImageButton) v.findViewById(R.id.btn_send);

        }

        _selectedItem = (ListView_talkRecord) getItem(position);

        _button_play.setTag(_selectedItem);


        if( _selectedItem != null)
        {
//            if(_selectedItem.getUserIcon() != null)
//            {
//                imagUserIcon.setImageDrawable(_selectedItem.getUserIcon());
//            }
//            tvUserName.setText(_selectedItem.getUserName());
//            tvUserPhoneNumber.setText(_selectedItem.getUserPhoneNumber());
            //btnSend.setOnClickListener((View.OnClickListener) this);

            _text_playTime.setText(_selectedItem._text_playTime);
            _text_name.setText(_selectedItem._text_name);
            _text_day.setText(_selectedItem._text_day);

            _button_play.setOnClickListener(this);
        }
        return v;
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // ListView Items
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void AddItem(ListView_talkRecord user, boolean notify)
    {
        _talkRecord_items.add(user);
        if(notify)
            notifyDataSetChanged();
    }
    public void RemoveItem(ListView_talkRecord user, boolean notify)
    {
        _talkRecord_items.remove(user);
        if(notify)
            notifyDataSetChanged();
    }
    public void RemoveItem(int index, boolean notify)
    {
        _talkRecord_items.remove(index);
        if(notify)
            notifyDataSetChanged();
    }
    public void ClearItems(boolean notify)
    {
        _talkRecord_items.clear();
        if(notify)
            notifyDataSetChanged();
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Click events
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Override
    public void onClick(View v)
    {
//        ListView_talkRecord clickItem = (ListView_talkRecord) v.getTag();
//
//        switch (v.getId())
//        {
//            case R.id.btn_send:
//                Toast.makeText(mContext, clickItem.getUserPhoneNumber(),
//                        Toast.LENGTH_SHORT).show();
//                break;
//        }

        ListView_talkRecord clickItem = (ListView_talkRecord) v.getTag();

        if( v.getId() == R.id.button_listview_item_history_right ||
                v.getId() == R.id.button_listview_item_history_left ||
                v.getId() == R.id.listview_history_button_topitem_play )
        {
            MainActivity.Instance.startActivity(new Intent(MainActivity.Instance, Activity_talkHistoryPlayback.class));

            FpcRoot.Instance._selected_talk_record = clickItem._fpcTalkRecord;
        }
        if( v.getId() == R.id.button_listview_item_history_delect ||
            v.getId() == R.id.button_listview_item_history_delect1  )
        {

            RemoveItem(clickItem, true);
            notifyDataSetChanged();
        }
    }


}
