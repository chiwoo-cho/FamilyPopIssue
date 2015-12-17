package com.j2y.network.base.data;

import com.j2y.network.base.FpNetIncomingMessage;
import com.j2y.network.base.FpNetOutgoingMessage;

import java.util.ArrayList;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetDataRes_recordInfoList
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpNetDataRes_recordInfoList extends FpNetData_base
{
    public FpNetDataRes_recordInfoData _attractor = new FpNetDataRes_recordInfoData();
    public ArrayList<FpNetDataRes_recordInfoData> _bubbles = new ArrayList<FpNetDataRes_recordInfoData>();
    public ArrayList<Integer> _smile_events = new ArrayList<Integer>();

    //----------------------------------------------------------------
    // 메시지 파싱
    @Override
    public void Parse(FpNetIncomingMessage inMsg)
    {
        super.Parse(inMsg);

        _attractor = new FpNetDataRes_recordInfoData();
        _attractor.Parse(inMsg);

        int count = inMsg.ReadInt();
        for(int i = 0; i < count; i++)
        {
            FpNetDataRes_recordInfoData recordInfo = new FpNetDataRes_recordInfoData();
            recordInfo.Parse(inMsg);
            _bubbles.add(recordInfo);
        }

        count = inMsg.ReadInt();
        for(int i = 0; i < count; i++)
        {
            int event_time = inMsg.ReadInt();
            _smile_events.add(event_time);
        }
    }

    //----------------------------------------------------------------
    // 메시지 패킹
    @Override
    public void Packing(FpNetOutgoingMessage outMsg)
    {
        super.Packing(outMsg);

        _attractor.Packing(outMsg);
        outMsg.Write(_bubbles.size());
        for(int i = 0; i < _bubbles.size(); i++)
        {
            FpNetDataRes_recordInfoData recordInfo = _bubbles.get(i);
            recordInfo.Packing(outMsg);
        }

        outMsg.Write(_smile_events.size());
        for(int i = 0; i < _smile_events.size(); i++)
            outMsg.Write(_smile_events.get(i));
    }

    //----------------------------------------------------------------
    // 데이터 추가
    public void AddRecordData(int start_time, int end_time, float x, float y, float size, int color)
    {
        FpNetDataRes_recordInfoData recordData = new FpNetDataRes_recordInfoData();
        recordData._start_time = start_time;
        recordData._end_time = end_time;
        recordData._x = x;
        recordData._y = y;
        recordData._color = color;
        recordData._size = size;

        _bubbles.add(recordData);
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // FpNetDataRes_recordInfoData
    public class FpNetDataRes_recordInfoData
    {
        public int _start_time;
        public int _end_time;
        public float _x;
        public float _y;
        public int _color;
        public float _size;

        //----------------------------------------------------------------
        // 메시지 파싱
        public void Parse(FpNetIncomingMessage inMsg)
        {
            _start_time = inMsg.ReadInt();
            _end_time = inMsg.ReadInt();
            _x = inMsg.ReadFloat();
            _y = inMsg.ReadFloat();
            _color = inMsg.ReadInt();
            _size = inMsg.ReadFloat();
        }

        //----------------------------------------------------------------
        // 메시지 패킹
        public void Packing(FpNetOutgoingMessage outMsg)
        {
            outMsg.Write(_start_time);
            outMsg.Write(_end_time);
            outMsg.Write(_x);
            outMsg.Write(_y);
            outMsg.Write(_color);
            outMsg.Write(_size);
        }
    }
}
