package com.chaoyu.jongwn.taximeter;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chaoyu.jongwn.taximeter.database.DBHelper;
import com.chaoyu.jongwn.taximeter.service.CounterService;
import com.chaoyu.jongwn.taximeter.utils.util.TaximeterUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {
    private Button btnStart, btnEnd;
    private ImageButton btnPrint, btnHistory, btnSettings, btnTariff;
    private TextView tvDistance, tvWait, tvSpeed, tvFare;
    private RadioGroup rgDayNight;

    private boolean isProcessActive;
    private boolean isGPSActive;
    private CounterService counterService;
    private boolean isServiceBound;
    private double price = 0, distance = 0;
    private double prefMinTarif, prefTarifEnter, prefTarif;

    public static final String PARAM_INTENT = "pendingIntent";
    public static final String DISTANCE = "distance";
    public static final String PARAM_STATUS_GPS = "gps_status";
    public static final int PARAM_CODE_DISTANCE = 333;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   //     Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
   //     setSupportActionBar(myToolbar);
        initWidgets();
        initCounterService();
    }
    private void initWidgets() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        btnEnd = (Button) findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(this);
        btnPrint = (ImageButton) findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(this);

        btnHistory = (ImageButton) findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(this);

        btnSettings = (ImageButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

        btnTariff = (ImageButton) findViewById(R.id.btnTariff);
        btnTariff.setOnClickListener(this);

        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvWait = (TextView) findViewById(R.id.tvWaitTime);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvFare = (TextView) findViewById(R.id.tvFare);



    }
    private boolean writeToDB(String sum, String distance) {
        DBHelper dbHelper = new DBHelper(this, null);

        ContentValues cv = new ContentValues();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        cv.put("date", date);
        cv.put("sum", sum);
        cv.put("distance", distance);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert(DBHelper.tableName, null, cv);
        db.close();
        dbHelper.close();
        return true;
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnStart:
                if (isGPSActive) {
                    if (!isProcessActive) {
                        //TODO start service;

                        isProcessActive = true;
                        btnStart.setEnabled(false);
                        btnEnd.setEnabled(true);
                        distance = 0;
                        price = 0;

                    } else {
                        //TODO pause
                        counterService.pauseCounter();
                        //btnStart.setText("Старт");
                        isProcessActive = false;

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "GPS don,t work", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnEnd:
                isProcessActive = false;
                btnStart.setEnabled(true);
                btnEnd.setEnabled(false);
                counterService.stopCounter();
            //    writeToDB(tvPrice.getText().toString(), tvDistance.getText().toString());

                DecimalFormat df = new DecimalFormat("0.00");

                tvFare.setText(df.format(calculatePrice(distance)));


                distance = 0;
                price = 0;

                break;
            case R.id.btnHistory:
                if (!isProcessActive) {
                    intent = new Intent(this, HistoryActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Please re-try after complete hire ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnPrint:
                if (!isProcessActive) {
                    intent = new Intent(this, PrintActivity.class);
                    startActivity(intent);
                } else {
                   Toast.makeText(getApplicationContext(), "Please complete hire", Toast.LENGTH_SHORT).show();
               }
                break;
            case R.id.btnSettings:
                if (!isProcessActive) {
                    intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Current it is working!. Afre finishing, retry to do it", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnTariff:
               // intent = new Intent(this, TariffActivity.class);
               // startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case PARAM_CODE_DISTANCE:
                isGPSActive = data.getBooleanExtra(MainActivity.PARAM_STATUS_GPS, false);
           /*     if (isGPSActive) {
                    updateGPSStatus("Спутники найдены");
                }*/
                distance += data.getDoubleExtra(MainActivity.DISTANCE, 0);
                if (isProcessActive) {
                    updateInfo(distance);
                }
                Log.d(TaximeterUtils.LOG_TAG, isGPSActive + " " + distance);
                break;


        }
    }
    private void updateInfo(double distance) {
        price = calculatePrice(distance);

        DecimalFormat df = new DecimalFormat("0.00");

        tvDistance.setText(String.valueOf(df.format(distance / 1000)) + " км");
//        tvPrice.setText(df.format(price) + " uah");

        Log.d(TaximeterUtils.LOG_TAG, (distance / 1000) + " " + price);
    }
    private double calculatePrice(double distance) {
        if (distance < 5 * 1000) {
            return prefMinTarif;
        } else {
            price = prefTarif * (distance / 1000) + prefTarifEnter;
        }
        return price;
    }
    private void initCounterService() {
        PendingIntent pendingIntent = createPendingResult(PARAM_CODE_DISTANCE, new Intent(), 0);
        Intent intent = new Intent(getApplicationContext(), CounterService.class).putExtra(PARAM_INTENT, pendingIntent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CounterService.LocalBinder binder = (CounterService.LocalBinder) iBinder;
            counterService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
        }
    };
}
