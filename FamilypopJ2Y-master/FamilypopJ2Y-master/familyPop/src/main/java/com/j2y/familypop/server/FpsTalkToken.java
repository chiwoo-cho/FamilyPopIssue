package com.j2y.familypop.server;

import java.util.ArrayList;
import java.util.HashMap;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsTalkToken
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsTalkToken
{
    private int _prevSpeakerID;
    private int _prevSpeakerCnt;
    private float _totalTalkCnt;
    private float _totalNoTalkCnt;

    ArrayList<TokenData> _tokenDataList = new ArrayList<TokenData>();
    ArrayList<TokenData> _tokenValidDataList = new ArrayList<TokenData>();
    ArrayList<TokenData> _tokenInvalidDataList = new ArrayList<TokenData>();

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 정리가 필요한 아이디 추가
    public void AddSpeakerID(int speakerID)
    {
        if(_prevSpeakerID == speakerID)
        {
            _prevSpeakerID = speakerID;
            _prevSpeakerCnt++;
        }
        else
        {
            if(_prevSpeakerID > 2)
                _tokenDataList.add(new TokenData(_prevSpeakerID, _prevSpeakerCnt));

            _prevSpeakerID = speakerID;
            _prevSpeakerCnt = 1;
        }

        _totalTalkCnt++;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 초기화
    public void Init()
    {
        _prevSpeakerID = -1;
        _prevSpeakerCnt = 0;
        _totalTalkCnt = 0;
        _totalNoTalkCnt = 0;

        _tokenDataList.clear();
        _tokenValidDataList.clear();
        _tokenInvalidDataList.clear();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 데이터 정리 (마지막 데이터를 마무리해주는 함수)
    public void Reduce()
    {
        _tokenDataList.add(new TokenData(_prevSpeakerID, _prevSpeakerCnt));

        // 정리
        for(TokenData tData : _tokenDataList)
        {
            if(tData._speakerID >= 2)
            {
                _tokenValidDataList.add(tData);
            }
            else
            {
                _tokenInvalidDataList.add(tData);
                _totalNoTalkCnt += tData._speakerCnt;
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public int GetValidTalkSize()
    {
        return _tokenValidDataList.size();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public TokenData GetValidTalkData(int index)
    {
        return _tokenValidDataList.get(index);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public float GetRateNoTalk()
    {
        if(_totalTalkCnt <= 0)
            return 1;

        return _totalNoTalkCnt / _totalTalkCnt;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    class TokenData
    {
        public int _speakerID;
        public int _speakerCnt;

        public TokenData(int speakerID, int speakerCnt)
        {
            _speakerID = speakerID;
            _speakerCnt = speakerCnt;
        }
    }
}
