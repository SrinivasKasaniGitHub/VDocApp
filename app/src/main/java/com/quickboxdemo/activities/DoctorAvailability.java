package com.quickboxdemo.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.quickboxdemo.MainActivity;
import com.quickboxdemo.R;
import com.quickboxdemo.fragments.MyDatePickerFragment;
import com.quickboxdemo.utils.Consts;

import java.util.Calendar;

/**
 * Created by Srinivas on 8/29/2018.
 */

public class DoctorAvailability extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public static AppCompatButton btnDate;
    AppCompatButton btn_Time, btn_Add,btn_Submit;
    Spinner billType;
    LinearLayout layout_timeSlots;
    String str_billType;
    public static String s_Date;
    private int mYear, mMonth, mDay, mHour, mMinute;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_availability);
        btnDate = (AppCompatButton) findViewById(R.id.btn_DatePicker);
        btn_Time = (AppCompatButton) findViewById(R.id.btn_TimePicker);
        btn_Add = (AppCompatButton) findViewById(R.id.btn_add);
        btn_Submit=(AppCompatButton)findViewById(R.id.btn_Submit);
        layout_timeSlots = (LinearLayout) findViewById(R.id.lyt_TimeSlots);
        layout_timeSlots.removeAllViews();
        billType = (Spinner) findViewById(R.id.spinner);
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getApplicationContext(), OpponentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, false);
                startActivity(intent);*/
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyDatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
        btn_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        DoctorAvailability.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");

            }
        });
        billType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_billType = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.doctors_availability_list_row, null);
                final AppCompatTextView txt_Date = addView.findViewById(R.id.txt_Date);
                final AppCompatTextView txt_Time = addView.findViewById(R.id.txt_Time);
                final AppCompatTextView txt_bill = addView.findViewById(R.id.txt_BillType);
                final AppCompatButton btn_Remove = addView.findViewById(R.id.btn_Remove);

                txt_Date.setText(btnDate.getText().toString().toUpperCase());
                txt_Time.setText(btn_Time.getText().toString());
                txt_bill.setText(str_billType);
                btnDate.setText("DATE");
                btn_Time.setText("TIME");
                layout_timeSlots.addView(addView);


                btn_Remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) addView.getParent()).removeView(addView);
                    }
                });

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent start = new Intent(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_HOME);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
        finish();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd;
        String time = "" + hourString + ":" + minuteString + " - " + hourStringEnd + ":" + minuteStringEnd;
        btn_Time.setText(time);
    }
}

