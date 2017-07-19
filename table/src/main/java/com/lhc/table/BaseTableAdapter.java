package com.lhc.table;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

/**
 * 作者：lhc
 * 时间：2017/7/19.
 */

public abstract class BaseTableAdapter implements ITableAdapter {
    DataSetObservable dataSetObservable = new DataSetObservable();

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        dataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObservable.unregisterObserver(observer);
    }

    public final void notifyDateSetChange() {
        dataSetObservable.notifyChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int row, int column) {
        return 0;
    }

    @Override
    public Object getItem() {
        return null;
    }
}
