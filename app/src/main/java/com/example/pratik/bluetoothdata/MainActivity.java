package com.example.pratik.bluetoothdata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnSubmit;
    //    TextView txtDataReceived, lblCommandNo, txtOutput, lblmeterId,lblWDCounter,lblMeterTime;
//    EditText txtCommandNo;
    EditText txtMeterId;

    Handler bluetoothIn;

    String MeterId;
    int que = 0x51;  //Q 81
    int atdRate = 0x40; //@ 64
    int star = 0x2A; // * 42
    int semicolon = 0x3B;  //; 59
    int are = 0x52; // R 82

    StringBuilder packetFormat;
    String packetRequest;
    int wd;

    private static final String[] commandTypeId = new String[]{"180", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "170"}; // array to save area code
    private static final String[] commandTypeName = new String[]{"Select Command", "Flow Data Total", "Tamper/Bat_Low", "Flow_Below_THR/Above_THR", "Pipe Empty/Leakage", "No_Consumption/Reverse_Flow", "Reserved_1/Reserved_2", "Min1", "Min2", "Min3", "Min4", "Min5", "Max1", "Max2", "Max3", "Max4", "Max5", "Flow For Day", "Read Meter Time", "Read WD Counter", "Format Meter"}; // array to show location

    Spinner spCommandNo;
    String commandName;
    String commandId;
    private List<String> commandIdList = new ArrayList<String>();
    private List<String> commandNameList = new ArrayList<String>();
    ArrayAdapter<String> adpt_command;

    private final String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private List<String> weekdays = new ArrayList<String>();


    long packetFormatArray[] = new long[28];
    long receivedDataArray[] = new long[28];

    private Calendar calendar;
    private String yy, mm, dd, hh, min, ss;

    final int handlerState = 0;  //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    String dataReceived;

    String byteNo9, byteNo10, byteNo11, byteNo12, byteNo13, byteNo14, byteNo15, byteNo16, byteNo17, byteNo18, byteNo19, byteNo20, byteNo21, byteNo22, byteNo23, byteNo24, byteNo25, byteNo26, byteNo27;

    StringBuilder sbByteNo9 = new StringBuilder();
    StringBuilder sbByteNo10 = new StringBuilder();
    StringBuilder sbByteNo11 = new StringBuilder();
    StringBuilder sbByteNo12 = new StringBuilder();
    StringBuilder sbByteNo13 = new StringBuilder();
    StringBuilder sbByteNo14 = new StringBuilder();
    StringBuilder sbByteNo15 = new StringBuilder();
    StringBuilder sbByteNo16 = new StringBuilder();
    StringBuilder sbByteNo17 = new StringBuilder();
    StringBuilder sbByteNo18 = new StringBuilder();
    StringBuilder sbByteNo19 = new StringBuilder();
    StringBuilder sbByteNo20 = new StringBuilder();
    StringBuilder sbByteNo21 = new StringBuilder();
    StringBuilder sbByteNo22 = new StringBuilder();
    StringBuilder sbByteNo23 = new StringBuilder();
    StringBuilder sbByteNo24 = new StringBuilder();
    StringBuilder sbByteNo25 = new StringBuilder();
    StringBuilder sbByteNo26 = new StringBuilder();
    StringBuilder sbByteNo27 = new StringBuilder();
    StringBuilder rMid = new StringBuilder();
    StringBuilder rCId = new StringBuilder();
    String rMeterId = "";
    String rCommandNo = "";
    private ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        txtMeterId = (EditText) findViewById(R.id.txtMeterId);
        spCommandNo = (Spinner) findViewById(R.id.txtCommandType);
//        lblmeterId = (TextView) findViewById(R.id.lblmeterId);
//        lblCommandNo = (TextView) findViewById(R.id.lblCommandNo);
//        lblWDCounter = (TextView) findViewById(R.id.lblWDCounter);
//        lblMeterTime = (TextView) findViewById(R.id.lblMeterTime);
//        txtDataReceived = (TextView) findViewById(R.id.txtDataReceived);
//        txtOutput = (TextView) findViewById(R.id.txtOutput);
//        txtCommandNo = (EditText) findViewById(R.id.txtCommandNo);

        btnSubmit.setOnClickListener(this);

        calendar = Calendar.getInstance();
        dd = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        mm = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        yy = String.valueOf(calendar.get(Calendar.YEAR) % 100);

        hh = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        min = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
        ss = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));

        commandIdList = new ArrayList<String>(Arrays.asList(commandTypeId));
        commandNameList = new ArrayList<String>(Arrays.asList(commandTypeName));

        adpt_command = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, commandNameList);
        spCommandNo.setAdapter(adpt_command);
        spCommandNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    commandName = parent.getItemAtPosition(position).toString();
                    commandId = commandIdList.get(position);
                    //Toast.makeText(MainActivity.this, "U Select"+commandName+" "+commandId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {        //if message is what we want
//                    long readMessage[] = (long[]) msg.obj;    // msg.arg1 = bytes from connect thread

                    calculateDataByteWise();

                    StringBuilder RpacketFormat = new StringBuilder();
                    for (long value : receivedDataArray) {
                        RpacketFormat.append(value + " ");
                    }

                    dataReceived = RpacketFormat.toString();
                    //txtDataReceived.setText("Received from Bluetooth: " + dataReceived);
                    progressDialog.dismiss();
                    if (receivedDataArray[0] == 82 && receivedDataArray[1] == 64) {

                        long possition = receivedDataArray[7];

                        if (possition == 0) {
                            cmd0FlowDataTotal(rMeterId, rCommandNo);
                        } else if (possition >= 1 && possition <= 5) { //1 to 5
                            //flag A1-A10
                            cmd1to5Alarms(rMeterId, rCommandNo);
                        } else if (possition >= 6 && possition <= 10) { //6 to 8 & A
                            //min1-5
                            //getMin1To5(rMeterId,rCommandNo);
                        } else if (possition >= 11 && possition <= 15) { // B to F
                            //max1-5
                            //getMax1To5(rMeterId,rCommandNo);
                        } else if (possition == 16) { //10
                            //Flow for DD / MM

                        } else if (possition == 17) { //11
                            //Read Meter Time
                            cmd11ReadMeterTime(rMeterId, rCommandNo);
                        } else if (possition == 18) {  //12
                            //Read Meter Watchdog counter
                            cmd12ReadMeterWatchDogCounter(rMeterId, rCommandNo);
                        } else if (possition == 170) { //AA
                            //FORMAT METER
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Please Try Again. not getting proper data. ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }


    private void calculateDataByteWise() {
        byteNo9 = "";
        byteNo10 = "";
        byteNo11 = "";
        byteNo12 = "";
        byteNo13 = "";
        byteNo14 = "";
        byteNo15 = "";
        byteNo16 = "";
        byteNo17 = "";
        byteNo18 = "";
        byteNo19 = "";
        byteNo20 = "";
        byteNo21 = "";
        byteNo22 = "";
        byteNo23 = "";
        byteNo24 = "";
        byteNo25 = "";
        byteNo26 = "";
        byteNo27 = "";

        sbByteNo9 = new StringBuilder();
        sbByteNo10 = new StringBuilder();
        sbByteNo11 = new StringBuilder();
        sbByteNo12 = new StringBuilder();
        sbByteNo13 = new StringBuilder();
        sbByteNo14 = new StringBuilder();
        sbByteNo15 = new StringBuilder();
        sbByteNo16 = new StringBuilder();
        sbByteNo17 = new StringBuilder();
        sbByteNo18 = new StringBuilder();
        sbByteNo19 = new StringBuilder();
        sbByteNo20 = new StringBuilder();
        sbByteNo21 = new StringBuilder();
        sbByteNo22 = new StringBuilder();
        sbByteNo23 = new StringBuilder();
        sbByteNo24 = new StringBuilder();
        sbByteNo25 = new StringBuilder();
        sbByteNo26 = new StringBuilder();
        sbByteNo27 = new StringBuilder();

        rMid = new StringBuilder();
        rCId = new StringBuilder();

        rMeterId = "";
        rCommandNo = "";

        //find meterId from string
        for (int i = 2; i <= 5; i++) {
            String hex = Integer.toHexString((int) (receivedDataArray[i] & 0xFF));
            rMid.append(hex);
            rMeterId = String.valueOf(rMid);
        }
        rMeterId = String.valueOf(Integer.parseInt(rMeterId, 16));
        //lblmeterId.setText("Received from Bluetooth: " + rMeterId);

        //find command no from string
          rCommandNo = Integer.toHexString((int) (receivedDataArray[7] & 0xFF));


        String hex = Integer.toHexString((int) (receivedDataArray[9] & 0xFF));
        sbByteNo9.append(hex);
        byteNo9 = String.valueOf(sbByteNo9);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[10] & 0xFF));
        sbByteNo10.append(hex);
        byteNo10 = String.valueOf(sbByteNo10);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[11] & 0xFF));
        sbByteNo11.append(hex);
        byteNo11 = String.valueOf(sbByteNo11);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[12] & 0xFF));
        sbByteNo12.append(hex);
        byteNo12 = String.valueOf(sbByteNo12);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[13] & 0xFF));
        sbByteNo13.append(hex);
        byteNo13 = String.valueOf(sbByteNo13);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[14] & 0xFF));
        sbByteNo14.append(hex);
        byteNo14 = String.valueOf(sbByteNo14);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[15] & 0xFF));
        sbByteNo15.append(hex);
        byteNo15 = String.valueOf(sbByteNo15);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[16] & 0xFF));
        sbByteNo16.append(hex);
        byteNo16 = String.valueOf(sbByteNo16);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[17] & 0xFF));
        sbByteNo17.append(hex);
        byteNo17 = String.valueOf(sbByteNo17);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[18] & 0xFF));
        sbByteNo18.append(hex);
        byteNo18 = String.valueOf(sbByteNo18);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[19] & 0xFF));
        sbByteNo19.append(hex);
        byteNo19 = String.valueOf(sbByteNo19);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[20] & 0xFF));
        sbByteNo20.append(hex);
        byteNo20 = String.valueOf(sbByteNo20);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[21] & 0xFF));
        sbByteNo21.append(hex);
        byteNo21 = String.valueOf(sbByteNo21);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[22] & 0xFF));
        sbByteNo22.append(hex);
        byteNo22 = String.valueOf(sbByteNo22);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[23] & 0xFF));
        sbByteNo23.append(hex);
        byteNo23 = String.valueOf(sbByteNo23);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[24] & 0xFF));
        sbByteNo24.append(hex);
        byteNo24 = String.valueOf(sbByteNo24);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[25] & 0xFF));
        sbByteNo25.append(hex);
        byteNo25 = String.valueOf(sbByteNo25);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[26] & 0xFF));
        sbByteNo26.append(hex);
        byteNo26 = String.valueOf(sbByteNo26);
        hex = "";

        hex = Integer.toHexString((int) (receivedDataArray[27] & 0xFF));
        sbByteNo27.append(hex);
        byteNo27 = String.valueOf(sbByteNo27);
        hex = "";
    }

    private void cmd0FlowDataTotal(String rMeterId, String rCommandNo) {
        String kiloLiters = "";
        String liters = "";
        String idata = "";
        String fdata = "";
        String flag1 = "";
        String flag2 = "";
        String flag3 = "";
        String flag4 = "";
        String flag5 = "";
        StringBuilder kId = new StringBuilder();
        StringBuilder lId = new StringBuilder();
        StringBuilder iId = new StringBuilder();
        StringBuilder fId = new StringBuilder();
        StringBuilder f1Id = new StringBuilder();
        StringBuilder f2Id = new StringBuilder();
        StringBuilder f3Id = new StringBuilder();
        StringBuilder f4Id = new StringBuilder();
        StringBuilder f5Id = new StringBuilder();

        //find kilo liters from String
        for (int i = 9; i <= 12; i++) {

            String destohex = Integer.toHexString((int) receivedDataArray[i]);
            kId.append(destohex);
            kiloLiters = String.valueOf(kId);
            //kiloLiters = kiloLiters.replace("0","");
        }
        kiloLiters = String.valueOf(Integer.parseInt(kiloLiters, 16));

        //find kilo liters from String
        for (int i = 13; i <= 14; i++) {
            String destohex = Integer.toHexString((int) receivedDataArray[i]);
            lId.append(destohex);
            liters = String.valueOf(lId);

        }
        liters = String.valueOf(Integer.parseInt(liters, 16));

        //find I data liters from String
        for (int i = 16; i <= 17; i++) {
            iId.append(receivedDataArray[i]);
            idata = String.valueOf(iId);
            //idata = idata.replace("0","");
        }
        idata = String.valueOf(Integer.parseInt(idata, 16));

        //find F data liters from String
        for (int i = 18; i <= 19; i++) {
            fId.append(receivedDataArray[i]);
            fdata = String.valueOf(fId);
            //fdata = fdata.replace("0","");
        }
        fdata = String.valueOf(Integer.parseInt(fdata, 16));

        //find flag1 from string
        flag1 = Integer.toHexString((int) receivedDataArray[21]);
        flag2 = Integer.toHexString((int) receivedDataArray[22]);
        flag3 = Integer.toHexString((int) receivedDataArray[23]);
        flag4 = Integer.toHexString((int) receivedDataArray[24]);
        flag5 = Integer.toHexString((int) receivedDataArray[25]);

//        recDataString.delete(0, recDataString.length()); //clear all string data

        Intent receivedData = new Intent(MainActivity.this, Command_0.class);
        receivedData.putExtra("meterId", rMeterId);
        receivedData.putExtra("rCommandNo", rCommandNo);
        receivedData.putExtra("kiloLiters", kiloLiters);
        receivedData.putExtra("liters", liters);
        receivedData.putExtra("idata", idata);
        receivedData.putExtra("fdata", fdata);
        receivedData.putExtra("flag1", flag1);
        receivedData.putExtra("flag2", flag2);
        receivedData.putExtra("flag3", flag3);
        receivedData.putExtra("flag4", flag4);
        receivedData.putExtra("flag5", flag5);
        startActivity(receivedData);
    }

    private void cmd1to5Alarms(String rMeterId, String rCommandNo) {

        Intent comand1To5 = new Intent(this, Command_1to5.class);

        comand1To5.putExtra("MeterId", rMeterId);
        comand1To5.putExtra("CommandNo", rCommandNo);

        comand1To5.putExtra("FirstFlag", byteNo9);
        comand1To5.putExtra("FirstHH", byteNo10);
        comand1To5.putExtra("FirstMIN", byteNo11);
        comand1To5.putExtra("FirstSS", byteNo12);
        comand1To5.putExtra("FirstDD", byteNo13);
        comand1To5.putExtra("FirstMM", byteNo14);
        comand1To5.putExtra("FirstYY", byteNo15);

        comand1To5.putExtra("SecondFlag", byteNo17);
        comand1To5.putExtra("SecondHH", byteNo18);
        comand1To5.putExtra("SecondMIN", byteNo19);
        comand1To5.putExtra("SecondSS", byteNo20);
        comand1To5.putExtra("SecondDD", byteNo21);
        comand1To5.putExtra("SecondMM", byteNo22);
        comand1To5.putExtra("SecondYY", byteNo23);
//        comand1To5.putExtra("dataReceived",dataReceived);
//        comand1To5.putExtra("dataSent", packetRequest);
        startActivity(comand1To5);

    }

    private void cmd11ReadMeterTime(String rMeterId, String rCommandNo) {


        Intent comand11 = new Intent(this, Command_11.class);
        comand11.putExtra("MeterId", rMeterId);
        comand11.putExtra("CommandNo", rCommandNo);
        comand11.putExtra("HH", byteNo9);
        comand11.putExtra("MIN", byteNo10);
        comand11.putExtra("SS", byteNo11);
        comand11.putExtra("DD", byteNo13);
        comand11.putExtra("MM", byteNo14);
        comand11.putExtra("YY", byteNo15);
        comand11.putExtra("WD", byteNo16);
        startActivity(comand11);

//        lblCommandNo.setText("Command No: "+rCommandNo);
//        lblmeterId.setText("MeterId: "+rMeterId);
//        lblWDCounter.setText(byteNo16);
//        lblMeterTime.setText("HH: "+byteNo9+" Min: "+byteNo10+" SS: "+byteNo11+" DD: "+byteNo13+" MM: "+byteNo14+" YY: "+byteNo15);

    }

    private void cmd12ReadMeterWatchDogCounter(String rMeterId, String rCommandNo) {

//        lblWDCounter.setText("Watchdog Counter: "+byteNo9);
//        lblCommandNo.setText("Command No: "+rCommandNo);
//        lblmeterId.setText("MeterId: "+rMeterId);

        Intent comand12 = new Intent(this, Command_12.class);
        comand12.putExtra("MeterId", rMeterId);
        comand12.putExtra("CommandNo", rCommandNo);
        comand12.putExtra("WD", byteNo9);
        startActivity(comand12);
    }


    private void getMin1To5(String rMeterId, String rCommandNo) {

    }

    private void getMax1To5(String rMeterId, String rCommandNo) {

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSubmit) {

            MeterId =txtMeterId.getText().toString();
            if (commandName == null || commandName.isEmpty()) {
                Toast.makeText(this, "Please Select Command.", Toast.LENGTH_LONG).show();
            }
            else if (MeterId.equals("") || MeterId == null || MeterId.isEmpty()) {
                Toast.makeText(this, "Please Enter MeterId.", Toast.LENGTH_LONG).show();
            }
            else {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Logging In.");
                progressDialog.show();

                MeterId = Integer.toHexString(Integer.parseInt(MeterId));
                int mIdLength = MeterId.length();
                int i = 2;

                if (mIdLength == 1) {
                    packetFormatArray[5] = Long.parseLong(MeterId, 16);
                } else if (mIdLength == 2) {
                    packetFormatArray[5] = Long.parseLong(MeterId, 16);
                } else if (mIdLength == 3) {
                    char str = MeterId.charAt(0);
                    packetFormatArray[4] = Long.parseLong(String.valueOf(str), 16);
                    String last2char = MeterId.length() > 2 ? MeterId.substring(MeterId.length() - 2) : MeterId;
                    packetFormatArray[5] = Long.parseLong(String.valueOf(last2char), 16);
                } else if (mIdLength == 4) {

                    String last2char = MeterId.length() > 2 ? MeterId.substring(MeterId.length() - 2) : MeterId;
                    packetFormatArray[5] = Long.parseLong(String.valueOf(last2char), 16);
                    String first2char = MeterId.substring(0, 2);
                    packetFormatArray[4] = Long.parseLong(String.valueOf(first2char), 16);
                } else if (mIdLength == 5) {

                    char str = MeterId.charAt(0);
                    packetFormatArray[3] = Long.parseLong(String.valueOf(str), 16);

                    String first2char = MeterId.substring(1, 3);
                    packetFormatArray[4] = Long.parseLong(String.valueOf(first2char), 16);

                    String last2char = MeterId.length() > 2 ? MeterId.substring(MeterId.length() - 2) : MeterId;
                    packetFormatArray[5] = Long.parseLong(String.valueOf(last2char), 16);
                } else if (mIdLength == 6) {

                    String first2char = MeterId.substring(0, 2);
                    packetFormatArray[3] = Long.parseLong(String.valueOf(first2char), 16);

                    String second2char = MeterId.substring(2, 4);
                    packetFormatArray[4] = Long.parseLong(String.valueOf(second2char), 16);

                    String last2char = MeterId.length() > 2 ? MeterId.substring(MeterId.length() - 2) : MeterId;
                    packetFormatArray[5] = Long.parseLong(String.valueOf(last2char), 16);
                } else if (mIdLength == 7) {

                    char str = MeterId.charAt(0);
                    packetFormatArray[2] = Long.parseLong(String.valueOf(str), 16);

                    String first2char = MeterId.substring(1, 3);
                    packetFormatArray[3] = Long.parseLong(String.valueOf(first2char), 16);

                    String second2char = MeterId.substring(3, 5);
                    packetFormatArray[4] = Long.parseLong(String.valueOf(second2char), 16);

                    String last2char = MeterId.length() > 2 ? MeterId.substring(MeterId.length() - 2) : MeterId;
                    packetFormatArray[5] = Long.parseLong(String.valueOf(last2char), 16);
                } else if (mIdLength == 8) {

                    String first2char = MeterId.substring(0, 2);
                    packetFormatArray[2] = Long.parseLong(String.valueOf(first2char), 16);

                    String second2char = MeterId.substring(2, 4);
                    packetFormatArray[3] = Long.parseLong(String.valueOf(second2char), 16);

                    String third2char = MeterId.substring(4, 6);
                    packetFormatArray[4] = Long.parseLong(String.valueOf(third2char), 16);

                    String last2char = MeterId.length() > 2 ? MeterId.substring(MeterId.length() - 2) : MeterId;
                    packetFormatArray[5] = Long.parseLong(String.valueOf(last2char), 16);
                }
                if (commandId.equals("0") || commandId.equals("170")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    Date d = new Date();
                    String dayOfTheWeek = sdf.format(d);
                    weekdays = new ArrayList<String>(Arrays.asList(days));
                    wd = weekdays.indexOf(dayOfTheWeek);
                    packetFormatArray[0] = que;
                    packetFormatArray[1] = atdRate;
                    packetFormatArray[6] = star;
                    packetFormatArray[7] = Long.parseLong(commandId);
                    packetFormatArray[8] = star;
                    packetFormatArray[9] = Long.parseLong(hh, 16);
                    packetFormatArray[10] = Long.parseLong(min, 16);
                    packetFormatArray[11] = Long.parseLong(ss, 16);
                    packetFormatArray[12] = star;
                    packetFormatArray[13] = Long.parseLong(dd, 16);
                    packetFormatArray[14] = Long.parseLong(mm, 16);
                    packetFormatArray[15] = Long.parseLong(yy, 16);
                    packetFormatArray[16] = wd;
                    packetFormatArray[17] = semicolon;
                    packetFormatArray[18] = semicolon;
                } else if (commandId.equals("10")) {
                    packetFormatArray[0] = que;
                    packetFormatArray[1] = atdRate;
                    packetFormatArray[6] = star;
                    packetFormatArray[7] = Long.parseLong(commandId);
                    packetFormatArray[8] = star;
                    packetFormatArray[9] = Long.parseLong(dd, 16);
                    packetFormatArray[10] = Long.parseLong(mm, 16);
                    packetFormatArray[11] = semicolon;
                    packetFormatArray[12] = semicolon;
                } else {
                    packetFormatArray[0] = que;
                    packetFormatArray[1] = atdRate;
                    packetFormatArray[6] = star;
                    packetFormatArray[7] = Long.parseLong(commandId);
                    packetFormatArray[8] = semicolon;
                    packetFormatArray[9] = semicolon;
                }
                packetFormat = new StringBuilder();
                for (long value : packetFormatArray) {
                    packetFormat.append(value + " ");
                }
                packetRequest = packetFormat.toString();
                //txtOutput.setText("Sent Data: " + packetRequest);
                mConnectedThread.write(packetFormatArray);
            }

        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        if (address != null) {
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
            }
            // Establish the Bluetooth socket connection.
            try {
                btSocket.connect();
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    //insert code to deal with this
                }
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {

            byte[] buffer = new byte[28];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    // Get number of bytes and message in "buffer"
                    long receivedDataArray2[] = new long[28];
                    for (int i = 0; i < buffer.length; i++) {
                        bytes = mmInStream.read();
                        receivedDataArray2[i] = bytes;
                    }
                    receivedDataArray = receivedDataArray2;
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, receivedDataArray).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(long[] packetFormatArray) {
            //converts entered String into bytes
            try {
                for (int i = 0; i < packetFormatArray.length; i++) {
                    int msgBuffer = (int) packetFormatArray[i];
                    mmOutStream.write(msgBuffer);
                }
                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                //Toast.makeText(getBaseContext(), "error:" + e, Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
