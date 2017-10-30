package com.lhc.test.table;

import android.os.Bundle;
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
//        listofFirstRow.add("五月收益");
//        listofFirstRow.add("六月收益");
//        listofFirstRow.add("七月收益");

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
            tmp.setRevenueJuly(r.nextInt(10) * 1.0);
            listOfDatas.add(tmp);
        }

        tableView.setAdapter(mAdapter = new TableAdapter(this, listofFirstRow, listOfDatas));

    }

    public void sort(View view) {
        Collections.sort(listOfDatas, TableAdapter.sort(3));
        mAdapter.notifyDateSetChange();
    }

    public void checkVertical(View view) {
        Log.d("testlhc", "能否向上滑动:" + tableView.canScrollVertically(-1));
        Log.d("testlhc", "能否向下滑动:" + tableView.canScrollVertically(1));
    }

    public void checkHorizontal(View view) {
        Log.d("testlhc", "能否向左滑动:" + tableView.canScrollHorizontally(-1));
        Log.d("testlhc", "能否向右滑动:" + tableView.canScrollHorizontally(1));
    }

    public void add(View view) {
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
            tmp.setRevenueJuly(r.nextInt(10) * 1.0);
            listOfDatas.add(tmp);
        }

        mAdapter.notifyDateSetChange();
    }

    public void change(View view) {
        final int size = listOfDatas.size();
        listOfDatas.clear();
        Model tmp = null;
        for (int i = 0; i < size; i++) {
            tmp = new Model();
            tmp.setName("Change" + i);
            tmp.setDes("Cdes" + i);
            tmp.setNet(r.nextInt(10) * 1.0);
            tmp.setRevenueJan(r.nextInt(10) * 1.0);
            tmp.setRevenueFeb(r.nextInt(10) * 1.0);
            tmp.setRevenueMar(r.nextInt(10) * 1.0);
            tmp.setRevenueApr(r.nextInt(10) * 1.0);
            tmp.setRevenueMay(r.nextInt(10) * 1.0);
            tmp.setRevenueJune(r.nextInt(10) * 1.0);
            tmp.setRevenueJuly(r.nextInt(10) * 1.0);
            listOfDatas.add(tmp);
        }
        mAdapter.notifyDateSetChange();
    }

    public void sub(View view) {
        for (int i = 0; i < 3; i++)
            listOfDatas.remove(listOfDatas.size() - 1);

        mAdapter.notifyDateSetChange();
    }


}
