package com.j2y.familypop.client;

import com.j2y.network.base.data.FpNetDataRes_recordInfoList;
import com.j2y.network.client.FpNetFacade_client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.content.Context;
import android.util.Log;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpcRoot
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpcRoot
{
	public static FpcRoot Instance;
	public static FpNetFacade_client _client;

    private Context _main_context;
	public FpcScenarioDirectorProxy _scenarioDirectorProxy;
    public ArrayList<FpcTalkRecord> _talk_records = new ArrayList<FpcTalkRecord>();
    public FpcTalkRecord _selected_talk_record;

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void Initialize(Context context)
	{
		Instance = this;
        _main_context = context;
		_client = new FpNetFacade_client();

        _scenarioDirectorProxy = new FpcScenarioDirectorProxy();
	}




    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //  TalkRecord
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    public FpcTalkRecord NewTalkRecord()
    {
        _selected_talk_record = new FpcTalkRecord();
        return _selected_talk_record;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void AddTalkRecord(FpcTalkRecord talk)
    {
        if(talk._list_added)
            return;
        talk._list_added = true;
        _talk_records.add(talk);
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void AddSelectedTalkRecord()
    {
        if(_selected_talk_record != null)
            _talk_records.add(_selected_talk_record);
        _selected_talk_record = null;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void RemoveTalkRecord(FpcTalkRecord talk)
    {
        _talk_records.remove(talk);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public int GetTalkRecord()
    {
        return _talk_records.size();
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 서버에서 버블 정보들 수신됨
    public void RecordTalkBubbles(FpNetDataRes_recordInfoList data) {

        if(_selected_talk_record == null)
            return;

        _selected_talk_record._bubbles.clear();

        for(FpNetDataRes_recordInfoList.FpNetDataRes_recordInfoData bubble : data._bubbles) {

            //Log.i("[J2Y]", String.format("[NetClient]:%f,%f", bubble._x, bubble._y));
            _selected_talk_record.AddBubble(bubble._start_time, bubble._end_time, bubble._x - data._attractor._x, bubble._y - data._attractor._y, bubble._size, bubble._color);
        }

        AddTalkRecord(_selected_talk_record);
        SaveTalkRecords();
    }



    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 파일 기록
    private String s_filename_talk_record = "talk_record.bin";

    public void SaveTalkRecords()
    {
        if(GetTalkRecord() <= 0)
            return;

        Log.i("[J2Y]", "FpcRoot:SaveTalkRecords");
        try
        {
            FileOutputStream fos  = _main_context.openFileOutput(s_filename_talk_record, Context.MODE_PRIVATE);

            ObjectOutputStream objStream = new ObjectOutputStream(fos);

//            if(_talk_records.size() > 0) {
//                FpcTalkRecord talk_record = _talk_records.get(_talk_records.size() - 1);
//                for (FpcTalkRecord.Bubble bubble : talk_record._bubbles)
//                    Log.i("[J2Y]", String.format("[Save][Bubble]:%f,%f", bubble._x, bubble._y));
//            }


            objStream.writeObject(_talk_records);
            objStream.close();

            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void LoadTalkRecords(Context ctx)
    {
        try
        {
            FileInputStream fileInputStream = ctx.openFileInput(s_filename_talk_record);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            _talk_records = (ArrayList<FpcTalkRecord>) objectInputStream.readObject();

//            FpcTalkRecord talk_record =_talk_records.get(0);
//            for(FpcTalkRecord.Bubble bubble : talk_record._bubbles)
//                Log.i("[J2Y]", String.format("[Load][Bubble]:%f,%f", bubble._x, bubble._y));

            objectInputStream.close();
            fileInputStream.close();
        }
        catch (FileNotFoundException e)
        {
            // 기록된 정보가 없음
            //e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}

