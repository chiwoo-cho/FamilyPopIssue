package com.j2y.familypop.server;

import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.Activity_serverMain;
import com.j2y.network.base.FpNetConstants;
import com.j2y.network.base.FpNetUtil;
import com.j2y.processing.Mover;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import processing.core.PApplet;
import processing.core.PImage;
import shiffman.box2d.Box2DProcessing;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsScenario_record
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsScenario_record extends FpsScenario_base
{
    // 대화 버블
    private ArrayList<Integer> _turnDataSpeakers = new ArrayList<Integer>();
    private ArrayList<Integer> _log_turnData = new ArrayList<Integer>();
    public Mover _current_bubble;
    private Lock _lock_turn_data = new ReentrantLock();

    // 이미지 공유
    private PImage _shareImage = null;


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 초기화
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [렌더링 쓰레드]
    @Override
    public void OnSetup(PApplet scPApplet, Box2DProcessing box2d)
    {
        super.OnSetup(scPApplet, box2d);
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 이벤트
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [렌더링 쓰레드??]
	@Override
	public void OnActivated()
	{
        Log.i("[J2Y]", "[FpsScenario_record] OnActivated ");


        try {
            MainActivity.Instance.startConversation();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.i("[J2Y]", "[SocioPhone] startRecord ");
        _turnDataSpeakers.clear();
        init_turnData_count();
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
    // [렌더링 쓰레드]
    @Override
	public void OnDeactivated()
	{
        Log.i("[J2Y]", "[FpsScenario_record] OnDeactivated ");

        // 말풍선 모두 제거
        for (FpsTalkUser user : Activity_serverMain.Instance._talk_users.values())
            user.ResetMovers();

        MainActivity.Instance._socioPhone.stopRecord();
        _current_bubble = null;

        Log.i("[J2Y]", "[SocioPhone] stopRecord ");
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [메인쓰레드] [이벤트] 대화 턴 데이터
    @Override
    public void OnTurnDataReceived(int speakerID) {
        //Log.i("[J2Y]", "OnTurnDataReceived: " + speakerID[0]);
        //Log.i("[J2Y]", "ThreadID:[OnTurn]" + (int) Thread.currentThread().getId());

        try {
            _lock_turn_data.lock();

            int current_speaker = speakerID; // [??] 0은 대화 없음, 1은 서버
            // 대화 데이터 추가
            if(current_speaker == 1) // 1은 서버
                return;

            _log_turnData.add(current_speaker);
            if(_log_turnData.size() > 10)
                _log_turnData.remove(0);


            if(current_speaker < s_max_user_count)
                ++_turnData_count[current_speaker];

            _turnDataSpeakers.add(current_speaker);
        }
        finally {
            _lock_turn_data.unlock();
        }
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 대화 버블 생성
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [렌더링쓰레드] [이벤트] 대화 턴 데이터
    private void process_turn_data_realtime()
    {
        try {
            _lock_turn_data.lock();
            if (_turnDataSpeakers.size() <= 1) // 첫 대화는 무시
                return;

            int prev_speaker = _turnDataSpeakers.get(0);
            int current_speaker = _turnDataSpeakers.get(_turnDataSpeakers.size() - 1);

            if (prev_speaker != current_speaker) {

                // 1. 이전 버블 발사
                if (_current_bubble != null) {
                    int end_time = (int) MainActivity.Instance._socioPhone.GetRecordTime();
                    _current_bubble.StartMover(end_time);
                    _current_bubble = null;
                }

                FpsTalkUser user = Activity_serverMain.Instance.FindTalkUser_byId(current_speaker - 2);
                if (user != null) {

                    // 2. 새로운 버블 생성
                    Mover bubble = create_new_bubble(prev_speaker, current_speaker);
                    if (bubble != null) {
                        user._mover.add(bubble);
                        _current_bubble = bubble;
                    }
                }

                _turnDataSpeakers.clear();
                _turnDataSpeakers.add(current_speaker); // 마지막 정보는 추가
            } else {
                    /* todo: 버블 크기 변경 */
                if (_current_bubble != null) {
                    _current_bubble.PlusMoverRadius(0.2f);
                }

                _turnDataSpeakers.add(current_speaker);
            }
        }
        finally {
            _lock_turn_data.unlock();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [렌더링쓰레드] [이벤트] 대화 턴 데이터
    private final static int s_max_user_count = 4 + 2; // 최대 유저는 4 + 2
    private int[] _turnData_count = new int[10];
    private int _prev_speaker;

    private void init_turnData_count()
    {
        //_prev_speaker = 0;
        for(int i = 0; i < _turnData_count.length; ++i)
            _turnData_count[i] = 0;
    }
    private void process_turn_data_average(int avg_count)
    {
        try {
            _lock_turn_data.lock();

            // 대화 정보 누적
            if (_turnDataSpeakers.size() <= avg_count) {

                // 버블 크기 변경
                if (_current_bubble != null)
                    _current_bubble.PlusMoverRadius(0.1f);
            }
            else {

                // 1. 가장 말을 많이 한 화자 찾기
                int current_speaker = 0;
                int max_count = 0;
                for (int i = 0; i < _turnData_count.length; ++i) {
                    if (max_count <= _turnData_count[i]) {
                        max_count = _turnData_count[i];
                        current_speaker = i;
                    }
                }

                // 2. 한사람이 계속 얘기 중
                if (_prev_speaker == current_speaker) {

                    // 버블 크기 변경
                    if (_current_bubble != null)
                        _current_bubble.PlusMoverRadius(0.1f);

                }   // 3. 화자가 변경됨
                else {

                    // 3.1. 이전 버블 발사
                    if (_current_bubble != null) {
                        int end_time = (int) MainActivity.Instance._socioPhone.GetRecordTime();
                        _current_bubble.StartMover(end_time);
                        _current_bubble = null;
                    }

                    if(current_speaker > 1) {
                        FpsTalkUser user = Activity_serverMain.Instance.FindTalkUser_byId(current_speaker - 2);
                        if (user != null) {

                            // 3.2. 새로운 버블 생성
                            Mover bubble = create_new_bubble(_prev_speaker, current_speaker);
                            if (bubble != null) {
                                user._mover.add(bubble);
                                _current_bubble = bubble;
                            }
                        }
                    }

                    _prev_speaker = current_speaker;
                }


                init_turnData_count();
                _turnDataSpeakers.clear();
            }
        }
        finally {
            _lock_turn_data.unlock();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 새로운 버블 생성
    private Mover create_new_bubble(int prev_speaker, int current_speaker) {

        if(prev_speaker <= 1) {
            prev_speaker = current_speaker;
            if(prev_speaker <= 1)
                prev_speaker = 2;
        }

        int colorId = prev_speaker - 2;
        int bubble_color = FpNetConstants.ColorArray[colorId];
        //int bubble_color = FpNetConstants.ColorArray[current_speaker - 2];
        Mover mover = new Mover();
        boolean res = mover.CreateMover(_box2d, 30.0f, _scPApplet.width/2, _scPApplet.height/2, bubble_color, colorId);

        if(res) {
            mover._start_time = (int)MainActivity.Instance._socioPhone.GetRecordTime();
            return mover;
        }

        // 기기 사양이 안좋으면 실패할때가 있음..
        return null;
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 그리기
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	//------------------------------------------------------------------------------------------------------------------------------------------------------
    // [렌더링 쓰레드]
	@Override
	public void OnDraw()
	{
		super.OnDraw();

        //process_turn_data_realtime();
        process_turn_data_average(4);

        _scPApplet.text("printing some text to the message window!", 50, 0);

        try {
            _lock_turn_data.lock();
            int y = 0;
            for(int log : _log_turnData)
                _scPApplet.text("" + log, 50, y += 50);
        }
        finally {
            _lock_turn_data.unlock();
        }



        if(_shareImage != null)
            _scPApplet.image(_shareImage, (_scPApplet.width - _shareImage.width) / 2, (_scPApplet.height - _shareImage.height) / 2);

        for (FpsTalkUser user : Activity_serverMain.Instance._talk_users.values())
        {
            if(null == user._attractor)
                continue;

            // 유저 그리기
            user._attractor.display(_scPApplet, _box2d);

            // 말풍선 그리기
            for (int i = user._mover.size () - 1; i > -1; i--)
            {
                // 움직여도 되는지?
                if(user._mover.get(i)._isMoving == true)
                {
                    Vec2 force = user._attractor.attract(user._mover.get(i));
                    user._mover.get(i).applyForce(force, user._attractor.GetAttractorPos(_box2d));
                }

                user._mover.get(i).display(_scPApplet);
            }
        }

	}



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 이미지 공유
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [메인쓰레드]
    public void SetShareImage(byte[] bitmapByteArray)
    {
        Bitmap shareBitmap = FpNetUtil.ByteArrayToBitmap(bitmapByteArray);
        int nHeight = shareBitmap.getHeight();
        int nWidth = shareBitmap.getWidth();

        PImage shareImage = _scPApplet.createImage(nWidth, nHeight, _scPApplet.ARGB);

        for(int y = 0; y < nHeight; y++)
        {
            for (int x = 0; x < nWidth; x++)
            {
                shareImage.pixels[y * nWidth + x] = shareBitmap.getPixel(x, y);
            }
        }

        shareImage.updatePixels();

        _shareImage = shareImage;
    }

}
