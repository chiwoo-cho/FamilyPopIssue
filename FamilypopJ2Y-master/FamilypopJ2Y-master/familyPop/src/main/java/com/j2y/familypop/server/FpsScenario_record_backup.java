package com.j2y.familypop.server;

import android.graphics.Bitmap;

import com.j2y.familypop.MainActivity;
import com.j2y.network.base.FpNetConstants;
import com.j2y.network.base.FpNetUtil;
import com.j2y.network.server.FpNetFacade_server;
import com.j2y.network.server.FpNetServer_client;
import com.j2y.processing.Mover;

import org.jbox2d.common.Vec2;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import processing.core.PApplet;
import processing.core.PImage;
import shiffman.box2d.Box2DProcessing;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsScenario_record
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsScenario_record_backup extends FpsScenario_base
{
    private HashMap<Integer, Mover> _speackingCurState = new HashMap<Integer, Mover>();

    private ScheduledJob _job = new ScheduledJob();
    private Timer _jobScheduler = new Timer();
    private FpsTalkToken _talkToken = new FpsTalkToken();

    private long _noTalkSecond;
    private int _prevSpeakerID ;

    private final long _checkInterval = 800;    // 체크 간격 (밀리초 단위)
    private final long _noTalkMax = 10000;      // 대화주제 끝난 시간(밀리초 단위)
    private final float _endTalkRate = 0.7f;    // 말이 끝났다고 생각할 비율
    private final int _validSpeakCnt = 2;       // 잡음 체크


    // 이미지 공유
    private PImage _shareImage = null;


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 초기화
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
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
	@Override
	public void OnActivated()
	{
        _jobScheduler.scheduleAtFixedRate(_job, 0, _checkInterval);
		MainActivity.Instance._socioPhone.startRecord(0, "temp");
        _noTalkSecond = 0;
        _prevSpeakerID = -1;
        _talkToken.Init();
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnDeactivated()
	{
		MainActivity.Instance._socioPhone.stopRecord();
        _jobScheduler.cancel();

//        // 말풍선 모두 제거
//		for(FpNetServer_client clinet : FpNetFacade_server.Instance._clients)
//		{
//			for(Mover move : clinet._mover)
//				move.DestroyMover();
//
//			clinet._mover.clear();
//		}
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void OnInteractionEventOccured(int speakerID, int eventType, long timestamp)
    {
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void OnDisplayMessageArrived(int type, String message)
    {
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void OnTurnDataReceived(int speakerID)
    {
        // 대화 데이터 추가
        _talkToken.AddSpeakerID(speakerID);
        System.out.println(speakerID);
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 그리기
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnDraw()
	{
		super.OnDraw();

        if(_shareImage != null)
            _scPApplet.image(_shareImage, (_scPApplet.width - _shareImage.width) / 2, (_scPApplet.height - _shareImage.height) / 2);

//		for(FpNetServer_client client : FpNetFacade_server.Instance._clients)
//		{
//            if(null == client._attractor)
//                continue;
//
//            // 유저 그리기
//            client._attractor.display(_scPApplet, _box2d);
//
//            // 말풍선 그리기
//			for (int i = client._mover.size () - 1; i > -1; i--)
//			{
//                // 움직여도 되는지?
//                if(client._mover.get(i)._isMoving == true)
//                {
//                    Vec2 force = client._attractor.attract(client._mover.get(i));
//                    client._mover.get(i).applyForce(force, client._attractor.GetAttractorPos(_box2d));
//                }
//
//                client._mover.get(i).display(_scPApplet);
//			}
//		}
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void AddOrPlusRadiusSpeechBubble(int speakerID, float rad, int color)
	{
        FpNetServer_client clinet = FpNetFacade_server.Instance._clients.get(speakerID);

//         if(!_speackingCurState.containsKey(speakerID))
//        {
//            Mover mover = new Mover();
//            boolean res = mover.CreateMover(_box2d, 30.0f, _scPApplet.width/2, _scPApplet.height/2, color);
//
//            // 기기 사양이 안좋으면 실패할때가 있음..
//            if(res != false)
//            {
//                _speackingCurState.put(speakerID, mover);
//                clinet._mover.add(mover);
//            }
//        }
//        else
//        {
//            Mover curMover = _speackingCurState.get(speakerID);
//            curMover.PlusMoverRadius(rad);
//        }
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 모든 버블 움직이도록 And 스택쌓는 말풍선 모두 제거
    public void AllMoveBubble()
    {
        for(Integer key : _speackingCurState.keySet())
            _speackingCurState.get(key).StartMover(0);

        _speackingCurState.clear();
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 이미지 공유
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
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


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 스케줄 작업?
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    class ScheduledJob extends TimerTask
    {
        public void run()
        {
            // 대화 데이터 정리
            _talkToken.Reduce();

            // 말 풍선 처리
            for(int i = 0; i < _talkToken.GetValidTalkSize(); i++)
            {
                // 유효한 대화 가저오기
                FpsTalkToken.TokenData tokenData = _talkToken.GetValidTalkData(i);

                // 잡음 제거를 위한 연속 갯수 체크
                if(tokenData._speakerCnt > _validSpeakCnt)
                {
                    // 다른 유저가 말하고 있었다면 그 말풍선 끊고 새로 추가
                    if(!_speackingCurState.containsKey(tokenData._speakerID - 2) && _speackingCurState.size() != 0)
                        AllMoveBubble();

                    // 말풍선 추가 or 크기업
                    int bubbleColor = _prevSpeakerID < 2 ? tokenData._speakerID : _prevSpeakerID;
                    AddOrPlusRadiusSpeechBubble(tokenData._speakerID - 2, tokenData._speakerCnt * 2.0f, FpNetConstants.ColorArray[bubbleColor - 2]);
                    _prevSpeakerID = tokenData._speakerID;
                    _noTalkSecond = 0;
                }
            }

            // 대화가 끝났는지 여부
            if(_talkToken.GetRateNoTalk() >= _endTalkRate)
            {
                // 스택 쌓던 말풍선 이제 움직이도록
                AllMoveBubble();

                // 대화 안한 시간 더함
                _noTalkSecond += _checkInterval;
             }

            // 대화 주제가 종료됬다고 간주함
            if(_noTalkSecond > _noTalkMax)
            {
                _prevSpeakerID = -1;
                _noTalkSecond = 0;
            }

            // 대화 데이터 초기화
            _talkToken.Init();
        }
    }
}
