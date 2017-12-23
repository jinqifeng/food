package com.chaoyu.jongwn.taximeter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class TariffActivity extends AppCompatActivity {
    private SharedPreferences mPref;
    private TextView tvInitCh,tvInitCh_n, tvTo1, tvCh1, tvFrom2,tvTo1_n, tvCh1_n, tvFrom2_n,tvCh2_n,
            tvCh2, tvWCh,tvMinim,tvMax,tvTax;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tariff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        mPref = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
     //   SharedPreferences sp = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);

        tvInitCh = (TextView) findViewById(R.id.init_charge);
        tvInitCh_n = (TextView) findViewById(R.id.init_charge_night);
        tvTo1 = (TextView) findViewById(R.id.tvTo1);
        tvCh1 = (TextView) findViewById(R.id.tvChgP1);
        tvFrom2 = (TextView) findViewById(R.id.tvFrom2);
        tvCh2 = (TextView) findViewById(R.id.tvChgP2);
        tvWCh = (TextView) findViewById(R.id.wait_charge);
        tvTo1_n = (TextView) findViewById(R.id.tvTo1_night);
        tvCh1_n = (TextView) findViewById(R.id.tvChgP1_night);
        tvFrom2_n = (TextView) findViewById(R.id.tvFrom2_night);
        tvCh2_n = (TextView) findViewById(R.id.tvChgP2_night);
        tvWCh = (TextView) findViewById(R.id.wait_charge);
        tvMinim = (TextView) findViewById(R.id.minimum);
        tvMax = (TextView) findViewById(R.id.max);
        tvTax = (TextView) findViewById(R.id.tax);

        getPreferencesData();

    }
    private void getPreferencesData() {
        String init=" ";
        init=mPref.getString("initial_charge", "0");
        tvInitCh.setText("" + init );
        init=mPref.getString("waiting_charge", "0");
        tvWCh.setText(init);
        init= mPref.getString("minimun_fare", "0");
        tvMinim.setText(init);
        init= mPref.getString("maximum_fare", "0");
        tvMax.setText(init);
        init=  mPref.getString("tax", "0");
        tvTax.setText(init);
        init=mPref.getString("initial_charge_n", "0");
        tvInitCh_n.setText("" + init );
        SharedPreferences ref = PreferenceManager.getDefaultSharedPreferences(this);
        if (ref.contains("distance_charge1")) {
            init= ref.getString("distance_charge1", "0");
            // mPref.getString("distance_charge", init);
            String[] split = init.split("-");
            tvTo1.setText(split[0]);
            tvCh1.setText(split[1]);
            tvFrom2.setText(split[0]);
            tvCh2.setText(split[2]);
        }else{
            tvTo1.setText("0.00");
            tvCh1.setText("0.00");
            tvFrom2.setText("0.00");
            tvCh2.setText("0.00");
        }
        if (ref.contains("distance_charge2")) {
            init=  ref.getString("distance_charge2", "0");
            // mPref.getString("distance_charge", init);
            String[] split = init.split("-");
            tvTo1_n.setText(split[0]);
            tvCh1_n.setText(split[1]);
            tvFrom2_n.setText(split[0]);
            tvCh2_n.setText(split[2]);
        }else{
            tvTo1_n.setText("0.00");
            tvCh1_n.setText("0.00");
            tvFrom2_n.setText("0.00");
            tvCh2_n.setText("0.00");
        }


    }
}
