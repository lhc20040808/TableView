package com.lhc.table;

import android.view.View;

import java.util.Stack;

/**
 * 作者：lhc
 * 时间：2017/7/17.
 */

public class RecyclerBin {

    private ITableAdapter mAdapter;

    private View[] mActiveViews = new View[0];

    private Stack<View>[] mScrapViews;

    private int viewTypeCount;

    public RecyclerBin(ITableAdapter adapter) {
        this.mAdapter = adapter;
        setViewTypeCount(adapter.getViewTypeCount());
    }

    /**
     * 设置View的类型数量
     * @param viewTypeCount
     */
    private void setViewTypeCount(int viewTypeCount) {
        if (viewTypeCount < 1) {
            throw new IllegalArgumentException("viewTypeCount 不能小于1");
        }

        Stack<View>[] scrapViews = new Stack[viewTypeCount];

        for (int i = 0; i < viewTypeCount; i++) {
            scrapViews[i] = new Stack<>();
        }

        this.mScrapViews = scrapViews;
        this.viewTypeCount = viewTypeCount;
    }

    /**
     * 获取回收池中的View
     * @param row 行数
     * @param column 列数
     * @return
     */
    View getScrapView(int row, int column) {
        int type = mAdapter.getItemViewType(row, column);
        checkTypeValue(type);

        return retrieveFromScrap(mScrapViews[type]);
    }

    private void checkTypeValue(int type) {
        if (type < 0) {
            throw new IllegalArgumentException("类型值不能小于0");
        }

        if (type >= mScrapViews.length) {
            throw new IndexOutOfBoundsException("类型值不能大于总类型数量");
        }
    }

    /**
     * 获取栈顶的View
     * @param mScrapView
     * @return
     */
    private View retrieveFromScrap(Stack<View> mScrapView) {
        if (!mScrapView.isEmpty()) {
            return mScrapView.pop();
        } else {
            return null;
        }
    }

    /**
     * 将View加入回收池
     * @param view
     * @param row 行数
     * @param column 列数
     */
    void addScrapView(View view, int row, int column) {
        int type = mAdapter.getItemViewType(row, column);
        checkTypeValue(type);
        mScrapViews[type].add(view);
    }

}
