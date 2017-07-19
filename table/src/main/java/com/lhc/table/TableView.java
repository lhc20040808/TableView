package com.lhc.table;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：lhc
 * 时间：2017/7/18.
 */

public class TableView extends ViewGroup {
    private static final int NO_POSITION = -1;

    private int width;
    private int height;
    private ITableAdapter mAdapter;
    private RecyclerBin mRecycler;

    private int dividerHeight = 10;
    private int nowColumn, nowRow;
    private int scrollX, scrollY;
    private int[] widthOfColumn;
    private int[] heightOfRow;

    private DataSetObserver observer;
    private GestureDetectorCompat detectorCompat;

    public TableView(Context context) {
        this(context, null);
    }

    public TableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        detectorCompat = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                Log.d("TAG", "disX:" + distanceX);
//                Log.d("TAG", "disY:" + distanceY);
//                if (Math.abs(distanceX) > Math.abs(distanceY)) {
//                    //横向滑动
//                    scrollX += (int) distanceX;
//                } else {
//                    //纵向滑动
//                    scrollY += (int) distanceY;
//                }
//                requestLayout();
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        if (mAdapter != null) {

            int rowCount = mAdapter.getRow();
            int columnCount = mAdapter.getColumn();
            widthOfColumn = new int[columnCount];
            heightOfRow = new int[rowCount];
            int returnHeight = measureHeightOfChildren(0, NO_POSITION);
            int returnWidth = measureWidthOfChildren(0, NO_POSITION);

            if (heightSpecMode == MeasureSpec.AT_MOST) {
                heightSpecSize = Math.min(heightSpecSize, returnHeight);
            }

            if (widthSpecMode == MeasureSpec.AT_MOST) {
                widthSpecSize = Math.min(widthSpecSize, returnWidth);
            }
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    private int measureWidthOfChildren(int startColumn, int endColumn) {
        endColumn = endColumn == NO_POSITION ? mAdapter.getColumn() : endColumn;
        int returnWidth = 0;
        for (int i = startColumn; i < endColumn; i++) {
            View child = obtainView(0, i);
            measureScrapChild(child);
            widthOfColumn[i] = child.getMeasuredWidth();
            if (i > 0) {
                returnWidth += dividerHeight;
            }
            returnWidth += child.getMeasuredWidth();
        }
        return returnWidth;
    }

    private int measureHeightOfChildren(int startRow, int endRow) {
        endRow = endRow == NO_POSITION ? mAdapter.getRow() : endRow;

        int returnHeight = 0;
        for (int i = startRow; i < endRow; i++) {
            View child = obtainView(i, 0);
            measureScrapChild(child);
            heightOfRow[i] = child.getMeasuredHeight();
            if (i > 0) {
                returnHeight += dividerHeight;
            }

            returnHeight += child.getMeasuredHeight();
        }
        return returnHeight;
    }

    private void measureScrapChild(View child) {
        LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = getLayoutParams();
            child.setLayoutParams(p);
        }

        int childWidthSpec = MeasureSpec.makeMeasureSpec(p.width, MeasureSpec.EXACTLY);
        int childHeightSpec = MeasureSpec.makeMeasureSpec(p.height, MeasureSpec.EXACTLY);
        child.measure(childWidthSpec, childHeightSpec);
//        child.forceLayout();
    }

    View obtainView(int row, int column) {
        View scrapView = mRecycler.getScrapView(row, column);
        View child = mAdapter.getView(row, column, scrapView, this);
        if (child == null) {
            throw new RuntimeException("getView不能返回null");
        }

        if (scrapView != null && scrapView != child) {
            mRecycler.addScrapView(scrapView, row, height);
        }

        return child;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childrenTop = getPaddingTop();
        int childrenBottom = childrenTop + getMeasuredHeight();
        int childCount = getChildCount();
        int columnCount = mAdapter.getColumn();
        int rowCount = mAdapter.getRow();

        makeAndAddView(0, 0, 0, 0, widthOfColumn[0], heightOfRow[0]);//头部View

        int left = widthOfColumn[0] + dividerHeight - scrollX;
        int top = heightOfRow[0] + dividerHeight - scrollY;
        int right = 0;
        int bottom = 0;

        if (nowRow == 0) {
            nowRow = 1;
        }

        if (nowColumn == 0) {
            nowColumn = 1;
        }

        //添加首行View
        for (int i = nowColumn; i < columnCount && left < width; i++) {
            right = left + widthOfColumn[nowColumn];
            makeAndAddView(0, i, left, 0, right, heightOfRow[0]);
            left = right + dividerHeight;
        }

        //添加首列View
        for (int i = nowRow; i < rowCount && top < height; i++) {
            bottom = top + heightOfRow[nowColumn];
            makeAndAddView(i, 0, 0, top, widthOfColumn[0], bottom);
            top = bottom + dividerHeight;
        }

        //添加BodyView
        top = heightOfRow[0] + dividerHeight - scrollY;
        for (int i = nowRow; i < rowCount && top < height; i++) {
            bottom = top + heightOfRow[nowRow];
            left = widthOfColumn[0] + dividerHeight - scrollX;
            for (int j = nowColumn; j < columnCount && left < width; j++) {
                right = left + widthOfColumn[nowColumn];
                makeAndAddView(i, j, left, top, right, bottom);
                left = right + dividerHeight;
            }
            top = bottom + dividerHeight;
        }
    }

    private View makeAndAddView(int row, int column, int left, int top, int right, int bottom) {
        View view = obtainView(row, column);
        view.measure(MeasureSpec.makeMeasureSpec(right - left, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(bottom - top, MeasureSpec.EXACTLY));//不测量会导致ViewGroup内部的控件不显示
        addTableView(view, row, column);
        view.layout(left, top, right, bottom);
        return view;
    }

    private void addTableView(View view, int row, int column) {
        if (row == 0 && column == 0) {
            addView(view, getChildCount() - 1);
        } else if (row == 0 || column == 0) {
            addView(view, getChildCount() - 2);
        } else {
            addView(view);
        }
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public void setAdapter(ITableAdapter adapter) {

        if (mAdapter != null && observer != null) {
            mAdapter.unregisterDataSetObserver(observer);
        }

        if (observer == null) {
            observer = new TableDataObserver();
        }

        this.mAdapter = adapter;
        this.mAdapter.registerDataSetObserver(observer);
        this.mRecycler = new RecyclerBin(mAdapter);
        initData();
    }

    private void initData() {
        nowColumn = 0;
        nowRow = 0;
        scrollX = 0;
        scrollY = 0;
        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detectorCompat.onTouchEvent(event);
    }

    class TableDataObserver extends DataSetObserver {


        @Override
        public void onChanged() {
            super.onChanged();
            initData();
        }
    }
}
