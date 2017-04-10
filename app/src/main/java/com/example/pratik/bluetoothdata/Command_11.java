package com.example.pratik.bluetoothdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Command_11 extends AppCompatActivity implements View.OnClickListener{

    TextView meterId;
    TextView cmdId;

    TextView txtDate;
    TextView txtTime;

    String mid;
    String cid;
    String date, hh,min,ss;
    String time, dd,mm,yy;
    String wd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.command_11);

        meterId = (TextView) findViewById(R.id.txtMeterId);
        cmdId = (TextView) findViewById(R.id.txtCommandId);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTime = (TextView) findViewById(R.id.txtTime);


        Intent intent = getIntent();
        if (null != intent) {
            mid = intent.getStringExtra("MeterId");
            cid = intent.getStringExtra("CommandNo");

            hh = intent.getStringExtra("HH");
            min = intent.getStringExtra("MIN");
            ss = intent.getStringExtra("SS");
            dd = intent.getStringExtra("DD");
            mm = intent.getStringExtra("MM");
            yy = intent.getStringExtra("YY");
            wd = intent.getStringExtra("WD");

        }

        date = dd+"-"+mm+"-"+yy;
        time = hh+"-"+min+"-"+ss;

        meterId.setText(mid);
        cmdId.setText(cid);

        txtDate.setText(date);
        txtTime.setText(time);

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

