package com.lhc.table;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：lhc
 * 时间：2017/8/3.
 */

public abstract class BaseCommonTableAdapter implements ITableAdapter {

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
    public final int getViewTypeCount() {
        return 4;
    }

    @Override
    public final int getItemViewType(int row, int column) {

        if (row == 0 && column == 0) {
            return 0;
        } else if (row == 0) {
            return 1;
        } else if (column == 0) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public Object getItem() {
        return null;
    }

    @Override
    public final View getView(int row, int column, View convertView, ViewGroup parent) {
        if (row == 0 && column == 0) {
            return getTitleView(convertView, parent);
        } else if (row == 0) {
            return getFirstRowView(column, convertView, parent);
        } else if (column == 0) {
            return getFirstColumnView(row, convertView, parent);
        } else {
            return getBodyView(row, column, convertView, parent);
        }
    }

    public abstract View getTitleView(View convertView, ViewGroup parent);

    public abstract View getFirstRowView(int column, View convertView, ViewGroup parent);

    public abstract View getFirstColumnView(int row, View convertView, ViewGroup parent);

    public abstract View getBodyView(int row, int column, View convertView, ViewGroup parent);
}
