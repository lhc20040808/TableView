package com.lhc.table;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：lhc
 * 时间：2017/7/18.
 */

public class TableView extends ViewGroup {
    private static final String TAG = "Table";
    private static final int NO_POSITION = -1;

    private int width;
    private int height;
    private ITableAdapter mAdapter;
    private RecyclerBin mRecycler;

    private int dividerHeight = 0;
    private int nowColumn, nowRow;
    private int[] widthOfColumn;
    private int[] heightOfRow;

    private DataSetObserver observer;
    private int touchSlop;
    private VelocityTracker velocityTracker;
    private int downX;
    private int downY;
    private int scrollX;
    private int scrollY;

    private boolean dataChangeFlag;

    private List<View> rowViewList;
    private List<View> columnViewList;
    private List<List<View>> bodyViewList;

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
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        rowViewList = new ArrayList<>();
        columnViewList = new ArrayList<>();
        bodyViewList = new ArrayList<>();
        this.setWillNotDraw(false);
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

        if (width == 0 || height == 0 || dataChangeFlag) {
            //添加或者删除view时都会回调onMeasure，在此防止频繁计算宽高造成卡顿
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
                dataChangeFlag = false;
            }
        } else {
            heightSpecSize = height;
            widthSpecSize = width;
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

    View obtainViewAndAddView(int row, int column, int width, int height) {
        View scrapView = mRecycler.getScrapView(row, column);
        View child = mAdapter.getView(row, column, scrapView, this);
        if (child == null) {
            throw new RuntimeException("getView不能返回null");
        }

        if (scrapView != null && scrapView != child) {
            mRecycler.addScrapView(scrapView, row, height);
        }
        child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));//不测量会导致ViewGroup内部的控件不显示
        child.setTag(R.id.column_num, column);
        child.setTag(R.id.row_num, row);
        addTableView(child, row, column);
        return child;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (changed) {
            int childrenTop = getPaddingTop();
            int childrenBottom = childrenTop + getMeasuredHeight();
            int childCount = getChildCount();
            int columnCount = mAdapter.getColumn();
            int rowCount = mAdapter.getRow();
            rowViewList.clear();
            columnViewList.clear();

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
                View view = makeAndAddView(0, i, left, 0, right, heightOfRow[0]);
                rowViewList.add(view);
                left = right + dividerHeight;
            }

            //添加首列View
            for (int i = nowRow; i < rowCount && top < height; i++) {
                bottom = top + heightOfRow[nowColumn];
                View view = makeAndAddView(i, 0, 0, top, widthOfColumn[0], bottom);
                columnViewList.add(view);
                top = bottom + dividerHeight;
            }

            //添加BodyView
            top = heightOfRow[0] + dividerHeight - scrollY;
            for (int i = nowRow; i < rowCount && top < height; i++) {
                bottom = top + heightOfRow[nowRow];
                left = widthOfColumn[0] + dividerHeight - scrollX;
                List<View> tmpRowViewList = new ArrayList<>();
                for (int j = nowColumn; j < columnCount && left < width; j++) {
                    right = left + widthOfColumn[nowColumn];
                    View view = makeAndAddView(i, j, left, top, right, bottom);
                    tmpRowViewList.add(view);
                    left = right + dividerHeight;
                }
                top = bottom + dividerHeight;
                bodyViewList.add(tmpRowViewList);
            }
        }


    }

    private View makeAndAddView(int row, int column, int left, int top, int right, int bottom) {
        View view = obtainView(row, column);
        view.measure(MeasureSpec.makeMeasureSpec(right - left, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(bottom - top, MeasureSpec.EXACTLY));//不测量会导致ViewGroup内部的控件不显示
        addTableView(view, row, column);
        view.layout(left, top, right, bottom);
        view.setTag(R.id.column_num, column);
        view.setTag(R.id.row_num, row);
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

//    @Override
//    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//        final boolean ret;
//
//        final Integer row = (Integer) child.getTag(R.id.row_num);
//        final Integer column = (Integer) child.getTag(R.id.column_num);
//        // row == null => Shadow view
//        if (row == null || (row == -1 && column == -1)) {
//            ret = super.drawChild(canvas, child, drawingTime);
//        } else {
//            canvas.save();
//            if (row == 0) {
//                canvas.clipRect(widthOfColumn[0], 0, canvas.getWidth(), canvas.getHeight
//                        ());
//            } else if (column == 0) {
//                canvas.clipRect(0, heightOfRow[0], canvas.getWidth(),
//                        canvas.getHeight());
//            } else {
//                canvas.clipRect(widthOfColumn[0], heightOfRow[0], canvas.getWidth(),
//                        canvas.getHeight());
//            }
//
//            ret = super.drawChild(canvas, child, drawingTime);
//            canvas.restore();
//        }
//        return ret;
//    }

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
        dataChangeFlag = true;
        requestLayout();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int disX = (int) Math.abs(downX - ev.getRawX());
                int dixY = (int) Math.abs(downY - ev.getRawY());
                if (disX > touchSlop || dixY > touchSlop) {
                    intercept = true;
                }
                break;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();
                downY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int x2 = (int) event.getRawX();
                int y2 = (int) event.getRawY();
                int diffX = downX - x2;
                int diffY = downY - y2;

                scrollBy(diffX, diffY);
                downX = x2;
                downY = y2;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public void scrollBy(@Px int x, @Px int y) {

        scrollX += x;
        scrollY += y;
        if (scrollX < 0) {
//            Log.d(TAG, "------> scrollX:" + scrollX);
            while (scrollX < 0) {
                //更新firstColumn
                nowColumn--;
                addLeft();
                scrollX += widthOfColumn[nowColumn];
            }

            while (getFilledWidth() - widthOfColumn[nowColumn + rowViewList.size()] >= width) {
                removeRight();
            }

        } else if (scrollX > 0) {
//            Log.d(TAG, "<------ scrollX:" + scrollX);
            while (scrollX > widthOfColumn[nowColumn] + dividerHeight) {
                if (!rowViewList.isEmpty()) {
                    removeLeft();
                }
                scrollX -= widthOfColumn[nowColumn];
                nowColumn++;
            }

            while (getFilledWidth() < width) {
                addRight();
            }
        }

        if (scrollY > 0) {
            Log.d(TAG, "屏幕往上划，手指往下划");
            while (scrollY > heightOfRow[nowRow] + dividerHeight) {
                if (!columnViewList.isEmpty()) {
                    removeTop();
                }

                scrollY -= heightOfRow[nowRow];
                nowRow++;
            }

            while (getFilledHeight() < height) {
                addBottom();
            }

        } else if (scrollY < 0) {
            Log.d(TAG, "屏幕往下划，手指往上划");
            while (scrollY < 0) {
                //更新firstColumn
                nowRow--;
                addTop();
                scrollY += heightOfRow[nowRow];
            }

            while (getFilledHeight() - heightOfRow[nowRow + columnViewList.size()] >= height) {
                removeBottom();
            }
        }

        repositionViews();
    }

    private void addTop() {
        int row = nowRow;
        int index = 0;
        addRow(row, index);
    }

    private void addBottom() {
        int row = nowRow + rowViewList.size();
        int index = rowViewList.size();
        addRow(row, index);
    }

    private void addRight() {
        int column = nowColumn + rowViewList.size();
        int index = rowViewList.size();
        addColumn(column, index);
    }

    private void addLeft() {
        int column = nowColumn;
        int index = 0;
        addColumn(column, index);
    }

    private void addRow(int row, int index) {
        View view = obtainViewAndAddView(row, 0, widthOfColumn[0], heightOfRow[row]);
        columnViewList.add(index, view);


        List<View> tmpList = new ArrayList<>();
        int size = nowColumn + columnViewList.size();
        for (int i = nowColumn; i < size; i++) {
            View child = obtainViewAndAddView(row, nowColumn, widthOfColumn[nowColumn], heightOfRow[row]);
            tmpList.add(child);
        }
        bodyViewList.add(tmpList);
    }

    private void addColumn(int column, int index) {
        View view = obtainViewAndAddView(0, column, widthOfColumn[column], heightOfRow[0]);
        rowViewList.add(index, view);

        int i = nowRow;
        for (List<View> list : bodyViewList) {
            View child = obtainViewAndAddView(i, column, widthOfColumn[column], heightOfRow[i]);
            list.add(index, child);
            i++;
        }
    }


    private void repositionViews() {
        int left, top, right, bottom;

        left = widthOfColumn[0] + dividerHeight - scrollX;
        int i = nowColumn;
        for (View child : rowViewList) {
            right = left + widthOfColumn[i++];
            child.layout(left, 0, right, heightOfRow[0]);
            left = right + dividerHeight;
        }

        top = heightOfRow[0] + dividerHeight - scrollY;
        i = nowRow;
        for (View child : columnViewList) {
            bottom = top + heightOfRow[i++];
            child.layout(0, top, widthOfColumn[0], bottom);
            top = bottom + dividerHeight;
        }

        top = heightOfRow[0] + dividerHeight - scrollY;
        i = nowRow;
        for (List<View> tmpRowList : bodyViewList) {
            bottom = top + heightOfRow[i++];
            left = widthOfColumn[0] + dividerHeight - scrollX;
            int j = nowColumn;
            for (View child : tmpRowList) {
                right = left + widthOfColumn[j++];
                child.layout(left, top, right, bottom);
                left = right + dividerHeight;
            }
            top = bottom + dividerHeight;
        }

        invalidate();
    }

    private int getFilledHeight() {
        return heightOfRow[0] + dividerHeight + sumArray(heightOfRow, nowRow, columnViewList.size()) - scrollY;
    }

    private int getFilledWidth() {
        return widthOfColumn[0] + dividerHeight + sumArray(widthOfColumn, nowColumn, rowViewList.size()) - scrollX;
    }

    private int sumArray(int[] array, int nowColumn, int count) {
        int sum = 0;
        count += nowColumn;
        for (int i = nowColumn; i < count; i++) {
            sum += array[i] + dividerHeight;
        }
        return sum;
    }


    private void removeTop() {
        removeRow(0);
    }

    private void removeBottom() {
        removeRow(columnViewList.size() - 1);
    }


    private void removeRow(int i) {
        removeView(columnViewList.remove(i));
    }


    private void removeRight() {
        removeColumn(rowViewList.size() - 1);
    }

    private void removeLeft() {
        removeColumn(0);
    }

    private void removeColumn(int i) {
        removeView(rowViewList.remove(i));

        for (List<View> list : bodyViewList) {
            removeView(list.remove(i));
        }
    }


    class TableDataObserver extends DataSetObserver {


        @Override
        public void onChanged() {
            super.onChanged();
            initData();
        }
    }
}
