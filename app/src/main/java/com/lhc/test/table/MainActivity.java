package com.lhc.test.table;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lhc.table.TableView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TableView tableView;
    List<String> listofFirstRow = new ArrayList<>();
    List<Model> listofFirstColumn = new ArrayList<>();
    TableAdapter mAdapter;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("Table","获取数据");
            Model tmp = null;
            for (int i = 0; i < 30; i++) {
                tmp = new Model();
                tmp.setName("Name" + i);
                tmp.setDes("des" + i);
                listofFirstColumn.add(tmp);
                listofFirstRow.add("类型" + i);
            }
            mAdapter.notifyDateSetChange();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tableView = (TableView) findViewById(R.id.tableView);
        tableView.setAdapter(mAdapter = new TableAdapter(this, listofFirstRow, listofFirstColumn));

        handler.sendEmptyMessageDelayed(0,3000);

    }
}
