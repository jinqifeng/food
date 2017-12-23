package com.chaoyu.jongwn.taximeter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chaoyu.jongwn.taximeter.utils.util.FareAdapter;
import com.chaoyu.jongwn.taximeter.utils.util.FareResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Math.round;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    RecyclerView recList;
    ArrayList<FareResult> fare_history;
    Spinner spinner;
    EditText dpTo;
    EditText dpFrom;
    EditText tmTo;
    EditText tmFrom;
    CheckBox summary;
    Double sum_fare,  sum_distance,tax;
    Integer hire_no;
    long sum_period;
    public SQLiteDatabase datab;
    Cursor res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        dpTo = (EditText) findViewById(R.id.etTo);
        dpFrom = (EditText) findViewById(R.id.etFrom);

        dpTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(HistoryActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dpTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        dpFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(HistoryActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dpFrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        tmTo = (EditText) findViewById(R.id.tmTo);
        tmFrom = (EditText) findViewById(R.id.tmFrom);

        tmFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(HistoryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tmFrom.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        tmTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(HistoryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tmTo.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        datab=openOrCreateDatabase("C_DB", Context.MODE_PRIVATE, null);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spiner_adapter = ArrayAdapter.createFromResource(this,
                R.array.history_period, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        spiner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spiner_adapter);
        spinner.setOnItemSelectedListener(this);
    /*    String text = spinner.getSelectedItem().toString();
        if(text.equals("Last Day")){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            long condition = calendar.getTimeInMillis();
            res = datab.rawQuery("SELECT * FROM history_table WHERE start > '"+condition+"';",null);
        }else if(text.equals("Last Week")){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -7);
            long condition = calendar.getTimeInMillis();
            res = datab.rawQuery("SELECT * FROM history_table WHERE start > '"+condition+"';",null);

        }else if(text.equals("Last Month")){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            long condition = calendar.getTimeInMillis();
            res = datab.rawQuery("SELECT * FROM history_table WHERE start > '"+condition+"';",null);
        }else{
            res = datab.rawQuery("SELECT * FROM history_table ",null);
       // }*/

        fare_history = new ArrayList<FareResult>();
   /*     sum_fare = 0; sum_period = 0; sum_distance = 0;hire_no = 0;
        res.moveToFirst();
        if(res.getCount()>0)
        do{
            String fare = res.getString(0);
            sum_fare = sum_fare + (int) Math.round(Double.valueOf(fare));
            String tax = res.getString(1);
            long period = res.getLong(2); sum_period = sum_period + period;
            Integer ddd = round(period/(1000*3600));
            String hour = Integer.toString(ddd);
            Integer mmm = round((period-(ddd*3600*1000))/(1000*60));
            String minute = Integer.toString(mmm);
            String Distance = res.getString(3);sum_distance = sum_distance + Integer.parseInt(Distance);
            long start = (long)res.getLong(4);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String ss = dateFormat.format(new Date(start));
            long end = (long)res.getLong(5);
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String ee = dateFormat2.format(new Date(end));
            FareResult p = new FareResult();
            p.setValue("Fare with Tax: $"+fare ,"Tax:$"+tax,"Period:" +hour+" hours "+minute+" min","Distance: "+Distance+"km","Start:"+ss,"End: "+ee);
            //ADD TO ARRAYLIS
            fare_history.add(p);
            //res.moveToNext();
            hire_no++;
        }while(res.moveToNext());
        res.moveToFirst();
        FareAdapter adapter = new FareAdapter(fare_history);
        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setAdapter(adapter);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
*/
        summary = (CheckBox) findViewById(R.id.summury);

        summary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(HistoryActivity.this,
                            "Bro, try Android :)", Toast.LENGTH_LONG).show();

                    fare_history.clear();
                    String period_ = "Period: " +sum_period/(3600*1000)%24+" hours "+sum_period/(60*1000)%60+" min "+sum_period/(1000)%60 +" seconds";
                    FareResult p = new FareResult();
                    p.setValue("Fare with Tax: $"+sum_fare ,"Fare: "+sum_fare,"Tax:$"+tax,period_,"Distance: "+sum_distance+"km","No.of hire:"+hire_no);
                    fare_history.add(p);
                    FareAdapter adapter = new FareAdapter(fare_history);
                    recList = (RecyclerView) findViewById(R.id.cardList);
                    recList.setAdapter(adapter);
                    recList.setHasFixedSize(true);
                    LinearLayoutManager llm = new LinearLayoutManager(HistoryActivity.this);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    recList.setLayoutManager(llm);
                }else{
                    fare_history.clear();
                    String text = spinner.getSelectedItem().toString();
                    if(text.equals("Last Day")){
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -1);
                        long condition = calendar.getTimeInMillis();
                        res = datab.rawQuery("SELECT * FROM history_table WHERE start > "+condition+";",null);
                    }else if(text.equals("Last Week")){
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -7);
                        long condition = calendar.getTimeInMillis();
                        res = datab.rawQuery("SELECT * FROM history_table WHERE start > "+condition+";",null);

                    }else if(text.equals("Last Month")){
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MONTH, -1);
                        long condition = calendar.getTimeInMillis();
                        res = datab.rawQuery("SELECT * FROM history_table WHERE start > '"+condition+"';",null);
                    }else{
                        res = datab.rawQuery("SELECT * FROM history_table ",null);
                    }


                    sum_fare = 0.0; sum_period = 0; sum_distance = 0.0;hire_no = 0;
                    res.moveToFirst();
                    if(res.getCount()>0)
                        do{
                            String fare = res.getString(0); sum_fare = sum_fare + (int) Math.round(Double.valueOf(fare));
                            String tax = res.getString(1);
                            long period = res.getLong(2); sum_period = sum_period + period;
                            String period_ = "Period: " +period/(3600*1000)%24+" hours "+period/(60*1000)%60+" min "+period/(1000)%60 +" seconds";
                            String Distance = res.getString(3);sum_distance = sum_distance + Integer.parseInt(Distance);
                            long start = (long)res.getLong(4);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String ss = dateFormat.format(new Date(start));
                            long end = (long)res.getLong(5);
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String ee = dateFormat2.format(new Date(end));
                            FareResult p = new FareResult();
                            p.setValue("Fare with Tax: $"+fare ,"Tax:$"+tax,period_,"Distance: "+Distance+"km","Start:"+ss,"End: "+ee);
                            //ADD TO ARRAYLIS
                            fare_history.add(p);
                            //res.moveToNext();
                            hire_no++;
                        }while(res.moveToNext());

                    FareAdapter adapter = new FareAdapter(fare_history);
                    recList = (RecyclerView) findViewById(R.id.cardList);
                    recList.setAdapter(adapter);
                    recList.setHasFixedSize(true);
                    LinearLayoutManager llm = new LinearLayoutManager(HistoryActivity.this);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    recList.setLayoutManager(llm);

                }

            }
        });

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        long condition;
        Calendar calendar = Calendar.getInstance();
        fare_history.clear();
        switch (position) {
            case 0:


                calendar.add(Calendar.DATE, -1);
                condition = calendar.getTimeInMillis();
                res = datab.rawQuery("SELECT * FROM history_table WHERE start > '"+condition+"';",null);//
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected


                calendar.add(Calendar.DATE, -7);
                condition = calendar.getTimeInMillis();
                res = datab.rawQuery("SELECT * FROM history_table WHERE start > '"+condition+"';",null);
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                calendar.add(Calendar.MONTH, -1);
                condition = calendar.getTimeInMillis();
                res = datab.rawQuery("SELECT * FROM history_table WHERE start > '"+condition+"';",null);
                break;
            case 3:
                String s = "";
                s = dpFrom.getText().toString();
                if(s.equals(""))return;
                s = s+" "+tmFrom.getText().toString();
                String e = dpTo.getText().toString();
                if(e.equals(""))return;
                e = e+" "+tmTo.getText().toString();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    long startmergin = df.parse(s).getTime();
                    long endmergin = df.parse(e).getTime();
                    res = datab.rawQuery("SELECT * FROM history_table WHERE start > '"+startmergin+"' AND end <'"+endmergin+"';",null);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                break;

        }
        Integer count = res.getCount();
        res.moveToFirst();

        sum_fare = 0.0; sum_period = 0; sum_distance = 0.0;hire_no = 0;
        if(count>0)
            do{
                String fare = res.getString(0);
                sum_fare = sum_fare + (int) Math.round(Double.valueOf(fare));
                String tax = res.getString(1);
                long period = res.getLong(2); sum_period = sum_period + period;
                String period_ = "Period: " +period/(3600*1000)%24+" hours "+period/(60*1000)%60+" min "+period/(1000)%60 +" seconds";
                String Distance = res.getString(3);sum_distance = sum_distance + Double.valueOf(Distance);
                long start = (long)res.getLong(4);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String ss = dateFormat.format(new Date(start));
                long end = (long)res.getLong(5);
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String ee = dateFormat2.format(new Date(end));
                FareResult p = new FareResult();
                p.setValue("Fare with Tax: $"+fare ,"Tax:$"+tax,period_,"Distance: "+Distance+"km","Start:"+ss,"End: "+ee);
                //ADD TO ARRAYLIS
                fare_history.add(p);
                //res.moveToNext();
                hire_no++;
            }while(res.moveToNext());

        FareAdapter adapter = new FareAdapter(fare_history);
        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setAdapter(adapter);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
