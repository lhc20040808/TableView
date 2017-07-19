package com.lhc.table;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：lhc
 * 时间：2017/7/17.
 */

public interface ITableAdapter<T> {

    int getViewTypeCount();

    int getItemViewType(int row, int column);

    int getRow();

    int getColumn();

    T getItem();

    View getView(int row, int column, View convertView, ViewGroup parent);

    void registerDataSetObserver(DataSetObserver observer);

    void unregisterDataSetObserver(DataSetObserver observer);
}
