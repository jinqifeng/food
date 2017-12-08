package com.chaoyu.jongwn.taximeter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {
    private Button btnStart, btnPrint, btnHistory, btnSettings, btnTariff;
    private TextView tvDistance, tvWait, tvSpeed, tvFare;
    private RadioGroup rgDayNight;

    private boolean isProcessActive;
    private boolean isGPSActive;

    private double price = 0, distance = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        initWidgets();
    }
    private void initWidgets() {
        btnStart = (Button) findViewById(R.id.btnStartEnd);
        btnStart.setOnClickListener(this);

        btnPrint = (Button) findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(this);

        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(this);

        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

        btnTariff = (Button) findViewById(R.id.btnTariff);
        btnTariff.setOnClickListener(this);

        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvWait = (TextView) findViewById(R.id.tvWaitTime);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvFare = (TextView) findViewById(R.id.tvFare);

        rgDayNight = (RadioGroup) findViewById(R.id.WorkingStatus);

    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnStartEnd:
                if (isGPSActive) {
                    if (!isProcessActive) {
                        //TODO start service;

                        isProcessActive = true;
                        btnStart.setText("Пауза");

                        distance = 0;
                        price = 0;

                    } else {
                        //TODO pause

                        btnStart.setText("Старт");
                        isProcessActive = false;

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Поиск спутников GPS, ожидайте", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnHistory:

                break;
            case R.id.btnPrint:

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
}
