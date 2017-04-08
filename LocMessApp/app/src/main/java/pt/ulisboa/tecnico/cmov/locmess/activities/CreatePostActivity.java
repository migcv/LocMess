package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Locale;

import pt.ulisboa.tecnico.cmov.locmess.R;

public class CreatePostActivity extends AppCompatActivity {

    EditText datetime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Set back arrow button on toolbar */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(getApplicationContext(), LocationOptionActivity.class);
                startActivity(intent);
            }
        });

        datetime = (EditText) findViewById(R.id.input_time);
        datetime.setKeyListener(null);
        SimpleDateFormat dateF = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault());
        String date = dateF.format(Calendar.getInstance().getTime());
        datetime.setText(date);

        datetime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    pickTime();
                }
            }
        });

        datetime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });
    }

    private void pickTime() {
        Calendar myCalendar = Calendar.getInstance();
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);
        int month = myCalendar.get(Calendar.MONTH);
        int year = myCalendar.get(Calendar.YEAR);
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);

        final TimePickerDialog timePickerDialog = new TimePickerDialog(CreatePostActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        datetime.setText(datetime.getText() + String.format(" %02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreatePostActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        SimpleDateFormat dateF = new SimpleDateFormat("EEE, d MMM yyyy");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        String date = dateF.format(calendar);
                        datetime.setText(date);
                        timePickerDialog.show();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

}
