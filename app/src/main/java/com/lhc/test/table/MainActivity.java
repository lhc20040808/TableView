package com.lhc.test.table;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lhc.table.TableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TableView tableView;
    List<String> listofFirstRow = new ArrayList<>();
    List<Model> listOfDatas = new ArrayList<>();
    TableAdapter mAdapter;
    Random r = new Random();


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("Table", "获取数据");
            Model tmp = null;
            for (int i = 0; i < 15; i++) {
                tmp = new Model();
                tmp.setName("Name" + i);
                tmp.setDes("des" + i);
                tmp.setNet(r.nextInt(10) * 1.0);
                tmp.setRevenueJan(r.nextInt(10) * 1.0);
                tmp.setRevenueFeb(r.nextInt(10) * 1.0);
                tmp.setRevenueMar(r.nextInt(10) * 1.0);
                tmp.setRevenueApr(r.nextInt(10) * 1.0);
                tmp.setRevenueMay(r.nextInt(10) * 1.0);
                tmp.setRevenueJune(r.nextInt(10) * 1.0);
//                tmp.setRevenueJuly(r.nextInt(10) * 1.0);
//                tmp.setRevenueAug(r.nextInt(10) * 1.0);
//                tmp.setRevenueSept(r.nextInt(10) * 1.0);
//                tmp.setRevenueOct(r.nextInt(10) * 1.0);
//                tmp.setRevenueNov(r.nextInt(10) * 1.0);
//                tmp.setRevenueDec(r.nextInt(10) * 1.0);
                listOfDatas.add(tmp);
            }


//            listofFirstRow.add("七月收益");
//            listofFirstRow.add("八月收益");
//            listofFirstRow.add("九月收益");
//            listofFirstRow.add("十月收益");
//            listofFirstRow.add("十一月收益");
//            listofFirstRow.add("十二月收益");

            mAdapter.notifyDateSetChange();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tableView = (TableView) findViewById(R.id.tableView);

        listofFirstRow.add("净值");
        listofFirstRow.add("一月收益");
        listofFirstRow.add("二月收益");
        listofFirstRow.add("三月收益");
        listofFirstRow.add("四月收益");
        listofFirstRow.add("五月收益");
        listofFirstRow.add("六月收益");

        tableView.setAdapter(mAdapter = new TableAdapter(this, listofFirstRow, listOfDatas));

        handler.sendEmptyMessageDelayed(0, 3000);

    }

    public void sort(View view) {
        Collections.sort(listOfDatas, TableAdapter.sort(3));
        mAdapter.notifyDateSetChange();
    }


}
