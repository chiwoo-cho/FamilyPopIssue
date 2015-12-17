package com.j2y.familypop.activity.lobby;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.backup.Dialog_MessageBox_ok_cancel;
import com.j2y.familypop.backup.test_CustomSeekBar;
import com.j2y.familypop.client.FpcRoot;
import com.j2y.familypop.client.FpcTalkRecord;
import com.nclab.familypop.R;

import java.util.ArrayList;


/**
 * Created by gmpguru on 2015-05-21.
 */
public class Activity_talkHistoryPlayback extends Activity implements View.OnClickListener ,SeekBar.OnSeekBarChangeListener
{

    RelativeLayout _layout_bubbles;
    ImageButton _button_home;

    SeekBar _seekbar_playState;

    ArrayList<BubbleButton> _bubbleButtons;

    public FpcTalkRecord _fpcTalkRecord;


    //test
    private float totalSpan = 1500;
    private float redSpan = 200;
    private float blueSpan = 300;
    private float greenSpan = 400;
    private float yellowSpan = 150;
    private float darkGreySpan;

    private ArrayList<ProgressItem> progressItemList;
    private ProgressItem mProgressItem;
    test_CustomSeekBar _testseekbar;
    //end test


    private MediaPlayer _media_player;

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 초기화
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("[J2Y]", "Activity_talkHistoryPlayback:onCreate");


        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialouge_playback);

        //ui
        _bubbleButtons = new ArrayList<BubbleButton>();

        _button_home = (ImageButton) findViewById(R.id.button_playback_home);
        _button_home.setOnClickListener(this);
        ((ImageButton) findViewById(R.id.button_play_record)).setOnClickListener(this);

        // buttons test (bubbles)
        _layout_bubbles = (RelativeLayout) findViewById(R.id.layout_talkhistory_playback_bubbles);

        FpcTalkRecord talk_record = FpcRoot.Instance._selected_talk_record;
        if(talk_record != null)
        {

            for (int i = 0; i < talk_record._bubbles.size(); i++)
            {
                FpcTalkRecord.Bubble item = talk_record._bubbles.get(i);
                //item._radius = 100; // 임시
                int bubble_size = (int)(item._radius * 2f);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) item._radius, (int) item._radius);

                Button bbt = bubbleButtons_Create(item);

                // todo: 버블 컬러 변경
                switch (item._color)
                {
//                    case R.color.red:  bbt.setBackgroundResource (R.drawable.image_bubble_red); break;
//                    case R.color.yellow: bbt.setBackgroundResource (R.drawable.image_bubble_yellow); break;
//                    case R.color.purple: bbt.setBackgroundResource(R.drawable.image_bubble_purple); break;
//                    case R.color.dncolor: bbt.setBackgroundResource(R.drawable.image_bubble_donotcolor); break;
                    case 0:  bbt.setBackgroundResource (R.drawable.image_bubble_red); break;
                    case 1: bbt.setBackgroundResource (R.drawable.image_bubble_yellow); break;
                    case 2: bbt.setBackgroundResource(R.drawable.image_bubble_purple); break;
                    case 3: bbt.setBackgroundResource(R.drawable.image_bubble_donotcolor); break;
                }

                Log.i("[J2Y]", String.format("[Playback][Bubble]:%f,%f", item._x, item._y));

                params.setMargins((int) item._x + bubble_size, (int) item._y + bubble_size, 0, 0);

                //params.setMargins(0, 0, 0, 0);
                bbt.setLayoutParams(params);
                bbt.requestLayout();
            }
        }
        //end buttons test (bubbles)
        create_progessBar();


        // 사운드 플레이

        _media_player = new MediaPlayer();
        try {
            String filepath = Environment.getExternalStorageDirectory().getPath() + "/SocioPhone";
            String wav_fullname = filepath + "/" + talk_record._filename;

            Log.i("[J2Y]", "[play_talk_record]" + wav_fullname);
            _media_player.setDataSource(wav_fullname);
            _media_player.prepare();
            _media_player.getDuration();
        }
        catch (Exception e) {
            Toast.makeText(this, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        // 스마일 추가
        addSmileEffect(0, R.drawable.scroll_smilepoint1);
        addSmileEffect(200, R.drawable.scroll_smilepoint0);
        addSmileEffect(400, R.drawable.scroll_smilepoint0);

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        stop_talk_record();

        super.onStop();
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 이벤트
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_playback_home:
                //startActivity(new Intent(MainActivity.Instance, Activity_talkHistory.class));
                finish();
                break;

            case R.id.button_play_record:
                if(_media_player.isPlaying())
                    _media_player.pause();
                else
                    play_talk_record();
                break;


//            case R.id.button_playback_home:
//                //startActivity(new Intent(MainActivity.Instance, Activity_talkHistory.class));
//                finish();
//                break;
        }



        for( int i=0; i<_bubbleButtons.size(); i++)
        {
            if( _bubbleButtons.get(i)._id == v.getId() )
            {
                _bubbleButtons.get(i).onClick(v);
            }
        }


    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void play_talk_record()
    {
        FpcTalkRecord talk_record = FpcRoot.Instance._selected_talk_record;
        if(talk_record == null)
            return;

        _media_player.start();
    }

    private void stop_talk_record() {
        if(_media_player != null)
            _media_player.stop();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    //seek bar
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        //mplayer.seekTo(progress);

        int time = _media_player.getDuration() * progress / 100;

        _media_player.seekTo(time);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 타임라인
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    //class
    public class ProgressItem {

        public int color;
        public float progressItemPercentage;
    }
    private void create_progessBar() {
        // test seekbar
        //_seekbar_playState = (SeekBar) findViewById(R.id.seekBar_history_voice_playstate);
        _testseekbar = (test_CustomSeekBar) findViewById(R.id.seekBar_history_voice_playstate);

        progressItemList = new ArrayList<ProgressItem>();
        // red span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = ((redSpan / totalSpan) * 100);
        //Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
        //mProgressItem.color = Color.RED; //R.color.red;
        mProgressItem.color = R.color.red;
        progressItemList.add(mProgressItem);
        // blue span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (blueSpan / totalSpan) * 100;
        //mProgressItem.color = Color.BLUE;//R.color.blue;
        mProgressItem.color = R.color.blue;
        progressItemList.add(mProgressItem);
        // green span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (greenSpan / totalSpan) * 100;
        //mProgressItem.color = Color.GREEN;//R.color.green;
        mProgressItem.color = R.color.green;
        progressItemList.add(mProgressItem);
        // yellow span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (yellowSpan / totalSpan) * 100;
        //mProgressItem.color = Color.YELLOW;//R.color.yellow;
        mProgressItem.color = R.color.yellow;
        progressItemList.add(mProgressItem);
        // greyspan
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
        //mProgressItem.color = Color.GRAY;//R.color.grey;
        mProgressItem.color = R.color.grey;
        progressItemList.add(mProgressItem);


        _testseekbar.initData(progressItemList);
        _testseekbar.invalidate();
        _testseekbar.setOnSeekBarChangeListener(this);
        _seekbar_handler = new SeekBarHandler();
        _seekbar_handler.execute();


        // end test seekbar

        //_seekbar_playState.initData(progressItemList);
        //_seekbar_playState.invalidate();


//        Button button = new Button(this);
//        button.setId(MainActivity.generateViewId());
//        //button.setid("asldfjaslfdjah")
//        //button.setId();
//        _layout_bubbles.addView(button);
//
//        //_button_home.setBackgroundColor(Color.rgb(0, 255, 0));
    }


    private SeekBarHandler _seekbar_handler;

    public class SeekBarHandler extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            //Log.d("##########Seek Bar Handler ################","###################Destroyed##################");
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            if(_media_player == null)
                return;

            int progress = _media_player.getCurrentPosition() * 100 / _media_player.getDuration();

            _testseekbar.setProgress(progress);
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            while(true) {
                try {
                    if(_media_player != null) {
                        //_media_player.isPlaying();
                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //onProgressUpdate();
                publishProgress();
            }
            //return null;
        }

    }



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 버블 버튼
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private Button bubbleButtons_Create(FpcTalkRecord.Bubble bubble)
    {
        BubbleButton bb = new BubbleButton(MainActivity.generateViewId(), _layout_bubbles,this);
        bb._bubble = bubble;
        _bubbleButtons.add(bb);
        return bb._button;
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public class  BubbleButton
    {
        Button _button;
        int _id;

        Activity _parents;
        public FpcTalkRecord.Bubble _bubble;

        public BubbleButton(int id, ViewGroup layout, Activity_talkHistoryPlayback parents )
        {
            // init
            _id = id;
            _parents = parents;

            // create button
            _button = new Button(parents);
            _button.setOnClickListener(parents);
            _button.setId(_id);
            layout.addView(_button);
        }

        public void onClick(View v)
        {
            if(_media_player == null            )
            return;

            _media_player.seekTo((int)_bubble._startTime);

            int progress = _media_player.getCurrentPosition() * 100 / _media_player.getDuration();
            _testseekbar.setProgress(progress);

            _button.setText("hit!! :" + _id);
        }

    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 스마일 효과
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------


    //pos, R.drawable.image
    private void addSmileEffect(int posx , int image)
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(_testseekbar.getPaddingLeft() + posx, 0, 0, 0);
        //params.addRule(RelativeLayout.BELOW, R.id.layout_talkhistory_playback_bubbles);
        //params.addRule(RelativeLayout.ALIGN_LEFT, R.id.seekBar_history_voice_playstate);

        //이미지 얻어오기
        Drawable drb  = getResources().getDrawable(image);

        ImageView imgview = new ImageView(this);
        //imgview.setImageResource(image);
        imgview.setLayoutParams(params);
        imgview.setImageDrawable(drb);

        // 레이아웃 얻어오기
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_talkhistory_playback_voicestate);

        // 레이아웃에 추가
        layout.addView(imgview);
        // 갱신.
        imgview.requestLayout();
    }
}
