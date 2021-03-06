package com.example.bbstatistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.bbstatistics.model.BBPlayer;
import com.example.bbstatistics.pojo.PlayerGamePojo;

import java.util.ArrayList;

public class StatisticView extends View implements View.OnClickListener {
    private static final String TAG = "StatisticView";
    private static final int MIN_DATA_ROWS = 5;// Assumed value to calculate size of font to display in grid
    private static final int NAME_COL_WIDTH_MULTIPLIER = 3;
    private static final int ADDITIONAL_COLUMNS_COUNT = 0;  // One column more for 'on court' cell
    private int mIncrement = 1; // When user touches display, value in cell is incremented by this (either +1 or -1)
    private float mVerticalPadding;
    private int mRowHeight, mColWidth;
    private int mNameColWidth;
    private Paint mLinePaint;
    private TextPaint mTextPaint = new TextPaint(), mHeaderTextPaint = new TextPaint(), mPlayerNamePaint = new TextPaint();
    private Paint mSolidBackground = new Paint();
    // In memory cached data of players
    private PlayerGamePojo[] mPlayersPojoCache; // Every player at game
    private ArrayList<Integer> mPlayersOnCourtIdx = new ArrayList<>(); // Indices of players on court.
    private int mHeaderTextSize;
    private boolean mHasModifiedData = false;

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

    /**
     * Initialize drawing objects
     */
    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.YELLOW);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeWidth(5f);
        // Set Paint properties
        mHeaderTextPaint.setColor(Color.BLUE);
        mPlayerNamePaint.setColor(Color.BLUE);
        mTextPaint.setColor(Color.BLACK);
        //mTextPaint.setTextSize(100);
        mTextPaint.setTextScaleX(1.0f);
        //
        mSolidBackground.setStyle(Paint.Style.FILL);
        mSolidBackground.setColor(Color.LTGRAY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //Bitmap canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //Canvas drawCanvas = new Canvas(canvasBitmap);

        // Name column is header column -
        mNameColWidth = getWidth() / (BBPlayer.getColumnCount() + NAME_COL_WIDTH_MULTIPLIER + ADDITIONAL_COLUMNS_COUNT) * NAME_COL_WIDTH_MULTIPLIER;
        // Column width does not changes
        mColWidth = (getWidth() - mNameColWidth) / (BBPlayer.getColumnCount() + ADDITIONAL_COLUMNS_COUNT);
        adjustRowHeightAndTextSize();
    }

    /**
     * More players can be displayed in grid than actually on court
     * Adjust text size to fit into cell
     */
    private void adjustRowHeightAndTextSize() {
        // Calculate size of text based on 5+1 lines in grid (+1 header row)
        // mPlayersOnCourtIdx contains 'OpponentRowNum' numbers at beginning of ArrayList
        int rowCount = Math.max(MIN_DATA_ROWS + Settings.OpponentRowNum, mPlayersOnCourtIdx.size());
        // Add header row
        rowCount++;
        // One more row needed for header row
        mRowHeight = getHeight() / rowCount;
//        Rect bounds = new Rect();
//        // ask the paint for the bounding rect if it were to draw this text
//        mTextPaint.getTextBounds("00", 0, 2, bounds);
//        // get the height that would have been produced
//        int h = bounds.bottom - bounds.top;
//        // Height of text is not proportional to row height. Try some adjustment ratio
//        float ratio = 9 - rowCount;
//        ratio = ratio > 0 ? ratio : 0;
//        ratio /= 20;
//        ratio = 1f - ratio;
//        // make the text text up 70% of the row height
//        float target = (float) mRowHeight * .7f;
//        // figure out what textSize setting would create that height of text
//        mTextSize = ((target / h) * 100f) * .6f * ratio;
//        Log.v(TAG, "adjHeight() rc:" + rowCount + ", pCnt:" + mPlayersOnCourtIdx.size() + ", rowH:" + mRowHeight
//                + ", Rat:" + ratio + ", txtH:" + mTextSize +", H:" + getHeight());
        float textSize = mRowHeight * .7f;
        mVerticalPadding = (mRowHeight - textSize) / 2;
        //Log.d(Consts.TAG, "Target:" + target + ", RowHeight:" + mRowHeight + ", ColWidth=" + mColWidth + ", TextSize:" + mTextSize);
        // and set it into the paint
        mTextPaint.setTextSize(textSize);
        mPlayerNamePaint.setTextSize(textSize * 0.5f);
        mHeaderTextSize = Math.round(textSize * 0.6f);
        mHeaderTextPaint.setTextSize(mHeaderTextSize);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.v(Consts.TAG, "StatisticView#onDraw(Canvas canvas)");
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
        // Display horizontal lines
        for (int i = 0; i < dataRowsCount; i++) {
            canvas.drawLine(0, (i + 1) * mRowHeight, w, (i + 1) * mRowHeight, mLinePaint);
            // Display player numbers followed by name
            int playerIdx = mPlayersOnCourtIdx.get(i);
            if (playerIdx <= mPlayersPojoCache.length) {
                PlayerGamePojo p = mPlayersPojoCache[playerIdx];
                String str = String.format("%2d", p.getPlayerNumber());
                // If player is not playing, draw solid rectangle as background
                if (!p.isPlaying()) {
                    canvas.drawRect(0, (i + 1) * mRowHeight, mNameColWidth, (i + 2) * mRowHeight, mSolidBackground);
                }
                canvas.drawText(str, 0, (i + 2) * mRowHeight - mVerticalPadding, mHeaderTextPaint);
                // Display beginning of name with smaller font
                // X coordinate is same as row height. Player number is displayed in square.
                // Length in pixels could be calculated, but this estimate is faster to do.
                str = String.format("%.7s", p.getPlayerName());
                canvas.drawText(str, mHeaderTextSize, (i + 2) * mRowHeight - mVerticalPadding, mPlayerNamePaint);
            } else {
                Log.e(TAG, "Player on court index " + playerIdx + " out of range.");
            }
        }
        // Display vertical lines
        for (int col = 0; col < BBPlayer.getColumnCount() + ADDITIONAL_COLUMNS_COUNT; col++) {
            canvas.drawLine(mNameColWidth + col * mColWidth, 0, mNameColWidth + col * mColWidth, h, mLinePaint);
        }
        String[] colHeaders = BBPlayer.getColumnNames();
        // Draw column by column
        for (int col = 0; col < BBPlayer.getColumnCount(); col++) {
            int dx = mNameColWidth + (col + ADDITIONAL_COLUMNS_COUNT) * mColWidth + 5;
            // Display column headers
            canvas.drawText(colHeaders[col], dx, mRowHeight - mVerticalPadding, mHeaderTextPaint);
            // Draw cells from up to down (row by row)
            for (int row = 0; row < dataRowsCount; row++) {
                int dy = (int) (row * mRowHeight + 2 * mRowHeight - mVerticalPadding);
                // Display values in cells
                int playerIdx = mPlayersOnCourtIdx.get(row);
                PlayerGamePojo p = mPlayersPojoCache[playerIdx];
                canvas.drawText("" + p.getFieldValue(col), dx, dy, mTextPaint);
            }
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
                //Log.d(Consts.TAG, "Touched at x:" + x + ", y:" + y);
                if (y > mRowHeight) {
                    row = (int) ((y - mRowHeight) / mRowHeight);
                    PlayerGamePojo p = mPlayersPojoCache[mPlayersOnCourtIdx.get(row)];
                    if (x > mNameColWidth + (ADDITIONAL_COLUMNS_COUNT * mColWidth)) {
                        // Calculate row, col what was touched
                        col = (int) ((x - mNameColWidth) / mColWidth - ADDITIONAL_COLUMNS_COUNT);
                        Log.d(Consts.TAG, "Touched at col:" + col + ", row:" + row);
                        if (row >= 0 && col >= 0) {
                            if (row < dataRowsCount && col < BBPlayer.getColumnCount()) {
                                // Indirect access to mPlayersPojoCache through index stored in ArrayList
                                // Is cell data 0? It can't be decremented
                                if (mIncrement < 0 && p.getFieldValue(col) == 0)
                                    break;
                                // If player is not playing, do not register touch
                                if (p.isPlaying()) {
                                    Log.d(Consts.TAG, "P " + p.getPlayerNumber() + "[" + col + "]=" + p.getFieldValue(col) + ". Inc:" + mIncrement);
                                    p.addToField(col, mIncrement);
                                    invalidate();
                                }
                            }
                        }
                    } else if (x < mNameColWidth) {
                        // Touched player name cell. Revert value
                        // TODO: Should be player playing status change be possible only when clock does not work?
                        if (row > 0)     // 0th row is opponent
                            p.setPlaying(!p.isPlaying());
                    } else {
                        // Touched some of additional columns
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
//        logPlayersOnCourt();
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
     * @param playersPojo In memory cache of players data
     */
    public void setSharedPlayersData(PlayerGamePojo[] playersPojo) {
        mPlayersPojoCache = playersPojo;
        for (int i : mPlayersOnCourtIdx) {
            mPlayersPojoCache[i].setOnCourt(true);
        }
    }

    public void logPlayersOnCourt() {
        StringBuilder sb = new StringBuilder("Content of mPlayersOnCourt:");
        for (Integer l : mPlayersOnCourtIdx) {
            sb.append(l).append(",");
        }
        Log.v(TAG, sb.toString());
    }

    /**
     * Refill list of players on court and redraw grid
     */
    public void redraw() {
        // Compare number of players marked on court before and after substitution
        int countOfPlayersOnCourt = mPlayersOnCourtIdx.size();
        mPlayersOnCourtIdx.clear();
        for (int i = 0; i < mPlayersPojoCache.length; i++) {
            if (mPlayersPojoCache[i].isOnCourt())
                mPlayersOnCourtIdx.add(i);  // Index is added for direct array access
        }
        if (mPlayersOnCourtIdx.size() != countOfPlayersOnCourt) {
            // There may be more/less players marked as 'on court'. Recalculate row height.
            adjustRowHeightAndTextSize();
        }
        logPlayersOnCourt();
        forceLayout();
        //invalidate();
    }

    /**
     * Returns modification stat of data grid
     *
     * @return
     */
    public boolean hasModifiedData() {
        return mHasModifiedData;
    }

    public void clearHasModifiedData() {
        this.mHasModifiedData = false;
    }
}
