package com.chaoyu.jongwn.taximeter;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chaoyu.jongwn.taximeter.database.DBHelper;
import com.chaoyu.jongwn.taximeter.service.CounterService;
import com.chaoyu.jongwn.taximeter.utils.util.TaximeterUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.graphics.Color.rgb;
import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button btnStart, btnEnd, btnSend;
    private ImageButton btnPrint, btnHistory, btnSettings, btnTariff;
    private TextView tvDistance,tvDistance_unit, tvWait, tvSpeed, tvFare, tvFare_uint,tvSpeed_unit;
    private EditText teRiderPhone;
    private boolean isProcessActive;
    private boolean isGPSActive;

    private double price = 0, distance = 0 ;

    public SQLiteDatabase datab;
    private LocationListener locListenD;
    public long start,end,period;
    public double waiting_fare,initial_charge,minifare,maxfare,initial_charge_n ;
    public String tax,distance_mearsure;
    public static final String PARAM_INTENT = "pendingIntent";
    public static final String DISTANCE = "distance";
    public static final String PARAM_STATUS_GPS = "gps_status";
    public static final int PARAM_CODE_DISTANCE = 333;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    double first_tariff_day,first_to_day,second_tariff_day,first_tariff_night,first_to_night,second_tariff_night;
    public long  expire;
    public Integer waiting_time,waitingshow=0;
    private double intevel;
    boolean waitingstart=false;
    public String currency;

    public String username,phonenumber,taxinumber;

    public  double lastspeed;
    public Location buffer;
    public boolean motiondetector = false;
    private SensorManager mSensorManager;

    private ShakeEventListener mSensorListener;
    private class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {
        /*    if(!location.hasSpeed()){
             //   Toast.makeText(MainActivity.this, " has not speed", Toast.LENGTH_SHORT).show();
                double speedt = location.getSpeed();
                teRiderPhone.setText(Double.toString(speedt));

            }else{
            //    Toast.makeText(MainActivity.this, " speed", Toast.LENGTH_SHORT).show();
                teRiderPhone.setText(Double.toString(location.getSpeed()));
            }
*/
            if(!isGPSActive){

                buffer =location;

                isGPSActive = true;
                btnStart.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape));
                return;
            }

       //     if(!isBetterLocation(locationgps)) return;

            if(!isProcessActive) return;

            DecimalFormat df = new DecimalFormat("0.0");


            btnStart.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape));

           //double speed = getMySpeed(location,dis);
           double speed = getMySpeed2(location);
           double dis = calculateDistance(location);
           buffer=location;

           if( (speed ==0  || !motiondetector)){

                if(!waitingstart){
                    waitingstart=true;
               //     t1 = null;
               //     t1 = new Thread1();
               //     t1.start();
                    waitingshow = 0;


                }
               waitingshow ++;
               if((waitingshow%6)==0){
                   waiting_time++;
               }
               tvSpeed.setText("WAITING");
               tvSpeed_unit.setVisibility(View.INVISIBLE);
               price = calculatePrice(distance);

               tvFare.setText(Double.toString(Double.valueOf(df.format(price))));
               tvWait.setText(Double.toString(Double.valueOf(df.format(waiting_time*0.1))));


           }else{
                if(waitingstart) {
              //      t1.release();
              //      t1 = null;
                    waitingstart = false;
                    waitingshow = 0;
                }
               lastspeed = speed;
               distance += dis;

               tvDistance.setText(Double.toString(Double.valueOf(df.format(distance))));

               tvSpeed.setText(Double.toString(Double.valueOf(df.format(speed))));
               tvSpeed_unit.setVisibility(View.VISIBLE);
               double ddd = Double.parseDouble(tvDistance.getText().toString());
               if((ddd-intevel)==0.1){
                   price = calculatePrice(distance);
                   tvFare.setText(Double.toString(Double.valueOf(df.format(price))));
                   intevel = ddd;
               }


           }

        }
        public boolean isBetterLocation(Location location) {
            boolean isNewer = location.getTime() > buffer.getTime();

            if(location.getSpeed()>200)return false;
            if(location.getSpeed()<4)return false;
            boolean isMoreAccurate = (location.getAccuracy() < buffer.getAccuracy());
            if (!isMoreAccurate && !isNewer) {
                // More accurate and newer is always better.
                return false;
            }
            return true;
        }
        public void locationbuffer(Location location){

            buffer = location;


        }

        public double getMySpeed2(Location l){
            double t;
            t = l.getSpeed();
            if(t>100)t = buffer.getSpeed();
   /*         if(t==0){
                if(buffer[1].getSpeed()==0 && buffer[2].getSpeed()==0){
                    t=0;
                }else if(buffer[1].getSpeed()!=0){
                    t = buffer[1].getSpeed();
                }else {
                    t = buffer[2].getSpeed();
                }
            }*/
           t = t*3600/1000;
            return t;
        }

        @Override
        public void onProviderDisabled(String provider) {
           // Toast.makeText(MainActivity.this, provider + " Disabled", Toast.LENGTH_SHORT).show();
            isGPSActive = false;
           // startGPS();
        }

        @Override
        public void onProviderEnabled(String provider) {
           Toast.makeText(MainActivity.this, provider + " Enabled", Toast.LENGTH_SHORT).show();
            isGPSActive = true;
          //  startGPS();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch(status) {
                case GpsStatus.GPS_EVENT_STARTED:
                //    Toast.makeText(MainActivity.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                    break;

                case GpsStatus.GPS_EVENT_STOPPED:
                //    startGPS();
                    break;

                case GpsStatus.GPS_EVENT_FIRST_FIX:
                //    Toast.makeText(MainActivity.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                    break;

                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                 //   Toast.makeText(MainActivity.this, "GPS_EVENT_SATELLITE_STATUS", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected  void onResume(){
        super.onResume();
        getPreferenceData();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datab=openOrCreateDatabase("C_DB", Context.MODE_PRIVATE, null);
        datab.execSQL("CREATE TABLE IF NOT EXISTS history_table (fare TEXT DEFAULT ' ',tax TEXT DEFAULT ' ',period INTEGER DEFAULT 0, distance Integer DEFAULT 0, start INTEGER DEFAULT 0, end INTEGER DEFAULT 0);");
        datab.close();



        getPreferenceData();
        initWidgets();
        if(!testexpire()){

            Toast.makeText(MainActivity.this, "You can not use this app", Toast.LENGTH_SHORT).show();
            return;
        }
        isGPSActive = false;
        startGPS();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
              //  Toast.makeText(MainActivity.this, "MotionMotion!", Toast.LENGTH_SHORT).show();
                motiondetector = true;
            }
            public void onStop() {

                Toast.makeText(MainActivity.this, "Stope!", Toast.LENGTH_SHORT).show();
                motiondetector = false;
            }
        });
    }

    public boolean testexpire(){
        boolean r;

        Calendar cl = Calendar.getInstance();
        cl.add(Calendar.DATE,0);

        if(cl.getTimeInMillis()>(expire+24*3600*1000)){
            r = false;
        }else{
            r= true;
        }
        return r;
    }
    public void startGPS(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{

        }
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        if((!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))&&(!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {

            //Toast.makeText(getApplicationContext(), "GPS provider don,t work", Toast.LENGTH_SHORT).show();
           // isGPSActive = false;

        } else {
            //Toast.makeText(getApplicationContext(), "GPS signal search", Toast.LENGTH_SHORT).show();
/*
            Location ll = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(ll != null) {


                buffer[0] =ll;
                buffer[1] = ll;
                buffer[2] = ll;

            }else{
                Location ll2 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                buffer[0] =ll2;
                buffer[1] = ll2;
                buffer[2] = ll2;
            }
*/
            locListenD = new MyLocationListener();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500L, 0.0f, locListenD);
         //   lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500L, 0.0f, locListenD);
        }
    }
    private double calculateDistance(Location location) {
        double dist=0.0;
        if(distance_mearsure.equals("km")){
            dist = (buffer.distanceTo(location))/1000.00;
        }else{
            dist = (buffer.distanceTo(location))/1609.00;
        }

        return dist;
    }

    public void getPreferenceData(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String value;
        if (sharedPref.contains("initial_charge")) {

            value =sharedPref.getString("initial_charge", "0");
            try {
                initial_charge = Double.parseDouble(value); // Make use of autoboxing.  It's also easier to read.
            } catch (NumberFormatException e) {
                // initial_charge did not contain a valid double
            }

        }else{
            initial_charge = 50.00;
        }
        if (sharedPref.contains("initial_charge_n")) {

            value =sharedPref.getString("initial_charge_n", "0");
            try {
                initial_charge_n = Double.parseDouble(value); // Make use of autoboxing.  It's also easier to read.
            } catch (NumberFormatException e) {
                // initial_charge did not contain a valid double
            }

        }else{
            initial_charge_n = 50.00;
        }
        if (sharedPref.contains("waiting_charge")) {

            value =sharedPref.getString("waiting_charge", "0");
            try{
                waiting_fare = Double.parseDouble(value);
            } catch (NumberFormatException e){

            }

        }else{
            waiting_fare = 5.00;
        }
        if (sharedPref.contains("minimun_fare")) {

            value =sharedPref.getString("minimun_fare", "0");
            try{
                minifare = Double.parseDouble(value);
            } catch (NumberFormatException e){

            }

        }else{
            minifare = 50.00;
        }
        if (sharedPref.contains("maximum_fare")) {

            value =sharedPref.getString("maximum_fare", "0");
            try{
                maxfare = Double.parseDouble(value);
            } catch (NumberFormatException e){

            }

        }else{
            maxfare = 1000.00;
        }
        if (sharedPref.contains("tax")) {

            tax =sharedPref.getString("tax", "0");
            //tax = Integer.parseInt(value);
        }else{
            tax = "0.00";
        }
        if (sharedPref.contains("distance_charge1")) {

            value =sharedPref.getString("distance_charge1", "0");
            String split[]=value.split("-");
            try{
                first_to_day = Double.parseDouble(split[0]);
                first_tariff_day = Double.parseDouble(split[1]);
                second_tariff_day = Double.parseDouble(split[2]);

            } catch (NumberFormatException e){

            }
        }else{
            first_to_day = 1.2;
            first_tariff_day = 8.0;
            second_tariff_day = 30;
        }
        if (sharedPref.contains("distance_charge2")) {

            value =sharedPref.getString("distance_charge2", "0");
            String split[]=value.split("-");

            try{
                first_to_night = Double.parseDouble(split[0]);
                first_tariff_night = Double.parseDouble(split[1]);
                second_tariff_night = Double.parseDouble(split[2]);

            } catch (NumberFormatException e){

            }

        }else{
            first_to_night = 1.2;
            first_tariff_night = 8.0;
            second_tariff_night = 30;
        }

        if (sharedPref.contains("general_second")) {

            distance_mearsure =sharedPref.getString("general_second", "km");
            if(distance_mearsure.equals("0"))distance_mearsure="km";
            if(distance_mearsure.equals("1"))distance_mearsure="mile";
        }else{
            distance_mearsure = "km";
        }
        if (sharedPref.contains("date_picker")) {

            value =sharedPref.getString("date_picker", "0");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = df.parse(value);
                expire = d.getTime();
            }catch (ParseException e1) {
                e1.printStackTrace();
            }
        }else{
            expire =  Calendar.getInstance().getTimeInMillis();
        }

        if (sharedPref.contains("general_first")) {

            currency =sharedPref.getString("general_first", "0");

        }else{
            currency =  "$";
        }
        if (sharedPref.contains("account_third")) {

            username =sharedPref.getString("account_third", "0");

        }else{
            username =  "@string/pref_account_third_edit";
        }
        if (sharedPref.contains("account_forth")) {

            taxinumber =sharedPref.getString("account_forth", "0");

        }else{
            taxinumber =  "@string/pref_account_forth_edit";
        }
        if (sharedPref.contains("account_fifth")) {

            phonenumber =sharedPref.getString("account_fifth", "0");

        }else{
            phonenumber =  "@string/pref_account_fifth_edit";
        }
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
        tvDistance.setText("0.0");
        tvWait = (TextView) findViewById(R.id.tvWaitTime);
        tvWait.setText("0.0");
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvSpeed.setText("park");
        tvFare = (TextView) findViewById(R.id.tvFare);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String dayvalue,nightvalue;
        if (sharedPref.contains("time_picker_day")) {

            dayvalue =sharedPref.getString("time_picker_day", "06:00");

        }else{
            dayvalue = "06:00";
        }
        if (sharedPref.contains("time_picker_night")) {

            nightvalue =sharedPref.getString("time_picker_night", "22:00");

        }else{
            nightvalue = "22:00";
        }
        DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        try {
            Date daydate = sdf.parse(dayvalue);
            Date nightdate = sdf.parse(nightvalue);

            Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            String currenttime = ""+hour+":"+minute;
            Date curr = sdf.parse(currenttime);

            if((curr.getTime()>daydate.getTime()) && (curr.getTime()<nightdate.getTime())){
                tvFare.setText(Double.toString(initial_charge));
            }else{
                tvFare.setText(Double.toString(initial_charge_n));
            }

         }catch (Exception e){

        }


        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        tvSpeed_unit = (TextView)findViewById(R.id.dd);
        tvSpeed_unit.setVisibility(View.INVISIBLE);
        tvFare_uint = (TextView)findViewById(R.id.fare_unit);
        tvFare_uint.setText(currency);

        tvDistance_unit = (TextView)findViewById(R.id.distance_unit);
        tvDistance_unit.setText(distance_mearsure);
        teRiderPhone = (EditText)findViewById(R.id.teRiderPhone);
    }
    private boolean writeToDB(String sum, String distance) {
        DBHelper dbHelper = new DBHelper(this, null);

        ContentValues cv = new ContentValues();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                    getPreferenceData();
                    initWidgets();
                    if (!isProcessActive) {
                        //TODO start service;

                        isProcessActive = true;
                        btnStart.setEnabled(false);
                        btnEnd.setEnabled(true);
                        startGPS();
                        distance = 0;
                        price = 0;
                        waiting_time = -1;waitingshow=0;
                        intevel = 0.0;lastspeed = 0.0;
                     //   t1=null;
                     //   t1 = new Thread1();
                     //   t1.start();

                        waitingstart = true;
                        Calendar calendar = Calendar.getInstance();
                        start = calendar.getTimeInMillis();

                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                        tax = sp.getString("tax","0");


                    } /*else {
                        //TODO pause
                        counterService.pauseCounter();
                        //btnStart.setText("Старт");
                        isProcessActive = false;

                    }by jwn*/
                } else {
                    Toast.makeText(getApplicationContext(), "GPS don,t work", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnEnd:
                isProcessActive = false;
                btnStart.setEnabled(true);
                btnEnd.setEnabled(false);


                LocationManager lm2 = (LocationManager)getSystemService(Context. LOCATION_SERVICE);
                lm2.removeUpdates( locListenD );    // Stop the update if it is in progress.

                Calendar calendar = Calendar.getInstance();
                end = calendar.getTimeInMillis();
                period = end-start;
               // motiondetector = false;
                datab=openOrCreateDatabase("C_DB", Context.MODE_PRIVATE, null);
                datab.execSQL("CREATE TABLE IF NOT EXISTS history_table (fare TEXT DEFAULT ' ',tax TEXT DEFAULT ' ',period INTEGER DEFAULT 0, distance Integer DEFAULT 0,start INTEGER DEFAULT 0, end INTEGER DEFAULT 0);");

                datab.execSQL("INSERT INTO history_table (fare, tax, period, distance, start, end) VALUES('"+tvFare.getText().toString()+"','"+tax+"','"+period+"','"+distance+"','"+start+"','"+end+"');");
                datab.close();

           //    t1.release();
           //    t1 = null;
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
                    intent.putExtra("fare",tvFare.getText().toString());
                    intent.putExtra("distance",tvDistance.getText().toString()+tvDistance_unit.getText().toString());
                    intent.putExtra("start",Long.toString(start));
                    intent.putExtra("end",Long.toString(end));
                    intent.putExtra("waiting",tvWait.getText().toString());

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
                intent = new Intent(this, TariffActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSend:
                if(!isProcessActive){


                    String period_ = ""+period/( 60 * 60 * 1000)%24 + "hours" +period/(60*1000)%60+"minutes"+period/1000%60+"seconds";
                    String start_ =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(start));
                    String end_ =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(end));
                    String smsBody="Fare-" + tvFare.getText().toString()+": User Name-" +username + ": Taxi Number-"+taxinumber +": Phone Number-"+phonenumber+": Period-"+period_+": Distance-"+distance+": Start-"+start_+": End-"+end_;
                    String phoneNumber = teRiderPhone.getText().toString();
                   // String smsBody = "the Driving finish";
                    String SMS_SENT = "SMS_SENT";
                    String SMS_DELIVERED = "SMS_DELIVERED";

                    PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
                    PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

// For when the SMS has been sent
                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            teRiderPhone.setText("");
                            switch (getResultCode()) {
                                case Activity.RESULT_OK:
                                    Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                                    break;
                                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                    Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                                    break;
                                case SmsManager.RESULT_ERROR_NO_SERVICE:
                                    Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                                    break;
                                case SmsManager.RESULT_ERROR_NULL_PDU:
                                    Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                                    break;
                                case SmsManager.RESULT_ERROR_RADIO_OFF:
                                    Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            teRiderPhone.setText("");
                            switch (getResultCode()) {
                                case Activity.RESULT_OK:
                                    Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                                    break;
                                case Activity.RESULT_CANCELED:
                                    Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }, new IntentFilter(SMS_DELIVERED));

// Get the default instance of SmsManager
                    SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS

                    if(!phoneNumber.equals("")) {
                        smsManager.sendTextMessage(phoneNumber, null, smsBody, sentPendingIntent, deliveredPendingIntent);
                        teRiderPhone.setText("");
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Current it is working!. Afre finishing, retry to do it", Toast.LENGTH_SHORT).show();
                }
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


    private double calculatePrice(double dist) {
        Calendar cal = Calendar.getInstance();
        Integer h = cal.get(Calendar.HOUR); Integer ap = cal.get(Calendar.AM_PM);
        double first_to, first_tarif, second_tarif;
        double initial;
        if(((ap==Calendar.AM)&&(h>5))||((ap==Calendar.PM)&&(h<9))){
            first_to = first_to_day; first_tarif = first_tariff_day; second_tarif = second_tariff_day;
            initial = initial_charge;

        }else{
            first_to = first_to_night; first_tarif = first_tariff_night; second_tarif = second_tariff_night;
            initial = initial_charge_n;
        }
        if (dist < first_to) {
            double price = waiting_fare*waiting_time*0.1 + initial;

            if(price>maxfare){
                Toast.makeText(MainActivity.this, "the Fare overcome maxfare", Toast.LENGTH_SHORT).show();
                price = maxfare;
            }
            return price;
        } else {
            double price = (dist-first_to)*second_tarif+waiting_fare*waiting_time*0.1 + initial;

            if(price>maxfare){
                Toast.makeText(MainActivity.this, "the Fare overcome maxfare", Toast.LENGTH_SHORT).show();
                price = maxfare;
            }
            return price;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //// gps location
                    Criteria criteria = new Criteria();

                    criteria.setAccuracy(Criteria.ACCURACY_FINE);

                    criteria.setPowerRequirement(Criteria.POWER_LOW);

                    criteria.setAltitudeRequired(true);

                    criteria.setBearingRequired(false);

                    criteria.setSpeedRequired(false);

                    criteria.setCostAllowed(true);

                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    String provider = lm.getBestProvider(criteria, true);



                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
   /* class Thread1 extends Thread {

         private boolean loopEnable = true;
         private int cnt = 0;

         public void run() {
           while (loopEnable) {
                try {
                    sleep(6000);
                } catch (InterruptedException e) {
                }
               handler.sendMessage(handler.obtainMessage());

            }
        };

        public void release() {
            loopEnable = false;

        }
        public void restart() {
            loopEnable = true;

        }
    };*/
  /*  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateWaitTime();
        }
    };*/

    public void updateWaitTime(){

        waiting_time ++;
        tvSpeed.setText("WAITING");
        tvSpeed_unit.setVisibility(View.INVISIBLE);
        price = calculatePrice(distance);
        DecimalFormat df = new DecimalFormat("0.0");
        tvFare.setText(Double.toString(Double.valueOf(df.format(price))));
        tvWait.setText(Double.toString(Double.valueOf(df.format(waiting_time*0.1))));
    }

}
