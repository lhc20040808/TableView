package com.lhc.test.table;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhc.table.BaseCommonTableAdapter;

import java.util.List;

/**
 * 作者：lhc
 * 时间：2017/7/19.
 */

public class TableAdapter extends BaseCommonTableAdapter {
    private Context context;
    List<String> listofFirstRow;
    List<Model> listofFirstColumn;


    public TableAdapter(Context context, List<String> listOfFirstRow, List<Model> listOfFirstColumn) {
        this.context = context;
        this.listofFirstColumn = listOfFirstColumn;
        this.listofFirstRow = listOfFirstRow;
    }

    @Override
    public int getRow() {
        return listofFirstColumn.size();
    }

    @Override
    public int getColumn() {
        return listofFirstRow.size();
    }

    @Override
    public View getTitleView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_title, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        textView.setText("Title");
        return convertView;
    }

    @Override
    public View getFirstRowView(int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_first_row, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        if (column % 2 == 0) {
            textView.setText("type2");
        } else {
            textView.setText("type1");
        }
        return convertView;
    }

    @Override
    public View getFirstColumnView(int row, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_first_column, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        TextView textCode = (TextView) convertView.findViewById(R.id.tv_code);
        textView.setText("Name");
        textCode.setText("code" + row);
        return convertView;
    }

    @Override
    public View getBodyView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_body, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        textView.setText("行:" + row + " 列:" + column);

        textView.setTextColor(Color.parseColor("#ffffff"));
        ll_container.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.table_bg));
        return convertView;
    }


}
