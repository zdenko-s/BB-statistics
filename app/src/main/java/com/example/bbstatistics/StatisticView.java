package com.example.bbstatistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.bbstatistics.com.example.bbstatistics.model.BBPlayer;
import com.example.bbstatistics.pojo.PlayerGamePojo;

import java.util.ArrayList;

public class StatisticView extends View implements View.OnClickListener {
    private static final String TAG = "StatisticView";
    private static final int DATA_ROWS = 6;// Assumed value to calculate size of font to display in grid
    private static final int NAME_COL_WIDTH_MULTIPLIER = 3;//, COLS = 10;
    private int mIncrement = 1; // When user touches display, value in cell is incremented by this (either +1 or -1)
    private float mTextSize = 10, mVerticalPadding = 0;
    private int mRowHeight, mColWidth;
    private int mNameColWidth;
    private Paint mLinePaint;
    private TextPaint mTextPaint = new TextPaint(), mHeaderTextPaint = new TextPaint();
    // In memory cached data of players
    private PlayerGamePojo[] mPlayersPojoCache; // Every player at game
    private ArrayList<Integer> mPlayersOnCourtIdx = new ArrayList<>(); // Indices of players on court.

    public StatisticView(Context context) {
        super(context);
        Log.v(Consts.TAG, "StatisticView(Context context)");
        init();
    }

    public StatisticView(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
        Log.v(Consts.TAG, "StatisticView(Context context, AttributeSet attrSet)");
        init();
    }

    public StatisticView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.v(Consts.TAG, "StatisticView(Context context, AttributeSet attrs, int defStyle)");
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.YELLOW);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeWidth(5f);
        // Get Model from Activity
        //Statistic stat = (Statistic) getContext();
        //data = new int[DATA_ROWS_][BBPlayer.getColumnCount()];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //int mWidth = w;
        //int mHeight = h;
        //Bitmap canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //Canvas drawCanvas = new Canvas(canvasBitmap);
        adjustTextSize();
    }

    /**
     * Adjust text size to fit into cell
     */
    private void adjustTextSize() {
        // Calculate size of text based on 6+1 lines in grid
        // One more row needed for header row
        mRowHeight = getHeight() / Math.max( (DATA_ROWS + 1), mPlayersOnCourtIdx.size() );
        // Name column is header column
        mNameColWidth = getWidth() / (BBPlayer.getColumnCount() + NAME_COL_WIDTH_MULTIPLIER) * NAME_COL_WIDTH_MULTIPLIER;
        mColWidth = (getWidth() - mNameColWidth) / BBPlayer.getColumnCount();
        // Set Paint properties
        mHeaderTextPaint.setColor(Color.BLUE);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(100);
        mTextPaint.setTextScaleX(1.0f);
        Rect bounds = new Rect();
        // ask the paint for the bounding rect if it were to draw this text
        mTextPaint.getTextBounds("00", 0, 2, bounds);
        // get the height that would have been produced
        int h = bounds.bottom - bounds.top;
        Log.d(Consts.TAG, "Bounds height:" + h);
        // make the text text up 70% of the row height
        float target = (float) mRowHeight * .7f;
        // figure out what textSize setting would create that height of text
        mTextSize = ((target / h) * 100f) * .7f;
        mVerticalPadding = (mRowHeight - mTextSize) / 2;
        //Log.d(Consts.TAG, "Target:" + target + ", RowHeight:" + mRowHeight + ", ColWidth=" + mColWidth + ", TextSize:" + mTextSize);
        // and set it into the paint
        mTextPaint.setTextSize(mTextSize);
        mHeaderTextPaint.setTextSize(mTextSize * 0.6f);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.v(Consts.TAG, "StatisticView#onDraw(Canvas canvas)");

        //canvas.drawBitmap(canvasBitmap, 0, 0, mLinePaint);
//        Rect rect = new Rect();
//        canvas.getClipBounds(rect);
//        Log.d(Consts.TAG, "StatisticView#onDraw clip bounds:" + rect.toShortString());
//        Log.d(Consts.TAG, "StatisticView#onDraw players count:" + rect.toShortString());
        canvas.drawRect(0, 0, getWidth(), getHeight(), mLinePaint);
        drawGrid(canvas);
    }

    /**
     * Draw grid lines.
     *
     * @param canvas Canvas to draw
     */
    private void drawGrid(Canvas canvas) {
        logPlayersOnCourt();
        int dataRowsCount = mPlayersOnCourtIdx.size();
        int w = getWidth();
        int h = getHeight();
        //float rowHeight = getHeight() / DATA_ROWS;
        //float colWidth = (getWidth() - NAME_COL_WIDTH) / COLS;
        // Display horizontal lines
        for (int i = 0; i < dataRowsCount; i++) {
            canvas.drawLine(0, (i + 1) * mRowHeight, w, (i + 1) * mRowHeight, mLinePaint);
            // Display player names
            int playerIdx = mPlayersOnCourtIdx.get(i);
            if (playerIdx <= mPlayersPojoCache.length) {
                PlayerGamePojo p = mPlayersPojoCache[playerIdx];
                String str = p.getPlayerNumber() + ":" + p.getPlayerName();
                canvas.drawText(str, 0, (i + 2) * mRowHeight - 10, mHeaderTextPaint);
            }
        }
        // Display vertical lines
        for (int col = 0; col < BBPlayer.getColumnCount(); col++) {
            canvas.drawLine(mNameColWidth + col * mColWidth, 0, mNameColWidth + col * mColWidth, h, mLinePaint);
        }
        String[] colHeaders = BBPlayer.getColumnNames();
        for (int col = 0; col < BBPlayer.getColumnCount(); col++) {
            int dx = mNameColWidth + col * mColWidth;
            // Display column headers
            canvas.drawText(colHeaders[col], dx, mRowHeight - mVerticalPadding, mHeaderTextPaint);
//            for (int row = 0; row < mDataRows; row++) {
//                int dy = (int) (row * mRowHeight + 2 * mRowHeight - mVerticalPadding);
//                // Display values in cells
//                canvas.drawText("" + data[row][col], dx, dy, mTextPaint);
//            }
        }
    }

    /**
     * When grid touched, find cell and increment/decrement value by one
     *
     * @param event Type of user motion
     * @return true if event consumed
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int row, col;
        final int dataRowsCount = mPlayersOnCourtIdx.size();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(Consts.TAG, "Touched at x:" + x + ", y:" + y);
                if (x > mNameColWidth && y > mRowHeight) {
                    // Calculate row, col what was touched
                    col = (int) ((x - mNameColWidth) / mColWidth);
                    row = (int) ((y - mRowHeight) / mRowHeight);
                    Log.d(Consts.TAG, "Touched at col:" + col + ", row:" + row);
                    if (row >= 0 && col >= 0) {
                        if (row < dataRowsCount && col < BBPlayer.getColumnCount()) {
                            // Is cell data 0? It can't be decremented
//                            if (mIncrement < 0 && data[row][col] == 0)
//                                break;
//                            //Log.d(Consts.TAG, "data[" + (row) + "][" + col + "]=" + data[row][col] + ". Inc:" + mIncrement);
//                            data[row][col] += mIncrement;
//                            Log.d(Consts.TAG, "data[" + (row) + "][" + col + "]=" + data[row][col]);
                            // Calculate size of rectangle to invalidate
                            //int t = (row + 1) * mRowHeight;
                            //int l = mNameColWidth + col * mColWidth;
                            //Rect invalidRect = new Rect(l, t, l + mColWidth, t + mRowHeight);
                            //Log.d(Consts.TAG, "Invalidate Rect:" + invalidRect.toShortString());
                            //Log.v(Consts.TAG, "onTouchEvent - forcing invalidate()");
                            invalidate();
                        }
                    }
                } else {
                    Log.d(Consts.TAG, "Touched out of bounds");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    /**
     * Click on +/- button toggles touch action, either +1 or -1
     *
     * @param v Button clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_plus_minus:
                Button pmButton = (Button) v;
                if (pmButton.getText().equals("+")) {
                    mIncrement = -1;
                    pmButton.setText("-");
                } else {
                    mIncrement = 1;
                    pmButton.setText("+");
                }
                pmButton.invalidate();
                break;
        }
    }

    /**
     * Data shared with parent activity. SubstituteDialog updates OnCourt values when Substitute is called
     *
     * @param playersPojo
     */
    public void setSharedPlayersData(PlayerGamePojo[] playersPojo) {
        mPlayersPojoCache = playersPojo;
        for(int i : mPlayersOnCourtIdx) {
            mPlayersPojoCache[i].setOnCourt(true);
        }
    }

    /**
     * mGridData array contains indices of data in PlayerGamePojo array. PlayerGamePojo holds data for every player
     * mGridData contains indices of players on court. Data of those players is displayed in grid.
     * mGridData is updated with every substitution. Data on screen will be displayed in order as in mGriData.
     * mGridData
     */
    private void setGridDataIndices() {

    }

    public void logPlayersOnCourt() {
        StringBuffer sb = new StringBuffer("Content of mPlayersOnCourt:");
        for (Integer l : mPlayersOnCourtIdx) {
            sb.append(l).append(",");
        }
        Log.v(Consts.TAG, sb.toString());
    }

    /**
     * Refill list of players on court and redraw grid
     */
    public void redraw() {
        mPlayersOnCourtIdx.clear();
        for (int i = 0; i < mPlayersPojoCache.length; i++) {
            if (mPlayersPojoCache[i].isOnCourt())
                mPlayersOnCourtIdx.add(i);  // Index is added for direct array access
        }
        logPlayersOnCourt();
        invalidate();
    }
}
