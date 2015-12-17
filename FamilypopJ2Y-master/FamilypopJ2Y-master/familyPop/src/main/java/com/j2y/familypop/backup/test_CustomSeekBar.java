package com.j2y.familypop.backup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.lobby.Activity_talkHistory;
import com.j2y.familypop.activity.lobby.Activity_talkHistoryPlayback;

import java.util.ArrayList;

/**
 * Created by gmpguru on 2015-05-27.
 */
public class test_CustomSeekBar extends SeekBar
{
    private ArrayList<Activity_talkHistoryPlayback.ProgressItem> mProgressItemsList;

    public test_CustomSeekBar(Context context)
    {
        super(context);
    }

    public test_CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public test_CustomSeekBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void initData(ArrayList<Activity_talkHistoryPlayback.ProgressItem> progressItemsList)
    {
        this.mProgressItemsList = progressItemsList;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas)
    {
        if (mProgressItemsList.size() > 0)
        {
            int progressBarWidth = getWidth();
            int progressBarHeight = getHeight();
            int thumboffset = getThumbOffset();
            int lastProgressX = 0;
            int progressItemWidth, progressItemRight;
            for (int i = 0; i < mProgressItemsList.size(); i++)
            {
                Activity_talkHistoryPlayback.ProgressItem progressItem = mProgressItemsList.get(i);
                Paint progressPaint = new Paint();
                progressPaint.setColor(getResources().getColor( progressItem.color));

                progressItemWidth = (int) (progressItem.progressItemPercentage * progressBarWidth / 100);

                progressItemRight = lastProgressX + progressItemWidth;

                // for last item give right to progress item to the width
                if (i == mProgressItemsList.size() - 1 && progressItemRight != progressBarWidth)
                {
                    progressItemRight = progressBarWidth;
                }
                Rect progressRect = new Rect();
                progressRect.set(lastProgressX, thumboffset / 2, progressItemRight, progressBarHeight - thumboffset / 2);
                canvas.drawRect(progressRect, progressPaint);
                lastProgressX = progressItemRight;
            }
            super.onDraw(canvas);
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {

        return super.onTouchEvent(e);
    }



}
