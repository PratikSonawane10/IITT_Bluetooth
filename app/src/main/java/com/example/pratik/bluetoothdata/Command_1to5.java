package com.example.pratik.bluetoothdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Command_1to5 extends AppCompatActivity implements View.OnClickListener{

    TextView meterId;
    TextView cmdId;

    TextView txtFlag1Name;
    TextView txtFlag2Name;
    TextView txtFlag1Date;
    TextView txtFlag2Date;
    TextView txtFlag2Time;
    TextView txtFlag1Time;

    String mid;
    String cid;
    String flag1Name;
    String flag1NameValue;
    String flag2Name;
    String flag2NameValue;

    String fhh;
    String fmin;
    String fss;
    String fdd;
    String fmm;
    String fyy;

    String shh;
    String smin;
    String sss;
    String sdd;
    String smm;
    String syy;

    String f1Date;
    String f1Time;
    String f2Date;
    String f2Time;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.command_1to5);

        meterId = (TextView) findViewById(R.id.meterId);
        cmdId = (TextView) findViewById(R.id.cmdId);
        txtFlag1Name = (TextView) findViewById(R.id.lblFlag1Name);
        txtFlag1Date = (TextView) findViewById(R.id.txtFlag1Date);
        txtFlag1Time = (TextView) findViewById(R.id.txtFlag1Time);

        txtFlag2Name = (TextView) findViewById(R.id.lblFlag2Name);
        txtFlag2Date = (TextView) findViewById(R.id.txtFlag2Date);
        txtFlag2Time = (TextView) findViewById(R.id.txtFlag2Time);



        Intent intent = getIntent();
        if (null != intent) {
            mid = intent.getStringExtra("MeterId");
            cid = intent.getStringExtra("CommandNo");

            flag1NameValue = intent.getStringExtra("FirstFlag");

            fhh = intent.getStringExtra("FirstHH");
            fmin = intent.getStringExtra("FirstMIN");
            fss = intent.getStringExtra("FirstSS");
            fdd = intent.getStringExtra("FirstDD");
            fmm = intent.getStringExtra("FirstMM");
            fyy = intent.getStringExtra("FirstYY");

            flag2NameValue = intent.getStringExtra("SecondFlag");
            shh = intent.getStringExtra("SecondHH");
            smin = intent.getStringExtra("SecondMIN");
            sss = intent.getStringExtra("SecondSS");
            sdd = intent.getStringExtra("SecondDD");
            smm = intent.getStringExtra("SecondMM");
            syy = intent.getStringExtra("SecondYY");
        }

        f1Date = fdd+"-"+fmm+"-"+fyy;
        f1Time = fhh+"-"+fmin+"-"+fss;
        f2Date = sdd+"-"+smm+"-"+syy;
        f2Time = shh+"-"+smin+"-"+sss;

        if(cid.equals("1")){
            flag1Name = "Tamper "+flag1NameValue;
            flag2Name = "Low_Bat "+flag2NameValue;
        }
        else if(cid.equals("2")){
            flag1Name = "Flow_Below_THR "+flag1NameValue;
            flag2Name = "Flow_Above_THR "+flag2NameValue;
        }
        else if(cid.equals("3")){
            flag1Name = "Pipe Empty "+flag1NameValue;
            flag2Name = "Leakage "+flag2NameValue;
        }
        else if(cid.equals("4")){
            flag1Name = "No_Consumption "+flag1NameValue;
            flag2Name = "Reverse_Flow "+flag2NameValue;
        }
        else if(cid.equals("5")){
            flag1Name = "Reserved_1 "+flag1NameValue;
            flag2Name = "Reserved_2 "+flag2NameValue;
        }
        if(flag1NameValue.equals("1")){
            txtFlag1Name.setTextColor(getResources().getColor(R.color.colorGreen500));
        }
        else if(flag1NameValue.equals("0")){
            txtFlag1Name.setTextColor(getResources().getColor(R.color.colorred500));
        }

         if(flag2NameValue.equals("1")){
            txtFlag2Name.setTextColor(getResources().getColor(R.color.colorGreen500));
        }

        else if(flag2NameValue.equals("0")){
            txtFlag2Name.setTextColor(getResources().getColor(R.color.colorred500));
        }

        meterId.setText(mid);
        cmdId.setText(cid);

        txtFlag1Name.setText(flag1Name);
        txtFlag2Name.setText(flag2Name);

        txtFlag1Date.setText(f1Date);
        txtFlag1Time.setText(f1Time);
        txtFlag2Date.setText(f2Date);
        txtFlag2Time.setText(f2Time);

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
