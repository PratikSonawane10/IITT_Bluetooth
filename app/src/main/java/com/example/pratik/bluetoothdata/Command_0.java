package com.example.pratik.bluetoothdata;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Command_0 extends AppCompatActivity implements View.OnClickListener{

    TextView meterId;
    TextView cmdId;
    TextView kiloLiters;
    TextView liters;
    TextView iData;
    TextView fData;
    TextView flag1;
    TextView flag2;
    TextView flag3;
    TextView flag4;
    TextView flag5;

    TextView flagName1;
    TextView flagName2;
    TextView flagName3;
    TextView flagName4;
    TextView flagName5;


    String mid;
    String cid;
    String kLtrs;
    String ltrs;
    String idta;
    String fdta;
    String f1;
    String f2;
    String f3;
    String f4;
    String f5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commandno_0);

        meterId = (TextView) findViewById(R.id.txtMeterId);
        cmdId = (TextView) findViewById(R.id.txtCommandId);
        kiloLiters = (TextView) findViewById(R.id.txtKLiters);
        liters = (TextView) findViewById(R.id.txtLiters);
        iData = (TextView) findViewById(R.id.iData);
        fData = (TextView) findViewById(R.id.fData);
        flag1 = (TextView) findViewById(R.id.txtAlarm1);
        flag2 = (TextView) findViewById(R.id.txtAlarm2);
        flag3 = (TextView) findViewById(R.id.txtAlarm3);
        flag4 = (TextView) findViewById(R.id.txtAlarm4);
        flag5 = (TextView) findViewById(R.id.txtAlarm5);

        flagName1 = (TextView) findViewById(R.id.lblAlarmName1);
        flagName2 = (TextView) findViewById(R.id.lblAlarmName2);
        flagName3 = (TextView) findViewById(R.id.lblAlarmName3);
        flagName4 = (TextView) findViewById(R.id.lblAlarmName4);
        flagName5 = (TextView) findViewById(R.id.lblAlarmName5);

        Intent intent = getIntent();
		if (null != intent) {
            mid = intent.getStringExtra("meterId");
            cid = intent.getStringExtra("rCommandNo");
            kLtrs = intent.getStringExtra("kiloLiters");
            ltrs = intent.getStringExtra("liters");
            idta = intent.getStringExtra("idata");
            fdta = intent.getStringExtra("fdata");
            f1 = intent.getStringExtra("flag1");
            f2 = intent.getStringExtra("flag2");
            f3 = intent.getStringExtra("flag3");
            f4 = intent.getStringExtra("flag4");
            f5 = intent.getStringExtra("flag5");
		}

        meterId.setText(mid);
        cmdId.setText(cid);
        kiloLiters.setText(kLtrs+" kltrs");
        liters.setText(ltrs+" ltrs");

        iData.setText(idta);
        fData.setText(fdta);
        flag1.setText(f1);
        flag2.setText(f2);
        flag3.setText(f3);
        flag4.setText(f4);
        flag5.setText(f5);

        if(f1.equals("0")){
            flagName1.setTextColor(getResources().getColor(R.color.colorred500));
        }
        else {
            flagName1.setTextColor(getResources().getColor(R.color.colorGreen500));
        }

        if(f2.equals("0")){
            flagName2.setTextColor(getResources().getColor(R.color.colorred500));
        }
        else {
            flagName2.setTextColor(getResources().getColor(R.color.colorGreen500));
        }

        if(f3.equals("0")){
            flagName3.setTextColor(getResources().getColor(R.color.colorred500));
        }
        else {
            flagName3.setTextColor(getResources().getColor(R.color.colorGreen500));
        }

        if(f4.equals("0")){
            flagName4.setTextColor(getResources().getColor(R.color.colorred500));
        }
        else {
            flagName4.setTextColor(getResources().getColor(R.color.colorGreen500));
        }
        if(f5.equals("0")){
            flagName5.setTextColor(getResources().getColor(R.color.colorred500));
        }
        else{
            flagName5.setTextColor(getResources().getColor(R.color.colorGreen500));
        }

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
