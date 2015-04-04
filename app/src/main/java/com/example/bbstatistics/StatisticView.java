package com.example.bbstatistics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.bbstatistics.com.example.bbstatistics.model.BBPlayer;

public class StatisticView extends View implements View.OnClickListener {
    private static final int DATA_ROWS = 6;//, COLS = 10;
    private static final int NAME_COL_WIDTH_MULTYPLIER = 3;//, COLS = 10;
    float mTextSize = 10, mVerticalPadding = 0;
    int mRowHeight, mColWidth;
    //private static final int NAME_COL_WIDTH = 200;
    private int mNameColWidth;
    private Paint mPaint;
    private int mWidth, mHeight;
    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private TextPaint mTextPaint = new TextPaint(), mHeaderTextPaint = new TextPaint();
    private int[][] data;// = new int[DATA_ROWS][COLS];
    private int mIncrement = 1;

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
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(5f);
        // Get Model from Activity
        Statistic stat = (Statistic) getContext();
        data = new int[DATA_ROWS][BBPlayer.getColumnCount()];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        adjustTextSize();
    }

    /**
     * Adjust text size to fit into cell
     */
    private void adjustTextSize() {
        // One more row needed for header row
        mRowHeight = getHeight() / (DATA_ROWS + 1);
        // Name column is header column
        mNameColWidth = getWidth() / (BBPlayer.getColumnCount() + NAME_COL_WIDTH_MULTYPLIER) * NAME_COL_WIDTH_MULTYPLIER;
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
        Log.d(Consts.TAG, "Target:" + target + ", RowHeight:" + mRowHeight + ", ColWidth=" + mColWidth + ", TextSize:" + mTextSize);
        // and set it into the paint
        mTextPaint.setTextSize(mTextSize);
        mHeaderTextPaint.setTextSize(mTextSize * 0.6f);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawBitmap(canvasBitmap, 0, 0, mPaint);
        //Rect rect = canvas.getClipBounds();
        //Log.d(Consts.TAG, "onDraw clip bounds:" + rect.toShortString());
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        drawGrid(canvas);
    }

    /**
     * Draw grid lines.
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        //float rowHeight = getHeight() / DATA_ROWS;
        //float colWidth = (getWidth() - NAME_COL_WIDTH) / COLS;
        // Display horizontal lines
        for (int i = 1; i <= DATA_ROWS; i++) {
            canvas.drawLine(0, i * mRowHeight, w, i * mRowHeight, mPaint);
        }
        // Display vertical lines
        for (int col = 0; col < BBPlayer.getColumnCount(); col++) {
            canvas.drawLine(mNameColWidth + col * mColWidth, 0, mNameColWidth + col * mColWidth, h, mPaint);
        }
        // Display column headers
        String[] colHeaders = BBPlayer.getColumnNames();
        // Display values
        for (int col = 0; col < BBPlayer.getColumnCount(); col++) {
            int dx = mNameColWidth + col * mColWidth;
            canvas.drawText(colHeaders[col], dx, mRowHeight, mHeaderTextPaint);
            for (int row = 0; row < DATA_ROWS; row++) {
                int dy = (int) (row * mRowHeight + 2 * mRowHeight - mVerticalPadding);
                //if(row == 0 && col == 0)
                //    Log.d(Consts.TAG, "drawGrid [0,0]; dx=" + dx + ", dy=" + dy);
                canvas.drawText("" + data[row][col], dx, dy, mTextPaint);
            }
        }
    }

    /**
     * When grid touched, find cell and increment/decrement value by one
     * @param event Type of user motion
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int row, col;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(Consts.TAG, "Touched at x:" + x + ", y:" + y);
                if (x > mNameColWidth && y > mRowHeight) {
                    // Calculate row, col what was touched
                    col = (int) ((x - mNameColWidth) / mColWidth);
                    row = (int) ((y - mRowHeight) / mRowHeight);
                    Log.d(Consts.TAG, "Touched at col:" + col + ", row:" + row);
                    if (row >= 0 && col >= 0) {
                        if (row < DATA_ROWS && col < BBPlayer.getColumnCount()) {
                            // Is cell data 0? It can't be decremented
                            if (mIncrement < 0 && data[row][col] == 0)
                                break;
                            //Log.d(Consts.TAG, "data[" + (row) + "][" + col + "]=" + data[row][col] + ". Inc:" + mIncrement);
                            data[row][col] += mIncrement;
                            Log.d(Consts.TAG, "data[" + (row) + "][" + col + "]=" + data[row][col]);
                            // Calculate size of rectangle to invalidate
                            int t = (row + 1) * mRowHeight;
                            int l = mNameColWidth + col * mColWidth;
                            //Rect invalidRect = new Rect(l, t, l + mColWidth, t + mRowHeight);
                            //Log.d(Consts.TAG, "Invalidate Rect:" + invalidRect.toShortString());
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
        return true;
    }

    /**
     * Click on +/- button toggles touch action, either +1 or -1
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
}
