package com.lhc.test.table;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
        return 20;
    }

    @Override
    public int getColumn() {
        return 20;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        textView.setText("行：" + row + " 列:" + column);
        if (row == 0 && column == 0) {
            textView.setText("时间\\月份");
        }

        if (row == 0) {
            textView.setTextColor(Color.parseColor("#000000"));
            ll_container.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            textView.setTextColor(Color.parseColor("#ffffff"));
            ll_container.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.table_bg));
        }

        return convertView;
    }


}
