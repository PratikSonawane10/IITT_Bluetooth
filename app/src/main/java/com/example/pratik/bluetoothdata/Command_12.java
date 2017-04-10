package com.example.pratik.bluetoothdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Command_12 extends AppCompatActivity implements View.OnClickListener{

    TextView meterId;
    TextView cmdId;
    TextView txtWD;

    String mid;
    String cid;
    String wd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.command_12);

        meterId = (TextView) findViewById(R.id.txtMeterId);
        cmdId = (TextView) findViewById(R.id.txtCommandId);
        txtWD = (TextView) findViewById(R.id.txtWd);

        Intent intent = getIntent();
        if (null != intent) {
            mid = intent.getStringExtra("MeterId");
            cid = intent.getStringExtra("CommandNo");
            wd = intent.getStringExtra("WD");
        }

        meterId.setText(mid);
        cmdId.setText(cid);

        txtWD.setText(wd);

    }
    @Override
    public void onClick(View v) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}