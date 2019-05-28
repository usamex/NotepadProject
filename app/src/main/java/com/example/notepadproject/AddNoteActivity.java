package com.example.notepadproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.notepadproject.are.src.main.java.com.chinalwb.are.AREditText;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolbar.IARE_Toolbar;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentCenter;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentLeft;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentRight;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_BackgroundColor;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Bold;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_FontColor;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_FontSize;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Hr;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Image;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Italic;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Link;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListBullet;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListNumber;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Quote;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Strikethrough;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Subscript;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Superscript;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Underline;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.ARE_ToolItem_Video;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.styles.toolitems.IARE_ToolItem;

import java.util.Calendar;


public class AddNoteActivity extends AppCompatActivity {
    final String dbTable = "Notes";
    private String dbName = "MyNotes";
    private String colID = "ID";
    private String colTitle = "Title";
    private String colDesc = "Description";
    private String colPriority = "Priority";
    private String colLocation = "Location";
    private String colDate = "Date";
    private String colAlarmDate = "AlarmDate";
    private String colHtmlDesc = "HtmlDesc";
    private Long time = 0L;
    Calendar alarm;
    int id = 0;
    EditText title;
    private IARE_Toolbar mToolbar;
    private AREditText mEditText;
    TextView date;
    ImageButton addBtn;
    int priority = 3;
    boolean result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        title = findViewById(R.id.titleAn);
        mEditText = findViewById(R.id.contentAn);
        date = findViewById(R.id.dateAn);
        try{
            final Bundle bundle = getIntent().getExtras();
            date.setText(bundle.getString("date"));
            id = bundle.getInt("ID");
            if(id != 0){
                getSupportActionBar().setTitle("Update Note");
                addBtn = findViewById(R.id.addBtn);
                addBtn.setImageDrawable(getDrawable(R.drawable.ic_action_refresh));
                title.setText(bundle.getString("name"));
                mEditText.fromHtml(bundle.getString("html_desc"));
            }
        }catch(Exception ex){ ex.printStackTrace();}
        initToolbar();
        alarm = Calendar.getInstance();

    }



    private void initToolbar() {
        mToolbar = this.findViewById(R.id.areToolbar);
        IARE_ToolItem bold = new ARE_ToolItem_Bold();
        IARE_ToolItem italic = new ARE_ToolItem_Italic();
        IARE_ToolItem underline = new ARE_ToolItem_Underline();
        IARE_ToolItem strikethrough = new ARE_ToolItem_Strikethrough();
        IARE_ToolItem fontSize = new ARE_ToolItem_FontSize();
        IARE_ToolItem fontColor = new ARE_ToolItem_FontColor();
        IARE_ToolItem backgroundColor = new ARE_ToolItem_BackgroundColor();
        IARE_ToolItem quote = new ARE_ToolItem_Quote();
        IARE_ToolItem listNumber = new ARE_ToolItem_ListNumber();
        IARE_ToolItem listBullet = new ARE_ToolItem_ListBullet();
        IARE_ToolItem hr = new ARE_ToolItem_Hr();
        IARE_ToolItem link = new ARE_ToolItem_Link();
        IARE_ToolItem left = new ARE_ToolItem_AlignmentLeft();
        IARE_ToolItem center = new ARE_ToolItem_AlignmentCenter();
        IARE_ToolItem right = new ARE_ToolItem_AlignmentRight();
        IARE_ToolItem image = new ARE_ToolItem_Image();
        IARE_ToolItem video = new ARE_ToolItem_Video();
        IARE_ToolItem subscript = new ARE_ToolItem_Subscript();
        IARE_ToolItem superscript = new ARE_ToolItem_Superscript();
        mToolbar.setEditText(mEditText);
        mToolbar.addToolbarItem(bold);
        mToolbar.addToolbarItem(italic);
        mToolbar.addToolbarItem(underline);
        mToolbar.addToolbarItem(strikethrough);
        mToolbar.addToolbarItem(fontSize);
        mToolbar.addToolbarItem(fontColor);
        mToolbar.addToolbarItem(backgroundColor);
        mToolbar.addToolbarItem(quote);
        mToolbar.addToolbarItem(listNumber);
        mToolbar.addToolbarItem(listBullet);
        mToolbar.addToolbarItem(hr);
        mToolbar.addToolbarItem(link);
        mToolbar.addToolbarItem(subscript);
        mToolbar.addToolbarItem(superscript);
        mToolbar.addToolbarItem(left);
        mToolbar.addToolbarItem(center);
        mToolbar.addToolbarItem(right);
        mToolbar.addToolbarItem(image);
        mToolbar.addToolbarItem(video);
        mEditText = this.findViewById(R.id.contentAn);
        mEditText.setToolbar(mToolbar);

        setHtml();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mToolbar.onActivityResult(requestCode, resultCode, data);
    }
    private void setHtml() {
        String html = "";
        mEditText.fromHtml(html);
    }


    public void addNote(View view) {
        if(alarm.isSet(Calendar.YEAR) && alarm.isSet(Calendar.MONTH) && alarm.isSet(Calendar.HOUR_OF_DAY) &&
                alarm.isSet(Calendar.MINUTE) && alarm.isSet(Calendar.DAY_OF_MONTH))
            time = alarm.getTimeInMillis();
        if(title.getText().toString().equals("")){
            Toast.makeText(this, "Please add a title to your note...", Toast.LENGTH_LONG).show();
            return;
        }else if(mEditText.getText().toString().equals("")){
            Toast.makeText(this, "Please add a description to your note...", Toast.LENGTH_LONG).show();
            return;
        }
        else if(time != 0 && time < Calendar.getInstance().getTimeInMillis()){
            Toast.makeText(this, "Please select a reasonable time...", Toast.LENGTH_LONG).show();
            return;
        }
        DbManager dbManager = new DbManager(this);
        ContentValues values = new ContentValues();
        /*
         * TODO: Add year and date
         */
        values.put("Title", title.getText().toString());
        values.put("Description", mEditText.getText().toString());
        values.put("Priority", priority);
        values.put("Date", date.getText().toString());
        values.put("Location", "Istanbul");

        values.put("AlarmDate", time);
        values.put("HtmlDesc", mEditText.getHtml());
        if (id == 0){
            long ID = dbManager.insert(values);
            if(ID > 0){
                Toast.makeText(this, "Note is added...", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(this, "Error adding note....", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            String[] selectionArgs = {Integer.toString(id)};
            long ID = dbManager.update(values, "ID=?", selectionArgs);
            if(ID > 0){
                Toast.makeText(this, "Note is added...", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(this, "Error adding note....", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void setTheTime(){
        final int mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddNoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                alarm.set(Calendar.HOUR_OF_DAY, i);
                alarm.set(Calendar.MINUTE, i1);
            }
        }, mHour, mMinute, true);
        timePickerDialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAlarm(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddNoteActivity.this);
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                alarm.clear();
                alarm.set(Calendar.YEAR, year);
                alarm.set(Calendar.MONTH, month);
                alarm.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setTheTime();
            }
        });
        datePickerDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item != null){
            switch (item.getItemId()){
                case R.id.priority_high:{
                    priority = 1;
                    Toast.makeText(this, "High prioritized", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.priority_medium:{
                    priority = 2;
                    Toast.makeText(this, "Medium prioritized", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.priority_low:{
                    priority = 3;
                    Toast.makeText(this, "Low prioritized", Toast.LENGTH_SHORT).show();
                    break;
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }



}
