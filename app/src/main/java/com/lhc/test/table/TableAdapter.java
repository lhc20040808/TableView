package com.lhc.test.table;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lhc.table.BaseTableAdapter;

/**
 * 作者：lhc
 * 时间：2017/7/19.
 */

public class TableAdapter extends BaseTableAdapter {
    private Context context;

    public TableAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getRow() {
        return 1000;
    }

    @Override
    public int getColumn() {
        return 1000;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        if (row == 0 && column == 0) {
            textView.setText("时间\\月份");
        } else {
            textView.setText("行：" + row + " 列:" + column);
        }

        return convertView;
    }


}
