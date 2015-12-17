package com.j2y.familypop.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.lobby.Activity_talkHistory;
import com.j2y.familypop.backup.Dialog_MessageBox_ok_cancel;
import com.j2y.familypop.client.FpcRoot;
import com.j2y.familypop.client.FpcScenario_base;
import com.j2y.familypop.client.FpcTalkRecord;
import com.j2y.network.base.FpNetConstants;
import com.j2y.network.base.data.FpNetData_base;
import com.j2y.network.client.FpNetFacade_client;
import com.nclab.familypop.R;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_clientMain
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_clientMain extends Activity implements OnClickListener//, RadioGroup.OnCheckedChangeListener
{
    public static Activity_clientMain Instance;
    //private Button _applyBtn,
    private Button _imgFindBtn;
    private Button _btGameBack;
    private int _selectScenario;

    private ImageButton _button_home;
    private ImageButton _button_feature;
    private ImageView _image_bubble;

    //feature buttons
    private Button _button_feature_imageFind;
    private Button _button_feature_quitdialogue;

    // text info
    private boolean _plugVisibleInfo;
    private TextView _text_date;
    private TextView _text_name;
    public TextView _text_user;

    //
    private ImageButton _button_redbubble;


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 초기화/종료
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);;
        Log.i("[J2Y]", "Activity_clientMain:onCreate");

        //setContentView(R.layout.activity_client);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_client_mode);


        Instance = this;
        _plugVisibleInfo = false;

        //		//_applyBtn = (Button) findViewById(R.id.applyButton);
//		_imgFindBtn = (Button) findViewById(R.id.imgFindButton);
//		//_btGameBack = (Button) findViewById(R.id.button_game_back);
//
//		//_applyBtn.setOnClickListener(this);
//        _imgFindBtn.setOnClickListener(this);
//		//_btGameBack.setOnClickListener(this);
//
        //_image_bubble = (ImageView) findViewById(R.id.image_bubble_red);
        ((Button) findViewById(R.id.button_client_featuremenu_familybomb)).setOnClickListener(this);
        ((Button) findViewById(R.id.button_client_featuremenu_talk)).setOnClickListener(this);
        ((Button) findViewById(R.id.button_client_featuremenu_smile_event)).setOnClickListener(this);

        _button_redbubble = (ImageButton) findViewById(R.id.button_client_redbubble);
        _button_redbubble.setOnClickListener(this);

        _text_date = (TextView) findViewById(R.id.text_client_date);
        _text_name = (TextView) findViewById(R.id.text_client_name);
        _text_user = (TextView) findViewById(R.id.text_client_users);



        _selectScenario = FpNetConstants.SCENARIO_NONE;

        // Scenario
        for(FpcScenario_base scenario : FpcRoot.Instance._scenarioDirectorProxy._scenarios)
        {
            if(scenario != null)
                scenario.OnCreate(this);
        }

        //top menu
        _button_home = (ImageButton) findViewById(R.id.button_client_dialogue_topmenu_home);
        _button_home.setOnClickListener(this);

        _button_feature = (ImageButton) findViewById(R.id.button_client_dialogue_topmenu_feature);
        _button_feature.setOnClickListener(this);

        // feature menu
        _button_feature_imageFind = (Button) findViewById(R.id.button_client_featuremenu_sharephotos);
        _button_feature_imageFind.setOnClickListener(this);
        _button_feature_quitdialogue = (Button) findViewById(R.id.button_client_featuremenu_quitdialogue);
        _button_feature_quitdialogue.setOnClickListener(this);


        _net_quit_request = false;

	}

    @Override
    protected void onDestroy() {

        Log.i("[J2Y]", "Activity_clientMain:onDestroy");
        //FpNetFacade_client.Instance.Disconnect();

        Instance = null;
        super.onDestroy();
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 이벤트
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [이벤트] 백버튼 막기
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // GetSoundAmplitue()
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [이벤트] 버튼 클릭
    @Override
	public void onClick(View view) 
	{
//		if(view.getId() == R.id.applyButton)
//		{
//			FpNetFacade_client.Instance.SendPacket_req_changeScenario(_selectScenario);
//			//setContentView(R.layout.activity_game);
//		}

        if(view.getId() == R.id.button_client_featuremenu_sharephotos)
        {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");             									// 모든 이미지
            intent.putExtra("crop", "true");        									// Crop기능 활성화
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());     				// 임시파일 생성
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); 	// 포맷방식

            startActivityForResult(intent, 0);
        }

		//scenario select
		switch(view.getId())
		{
			case R.id.button_client_featuremenu_familybomb:
				_selectScenario = FpNetConstants.SCENARIO_GAME;
				FpNetFacade_client.Instance.SendPacket_req_changeScenario(_selectScenario);
                //setContentView(R.layout.activity_game);
				break;

			case R.id.button_client_featuremenu_talk:
				_selectScenario = FpNetConstants.SCENARIO_RECORD;
				FpNetFacade_client.Instance.SendPacket_req_changeScenario(_selectScenario);
                break;

//			case R.id.button_client_talk:
//				_selectScenario = FpNetConstants.SCENARIO_TALK;
//				FpNetFacade_client.Instance.SendPacket_req_changeScenario(_selectScenario);
//                break;

//			case R.id.button_client_dialogue_topmenu_home:
//				startActivity(new Intent(MainActivity.Instance, Activity_talkHistory.class));
//                finish();
//				break;

            case R.id.button_client_dialogue_topmenu_feature:

                active_featureMenu(true);
                //layout_dialogue_menu_feature
                break;

            case R.id.button_client_featuremenu_quitdialogue:

                onClick_Quitdialogue(true);

                break;

            case R.id.button_client_redbubble:
                onClick_Bubble();
                break;

            case R.id.button_client_featuremenu_smile_event:
                FpNetFacade_client.Instance.sendMessage(FpNetConstants.CSReq_smileEvent, new FpNetData_base());
                break;

		}


//		//game activity back
//		if( view.getId() == R.id.button_game_back) // ??
//		{
//			setContentView(R.layout.activity_client);
//		}
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 방 나가기 요청
    private void request_exitRoom() {

        Log.i("[J2Y]", "Request exit room");

        // todo: 방 정보 입력 팝업

        if(FpcRoot.Instance._scenarioDirectorProxy._activeScenario != null)
            FpcRoot.Instance._scenarioDirectorProxy._activeScenario.OnDeactivated();

        FpNetFacade_client.Instance.sendMessage(FpNetConstants.CSReq_exitRoom, new FpNetData_base());
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 서버에서 방 나가기 요청
    public void OnEventSC_exitRoom() {

        Log.i("[J2Y]", "Exit room");

        if(_net_quit_request) {
            ExitRoom();
        }
        else {
            // 방 정보 입력 팝업
            onClick_Quitdialogue(false);
        }
    }
    public void ExitRoom() {


        // 대화 정보 기록하기
        FpcTalkRecord talk_record = FpcRoot.Instance._selected_talk_record;
        if(talk_record != null) {
            //talk_record._name = "test_save";
            if(!talk_record._list_added)
                FpcRoot.Instance.AddTalkRecord(talk_record);
            FpcRoot.Instance.SaveTalkRecords();
        }


        startActivity(new Intent(MainActivity.Instance, Activity_talkHistory.class));
        finish();
    }


    private void active_featureMenu(boolean active)
	{
		RelativeLayout menu = (RelativeLayout) findViewById(R.id.layout_dialogue_menu_feature);
		if(active )
		{
			menu.setVisibility(View.VISIBLE);
		}
		else
		{
			menu.setVisibility(View.INVISIBLE);
		}
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [이벤트] 터치
    public boolean onTouchEvent(MotionEvent e)
	{
		RelativeLayout menu = (RelativeLayout) findViewById(R.id.layout_dialogue_menu_feature);
		menu.setVisibility(View.INVISIBLE);
		return super.onTouchEvent(e);
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [이벤트] 네트워크
    public void OnInteractionEventOccured(int speakerID, int eventType, long timestamp)
	{
		FpcScenario_base activeScenario = FpcRoot.Instance._scenarioDirectorProxy.GetActiveScenario();
		
		if(activeScenario != null)
			activeScenario.OnInteractionEventOccured(speakerID, eventType, timestamp);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnDisplayMessageArrived(int type, String message) 
	{
		FpcScenario_base activeScenario = FpcRoot.Instance._scenarioDirectorProxy.GetActiveScenario();
		
		if(activeScenario != null)
			activeScenario.OnDisplayMessageArrived(type, message);
	}


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [이벤트] 화자 인식
    public void OnTurnDataReceived(int[] speakerID)
	{
		FpcScenario_base activeScenario = FpcRoot.Instance._scenarioDirectorProxy.GetActiveScenario();
		
		if(activeScenario != null)
			activeScenario.OnTurnDataReceived(speakerID);
	}



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 음성 파동 출력
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public VoiceAmplitudeTask _task_voiceAmplitude = new VoiceAmplitudeTask();
    private float _bubble_size = 200f;

    public VoiceAmplitudeTask NewVoiceAmplitudeTask()
    {
        _task_voiceAmplitude = new VoiceAmplitudeTask();
        _task_voiceAmplitude.execute();
        return _task_voiceAmplitude;
    }

    public class VoiceAmplitudeTask extends AsyncTask<Void, Void, Void> {

        public VoiceAmplitudeTask() { }

        @Override
        protected void onProgressUpdate(Void... values) {

            double amplitude = MainActivity.Instance._socioPhone.GetSoundAmplitue();
            int size = (int)(_bubble_size + amplitude);
            _button_redbubble.getLayoutParams().width = size;
            _button_redbubble.getLayoutParams().height = size;
            _button_redbubble.requestLayout();

            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            while(true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                publishProgress();
            }
            //return null;
        }

    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 기타
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 외장메모리에 임시 이미지 파일을 생성하여 그 파일의 경로를 반환
    private Uri getTempUri() 
    {
    	// Check SDCard Mount
    	 String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED))
            return null;
        
        // Create Temp JPG File
        File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        try 
        {
        	boolean res = f.createNewFile();      // 외장메모리에 temp.jpg 파일 생성

            if(res == true)
                return Uri.fromFile(f);
        } 
        catch (IOException e) 
        {
            return null;
        }
 
        // return temp Uri
        return Uri.fromFile(f);
    }
    
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 다시 액티비티로 복귀하였을때 이미지를 셋팅
    protected void onActivityResult(int requestCode, int resultCode, Intent imageData) 
    {
        super.onActivityResult(requestCode, resultCode, imageData);
 
        switch (requestCode) 
        {
	        case 0:
	            if (resultCode == RESULT_OK) 
	            {
	                if (imageData != null) {
	                    String filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";
	 
	                    System.out.println("path" + filePath); // logCat으로 경로확인.

                        // temp.jpg파일을 Bitmap으로 디코딩한다.
	                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);

                        // temp.jpg파일을 이미지뷰에 씌운다.
	                    ImageView _image = (ImageView) findViewById(R.id.imageView);
	                    _image.setImageBitmap(selectedImage);

                        // 임시
                        FpNetFacade_client.Instance.SendPacket_req_shareImage(selectedImage);
                    }
	            }
	            break;
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // ui
    private void onClick_Bubble()
    {
        if( _plugVisibleInfo)
        {
            _text_date.setVisibility(View.VISIBLE);
            _text_name.setVisibility(View.VISIBLE);
            _text_user.setVisibility(View.VISIBLE);

            _plugVisibleInfo = false;
        }
        else
        {
            _text_date.setVisibility(View.INVISIBLE);
            _text_name.setVisibility(View.INVISIBLE);
            _text_user.setVisibility(View.INVISIBLE);
            _plugVisibleInfo = true;
        }

        RelativeLayout menu = (RelativeLayout) findViewById(R.id.layout_dialogue_menu_feature);
        menu.setVisibility(View.INVISIBLE);
    }
    private boolean _net_quit_request;

    private void onClick_Quitdialogue(final boolean net_request)
    {
        _net_quit_request = net_request;
        Dialog_MessageBox_ok_cancel msgbox = new Dialog_MessageBox_ok_cancel(this)
        {
            @Override
            protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                //_content.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");

                //String str  = _editText.getText().toString();

            }
            @Override
            public void onClick(View v)
            {
                super.onClick(v);
                switch (v.getId())
                {
                    case R.id.button_custom_dialog_ok: {
                        FpcTalkRecord talk_record = FpcRoot.Instance._selected_talk_record;
                        if(talk_record != null) {
                            talk_record._name = _editText.getText().toString();
                            talk_record._filename = MainActivity.Instance._socioPhone.GetWavFileName();
                        }

                        if(net_request)
                            request_exitRoom();
                        else
                            ExitRoom();
                    }
                        break;
                    case R.id.button_custom_dialog_cancel: cancel(); break;
                }
            }
        };
        msgbox.show();

    }
}
