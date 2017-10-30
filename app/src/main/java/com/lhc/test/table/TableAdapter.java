package com.lhc.test.table;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhc.table.BaseCommonTableAdapter;

import java.util.Comparator;
import java.util.List;

/**
 * 作者：lhc
 * 时间：2017/7/19.
 */

public class TableAdapter extends BaseCommonTableAdapter {
    private Context context;
    List<String> listofFirstRow;
    List<Model> listOfDatas;


    public TableAdapter(Context context, List<String> listOfFirstRow, List<Model> datas) {
        this.context = context;
        this.listOfDatas = datas;
        this.listofFirstRow = listOfFirstRow;
    }

    @Override
    public int getBodyRow() {
        return listOfDatas.size();
    }

    @Override
    public int getBodyColumn() {
        return listofFirstRow.size();
    }

    @Override
    public View getTitleView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_title, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        textView.setText("基金名称");
        return convertView;
    }

    @Override
    public View getFirstRowView(final int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_first_row, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        textView.setText(listofFirstRow.get(column % listofFirstRow.size()));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "第" + column + "列", Toast.LENGTH_SHORT).show();
//                Log.d("sort", "根据" + column + "排序");
//                Collections.sort(listOfDatas, TableAdapter.sort(column));
//                TableAdapter.this.notifyDateSetChange();
            }
        });
        return convertView;
    }

    @Override
    public View getFirstColumnView(int row, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_first_column, parent, false);
        }

        Model model = listOfDatas.get(row);

        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        TextView textCode = (TextView) convertView.findViewById(R.id.tv_code);
        textView.setText(model.getName());
        textCode.setText(model.getNet() + "");
        return convertView;
    }

    @Override
    public View getBodyView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_body, parent, false);
        }

        Model model = listOfDatas.get(row);
        Log.d("testlhc", "row:" + row + " column:" + column);
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        ll_container.setBackgroundColor(column % 2 == 0 ? Color.BLUE : Color.GRAY);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);

        switch (column) {
            case 1:
                textView.setText(model.getRevenueJan() + "");
                break;
            case 2:
                textView.setText(model.getRevenueFeb() + "");
                break;
            case 3:
                textView.setText(model.getRevenueMar() + "");
                break;
            case 4:
                textView.setText(model.getRevenueApr() + "");
                break;
            case 5:
                textView.setText(model.getRevenueMay() + "");
                break;
            case 6:
                textView.setText(model.getRevenueJune() + "");
                break;
            case 7:
                textView.setText(model.getRevenueJuly() + "");
                break;
            case 8:
                textView.setText(model.getRevenueAug() + "");
                break;
            case 9:
                textView.setText(model.getRevenueSept() + "");
                break;
            case 10:
                textView.setText(model.getRevenueOct() + "");
                break;
            case 11:
                textView.setText(model.getRevenueNov() + "");
                break;
            case 12:
                textView.setText(model.getRevenueDec() + "");
                break;


        }

        return convertView;
    }

    public static Comparator<Model> sort(int column) {
        switch (column) {
            case 2:
                return new Comparator<Model>() {
                    @Override
                    public int compare(Model o1, Model o2) {
                        return (int) (o1.getRevenueFeb() - o2.getRevenueFeb());
                    }
                };
            case 1:
                return new Comparator<Model>() {
                    @Override
                    public int compare(Model o1, Model o2) {
                        return (int) (o1.getRevenueJan() - o2.getRevenueJan());
                    }
                };
            default:
                return new Comparator<Model>() {
                    @Override
                    public int compare(Model o1, Model o2) {
                        return (int) (o1.getRevenueJan() - o2.getRevenueJan());
                    }
                };
        }
    }

}
