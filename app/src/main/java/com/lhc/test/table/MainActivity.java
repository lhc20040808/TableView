package com.lhc.test.table;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lhc.table.TableView;

public class MainActivity extends AppCompatActivity {
    TableView tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tableView = (TableView) findViewById(R.id.tableView);
        tableView.setAdapter(new TableAdapter(this));
    }
}
