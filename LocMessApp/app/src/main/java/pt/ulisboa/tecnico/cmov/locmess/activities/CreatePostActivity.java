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
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.utils.NewPost;

public class CreatePostActivity extends AppCompatActivity {

    private EditText datetime = null;

    private int hourPost;
    private int minutePost;
    private int dayPost;
    private int monthPost;
    private int yearPost;

    private boolean error = false;

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
                error = false;
                if(((EditText)findViewById(R.id.input_tittle)).getText().toString().isEmpty()) {
                    ((EditText)findViewById(R.id.input_tittle)).setError("Tittle needed!");
                    error = true;
                }
                if(((EditText)findViewById(R.id.input_content)).getText().toString().isEmpty()) {
                    ((EditText)findViewById(R.id.input_content)).setError("Content needed!");
                    error = true;
                }
                if(((EditText)findViewById(R.id.input_contact)).getText().toString().isEmpty()) {
                    ((EditText)findViewById(R.id.input_contact)).setError("Contact needed!");
                    error = true;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(((EditText)findViewById(R.id.input_contact)).getText().toString()).matches() &&
                        !Patterns.PHONE.matcher(((EditText)findViewById(R.id.input_contact)).getText().toString()).matches()) {
                    ((EditText)findViewById(R.id.input_contact)).setError("Contact not valid!");
                    error = true;
                }
                SimpleDateFormat dateF = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
                Calendar calendar = Calendar.getInstance();
                calendar.set(yearPost, monthPost, dayPost, hourPost, minutePost);
                try {
                    if(new SimpleDateFormat("EEE, d MMM yyyy HH:mm").parse(dateF.format(calendar)).before(Calendar.getInstance().getTime())) {
                        ((EditText)findViewById(R.id.input_time)).setError("Time not valid!");
                        error = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(!error) {
                    NewPost.tittle = ((EditText) findViewById(R.id.input_tittle)).getText().toString();
                    NewPost.content = ((EditText) findViewById(R.id.input_content)).getText().toString();
                    NewPost.contact = ((EditText) findViewById(R.id.input_contact)).getText().toString();

                    NewPost.day = dayPost;
                    NewPost.month = monthPost;
                    NewPost.year = yearPost;
                    NewPost.hour = hourPost;
                    NewPost.minute = minutePost;

                    Intent intent = new Intent(getApplicationContext(), LocationOptionActivity.class);
                    startActivity(intent);
                }
            }
        });

        datetime = (EditText) findViewById(R.id.input_time);
        datetime.setKeyListener(null);
        Calendar myCalendar = Calendar.getInstance();
        dayPost = myCalendar.get(Calendar.DAY_OF_MONTH);
        monthPost = myCalendar.get(Calendar.MONTH);
        yearPost = myCalendar.get(Calendar.YEAR);
        myCalendar.setTime(new Date(System.currentTimeMillis()+5*60*1000));
        hourPost = myCalendar.get(Calendar.HOUR_OF_DAY);
        minutePost = myCalendar.get(Calendar.MINUTE);
        SimpleDateFormat dateF = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault());
        String date = dateF.format(myCalendar.getTime());
        datetime.setText(date);

        datetime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    pickTime();
                    datetime.setError(null);
                }
            }
        });
        datetime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pickTime();
                datetime.setError(null);
            }
        });
    }

    private void pickTime() {
        Calendar myCalendar = Calendar.getInstance();
        dayPost = myCalendar.get(Calendar.DAY_OF_MONTH);
        monthPost = myCalendar.get(Calendar.MONTH);
        yearPost = myCalendar.get(Calendar.YEAR);
        myCalendar.setTime(new Date(System.currentTimeMillis()+5*60*1000));
        hourPost = myCalendar.get(Calendar.HOUR_OF_DAY);
        minutePost = myCalendar.get(Calendar.MINUTE);

        final TimePickerDialog timePickerDialog = new TimePickerDialog(CreatePostActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hourPost = hourOfDay;
                        minutePost = minute;
                        datetime.setText(datetime.getText() + String.format(" %02d:%02d", hourOfDay, minute));
                    }
                }, hourPost, minutePost, true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreatePostActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        yearPost = year;
                        monthPost = monthOfYear;
                        dayPost = dayOfMonth;
                        SimpleDateFormat dateF = new SimpleDateFormat("EEE, d MMM yyyy");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        String date = dateF.format(calendar);
                        datetime.setText(date);
                        timePickerDialog.show();
                    }
                }, yearPost, monthPost, dayPost);
        datePickerDialog.show();

    }

}
