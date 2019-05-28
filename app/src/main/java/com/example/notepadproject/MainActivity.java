package com.example.notepadproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private String dbName = "MyNotes";
    private String dbTable = "Notes";
    private String colID = "ID";
    private String colTitle = "Title";
    private String colDesc = "Description";
    private String colPriority = "Priority";
    private String colLocation = "Location";
    private String colDate = "Date";
    private String colAlarmDate = "AlarmDate";
    private String colHtmlDesc = "HtmlDesc";
    Calendar cal = Calendar.getInstance();
    ArrayList<Note> listNotes = new ArrayList<Note>();
    SharedPreferences mSharedPref;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPref = this.getSharedPreferences("My_data", Context.MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "newest");
        switch (mSorting){
            case "newest":
                LoadQueryNewest("%");
                break;
            case "oldest":
                LoadQueryOldest("%");
                break;
            case "priority_ascending":
                LoadQueryAscending("%");
                break;
            case "priority_descending":
                LoadQueryDescending("%");
                break;
        }

        boolean result = isReadStoragePermissionGranted();
        ArrayList<Integer> allNotesId = getNotesId();
        if(allNotesId != null){
            HashMap<Integer, Long> idToAlarm = getNotesIdToAlarm(allNotesId);
            if(idToAlarm != null){
                AlarmManager mgr=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
                Intent it = new Intent(getApplicationContext(), AlarmReceiver.class);
                it.setAction("com.example.intent.action.ALARM");
                for (Integer i: idToAlarm.keySet()) {
                    it.putExtra("id", i);
                    if((System.currentTimeMillis() - idToAlarm.get(i)) / 1000 < 0){
                        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), i, it, PendingIntent.FLAG_UPDATE_CURRENT);
                        mgr.set(AlarmManager.RTC_WAKEUP, idToAlarm.get(i), pi);
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HashMap<Integer, Long> getNotesIdToAlarm(ArrayList<Integer> idArray){
        HashMap<Integer, Long> idToAlarm = new HashMap<>();
        for(Integer id: idArray){
            Long alarm = getAlarm(id);
            if(alarm != 0L){
                idToAlarm.put(id, alarm);
            }
        }
        return idToAlarm;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Long getAlarm(int id) {
        for (Note n : listNotes) {
            if (n.getNoteID() == id)
                return n.getNoteAlarmDate();
        }
        return 0L;
    }
    private void query(ArrayList<Note> noteList, String title) {
        DbManager dbManager = new DbManager(this.getBaseContext());
        String[] projections = {colID, colTitle, colDesc, colPriority, colDate, colAlarmDate, colLocation, colHtmlDesc};
        String[] selectionArgs = {title};
        Cursor cursor = dbManager.query(projections, "ID LIKE ?", selectionArgs, colID);
        noteList.clear();
        if (cursor.moveToFirst()) {
            do {
                int db_id = cursor.getInt(cursor.getColumnIndex(colID));
                String db_title = cursor.getString(cursor.getColumnIndex(colTitle));
                String db_description = cursor.getString(cursor.getColumnIndex(colDesc));
                int db_priority = cursor.getInt(cursor.getColumnIndex(colPriority));
                String db_date = cursor.getString(cursor.getColumnIndex(colDate));
                Long db_alarm_date = cursor.getLong(cursor.getColumnIndex(colAlarmDate));
                String db_location = cursor.getString(cursor.getColumnIndex(colLocation));
                String db_html_desc = cursor.getString(cursor.getColumnIndex(colHtmlDesc));
                noteList.add(new Note(db_id, db_title, db_description, db_priority, db_location,
                        db_date, db_alarm_date, db_html_desc));
            } while (cursor.moveToNext());
        }
    }
    public ArrayList<Integer> getNotesId(){
        ArrayList<Note> noteList = new ArrayList<Note>();
        query(noteList, "%");
        ArrayList<Integer> idArray = new ArrayList<Integer>();

        for (Note n: noteList) {
            idArray.add(n.getNoteID());
        }
        if(idArray.size() > 0)
            return idArray;
        else
            return null;
    }
    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        String mSorting = mSharedPref.getString("Sort", "newest");
        switch (mSorting){
            case "newest":
                LoadQueryNewest("%");
                break;
            case "oldest":
                LoadQueryOldest("%");
                break;
            case "priority_ascending":
                LoadQueryAscending("%");
                break;
            case "priority_descending":
                LoadQueryDescending("%");
                break;
        }
        ArrayList<Integer> allNotesId = getNotesId();
        if(allNotesId != null){
            HashMap<Integer, Long> idToAlarm = getNotesIdToAlarm(allNotesId);
            if(idToAlarm != null){
                AlarmManager mgr=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
                Intent it = new Intent(getApplicationContext(), AlarmReceiver.class);
                it.setAction("com.example.intent.action.ALARM");
                for (Integer i: idToAlarm.keySet()) {
                    it.putExtra("id", i);
                    if((System.currentTimeMillis() - idToAlarm.get(i)) / 1000 < 0){
                        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), i, it, PendingIntent.FLAG_UPDATE_CURRENT);
                        mgr.set(AlarmManager.RTC_WAKEUP, idToAlarm.get(i), pi);
                    }
                }
            }
        }
    }
    private void LoadQuerySearch(String title) {
        DbManager dbManager = new DbManager(this.getBaseContext());
        String[] projections = {colID, colTitle, colDesc, colPriority, colDate, colAlarmDate, colLocation, colHtmlDesc};
        String[] selectionArgs = {title};
        Cursor cursor = dbManager.query(projections, "Title LIKE ?", selectionArgs, colID);
        listNotes.clear();
        if(cursor.moveToFirst()){
            do{
                int db_id = cursor.getInt(cursor.getColumnIndex(colID));
                String db_title = cursor.getString(cursor.getColumnIndex(colTitle));
                String db_description = cursor.getString(cursor.getColumnIndex(colDesc));
                int db_priority = cursor.getInt(cursor.getColumnIndex(colPriority));
                String db_date = cursor.getString(cursor.getColumnIndex(colDate));
                Long db_alarm_date = cursor.getLong(cursor.getColumnIndex(colAlarmDate));
                String db_location = cursor.getString(cursor.getColumnIndex(colLocation));
                String db_html_desc = cursor.getString(cursor.getColumnIndex(colHtmlDesc));
                listNotes.add(new Note(db_id, db_title, db_description, db_priority, db_location,
                        db_date, db_alarm_date, db_html_desc));
            }while(cursor.moveToNext());
        }

        MyNotesAdapter myNotesAdapter = new MyNotesAdapter(this, listNotes);
        ListView notesLv = findViewById(R.id.NotesLv);
        notesLv.setAdapter(myNotesAdapter);
        int total = notesLv.getCount();
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null){
            mActionBar.setSubtitle("You have " + total + " note(s) in list...");
        }

    }
    private void LoadQueryAscending(String title) {
        DbManager dbManager = new DbManager(this.getBaseContext());
        String[] projections = {colID, colTitle, colDesc, colPriority, colDate, colAlarmDate, colLocation, colHtmlDesc};
        String[] selectionArgs = {title};
        Cursor cursor = dbManager.query(projections, "PRIORITY LIKE ?", selectionArgs, colPriority);
        listNotes.clear();
        if(cursor.moveToLast()){
            do{
                int db_id = cursor.getInt(cursor.getColumnIndex(colID));
                String db_title = cursor.getString(cursor.getColumnIndex(colTitle));
                String db_description = cursor.getString(cursor.getColumnIndex(colDesc));
                int db_priority = cursor.getInt(cursor.getColumnIndex(colPriority));
                String db_date = cursor.getString(cursor.getColumnIndex(colDate));
                Long db_alarm_date = cursor.getLong(cursor.getColumnIndex(colAlarmDate));
                String db_location = cursor.getString(cursor.getColumnIndex(colLocation));
                String db_html_desc = cursor.getString(cursor.getColumnIndex(colHtmlDesc));
                listNotes.add(new Note(db_id, db_title, db_description, db_priority, db_location,
                        db_date, db_alarm_date, db_html_desc));
            }while(cursor.moveToPrevious());
        }

        MyNotesAdapter myNotesAdapter = new MyNotesAdapter(this, listNotes);
        ListView notesLv = findViewById(R.id.NotesLv);
        notesLv.setAdapter(myNotesAdapter);
        int total = notesLv.getCount();
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null){
            mActionBar.setSubtitle("You have " + total + " note(s) in list...");
        }

    }
    private void LoadQueryDescending(String title) {
        DbManager dbManager = new DbManager(this.getBaseContext());
        String[] projections = {colID, colTitle, colDesc, colPriority, colDate, colAlarmDate, colLocation, colHtmlDesc};
        String[] selectionArgs = {title};
        Cursor cursor = dbManager.query(projections, "PRIORITY LIKE ?", selectionArgs, colPriority);
        listNotes.clear();
        if(cursor.moveToFirst()){
            do{
                int db_id = cursor.getInt(cursor.getColumnIndex(colID));
                String db_title = cursor.getString(cursor.getColumnIndex(colTitle));
                String db_description = cursor.getString(cursor.getColumnIndex(colDesc));
                int db_priority = cursor.getInt(cursor.getColumnIndex(colPriority));
                String db_date = cursor.getString(cursor.getColumnIndex(colDate));
                Long db_alarm_date = cursor.getLong(cursor.getColumnIndex(colAlarmDate));
                String db_location = cursor.getString(cursor.getColumnIndex(colLocation));
                String db_html_desc = cursor.getString(cursor.getColumnIndex(colHtmlDesc));
                listNotes.add(new Note(db_id, db_title, db_description, db_priority, db_location,
                        db_date, db_alarm_date, db_html_desc));
            }while(cursor.moveToNext());
        }

        MyNotesAdapter myNotesAdapter = new MyNotesAdapter(this, listNotes);
        ListView notesLv = findViewById(R.id.NotesLv);
        notesLv.setAdapter(myNotesAdapter);
        int total = notesLv.getCount();
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null){
            mActionBar.setSubtitle("You have " + total + " note(s) in list...");
        }

    }
    private void LoadQueryNewest(String title) {
        DbManager dbManager = new DbManager(this.getBaseContext());
        String[] projections = {colID, colTitle, colDesc, colPriority, colDate, colAlarmDate, colLocation, colHtmlDesc};
        String[] selectionArgs = {title};
        Cursor cursor = dbManager.query(projections, "ID LIKE ?", selectionArgs, colID);
        listNotes.clear();
        if(cursor.moveToLast()){
            do{
                int db_id = cursor.getInt(cursor.getColumnIndex(colID));
                String db_title = cursor.getString(cursor.getColumnIndex(colTitle));
                String db_description = cursor.getString(cursor.getColumnIndex(colDesc));
                int db_priority = cursor.getInt(cursor.getColumnIndex(colPriority));
                String db_date = cursor.getString(cursor.getColumnIndex(colDate));
                Long db_alarm_date = cursor.getLong(cursor.getColumnIndex(colAlarmDate));
                String db_location = cursor.getString(cursor.getColumnIndex(colLocation));
                String db_html_desc = cursor.getString(cursor.getColumnIndex(colHtmlDesc));
                listNotes.add(new Note(db_id, db_title, db_description, db_priority, db_location,
                        db_date, db_alarm_date, db_html_desc));
            }while(cursor.moveToPrevious());
        }

        MyNotesAdapter myNotesAdapter = new MyNotesAdapter(this, listNotes);
        ListView notesLv = findViewById(R.id.NotesLv);
        notesLv.setAdapter(myNotesAdapter);
        int total = notesLv.getCount();
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null){
            mActionBar.setSubtitle("You have " + total + " note(s) in list...");
        }

    }



    private void LoadQueryOldest(String title) {
        DbManager dbManager = new DbManager(this.getBaseContext());
        String[] projections = {colID, colTitle, colDesc, colPriority, colDate, colAlarmDate, colLocation, colHtmlDesc};
        String[] selectionArgs = {title};
        Cursor cursor = dbManager.query(projections, "ID LIKE ?", selectionArgs, colID);
        listNotes.clear();
        if(cursor.moveToFirst()){
            do{
                int db_id = cursor.getInt(cursor.getColumnIndex(colID));
                String db_title = cursor.getString(cursor.getColumnIndex(colTitle));
                String db_description = cursor.getString(cursor.getColumnIndex(colDesc));
                int db_priority = cursor.getInt(cursor.getColumnIndex(colPriority));
                String db_date = cursor.getString(cursor.getColumnIndex(colDate));
                Long db_alarm_date = cursor.getLong(cursor.getColumnIndex(colAlarmDate));
                String db_location = cursor.getString(cursor.getColumnIndex(colLocation));
                String db_html_desc = cursor.getString(cursor.getColumnIndex(colHtmlDesc));
                listNotes.add(new Note(db_id, db_title, db_description, db_priority, db_location,
                        db_date, db_alarm_date, db_html_desc));
            }while(cursor.moveToNext());
        }

        MyNotesAdapter myNotesAdapter = new MyNotesAdapter(this, listNotes);
        ListView notesLv = findViewById(R.id.NotesLv);
        notesLv.setAdapter(myNotesAdapter);
        int total = notesLv.getCount();
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null){
            mActionBar.setSubtitle("You have " + total + " note(s) in list...");
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item != null){
            final SharedPreferences.Editor editor = mSharedPref.edit();
            switch (item.getItemId()){
                case R.id.add_note:
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yy");
                    Intent intent = new Intent(this, AddNoteActivity.class);
                    intent.putExtra("date", simpleFormat.format(cal.getTime()));
                    startActivity(intent);
                    break;
                case R.id.action_newest_sort:
                    Toast.makeText(this, "Newest", Toast.LENGTH_SHORT).show();
                    editor.clear();
                    editor.putString("Sort", "newest");
                    editor.apply();
                    LoadQueryNewest("%");
                    break;
                case R.id.action_oldest_sort:
                    Toast.makeText(this, "Oldest", Toast.LENGTH_SHORT).show();
                    editor.clear();
                    editor.putString("Sort", "oldest");
                    editor.apply();
                    LoadQueryOldest("%");
                    break;
                case R.id.action_priority_asc_sort:
                    Toast.makeText(this, "Priority Ascending", Toast.LENGTH_SHORT).show();
                    editor.clear();
                    editor.putString("Sort", "priority_ascending");
                    editor.apply();
                    LoadQueryAscending("%");
                    break;
                case R.id.action_priority_desc_sort:
                    Toast.makeText(this, "Priority Descending", Toast.LENGTH_SHORT).show();
                    editor.clear();
                    editor.putString("Sort", "priority_descending");
                    editor.apply();
                    LoadQueryDescending("%");
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        android.support.v7.widget.SearchView sv = (android.support.v7.widget.SearchView) (menu.findItem(R.id.app_bar_search).getActionView());
        SearchManager sm = (SearchManager) (getSystemService(Context.SEARCH_SERVICE));
        sv.setSearchableInfo(sm.getSearchableInfo(getComponentName()));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LoadQuerySearch("%" + query + "%");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LoadQuerySearch("%" + newText + "%");
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    class MyNotesAdapter extends BaseAdapter {
        ArrayList<Note> listNotesArray = new ArrayList<Note>();
        Context context;

        MyNotesAdapter(Context context, ArrayList<Note> listNotesArray){
            this.listNotesArray = listNotesArray;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View myView = getLayoutInflater().inflate(R.layout.row, null);
            final Note myNote = this.listNotesArray.get(position);
            final TextView titleTv = myView.findViewById(R.id.titleTv);
            final TextView contentTv = myView.findViewById(R.id.contentTv);
            TextView locationTv = myView.findViewById(R.id.locationTv);
            TextView dateTv = myView.findViewById(R.id.dateTv);

            titleTv.setText(myNote.getNoteName());
            contentTv.setText(myNote.getNoteDesc());
            locationTv.setText(myNote.getNoteLocation() == null ? "" : myNote.getNoteLocation());
            dateTv.setText(myNote.getNoteDate());
            if(myNote.getNotePriority() == 1){
                myView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.purple));
            } else if(myNote.getNotePriority() == 2){
                myView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.brown));
            } else{
                myView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.green));
            }
            ImageButton deleteBtn = myView.findViewById(R.id.deleteBtnRow);
            ImageButton editBtn = myView.findViewById(R.id.editBtnRow);
            ImageButton shareBtn = myView.findViewById(R.id.shareBtnRow);
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToViewFunction(myNote);
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    DbManager db = new DbManager(v.getContext());
                    String[] selectionArgs = {Integer.toString(myNote.getNoteID())};
                    db.delete("ID=?", selectionArgs);
                    LoadQueryAscending("%");

                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    goToUpdateFunc(myNote);
                }


            });

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = titleTv.getText().toString();
                    String desc = contentTv.getText().toString();
                    String result = title + "\n" + desc;
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, result);
                    startActivity(Intent.createChooser(shareIntent, result));
                }
            });

            return myView;
        }

        @Override
        public int getCount() {
            return listNotesArray.size();
        }

        @Override
        public Object getItem(int position) {
            return this.listNotesArray.get(position);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public long getItemId(int position) {
            return Integer.toUnsignedLong(position);
        }

    }

    private void goToViewFunction(Note myNote) {
        Intent intent = new Intent(this, TextViewActivity.class);
        intent.putExtra("ID", myNote.getNoteID());
        intent.putExtra("name", myNote.getNoteName());
        intent.putExtra("description", myNote.getNoteDesc());
        /*
         * TODO: CONTROL ALARM DATE
         */
        intent.putExtra("date", myNote.getNoteDate());
        intent.putExtra("priority", myNote.getNotePriority());
        intent.putExtra("html_desc", myNote.getNoteHtmlDesc());
        startActivity(intent);
    }
    private void goToUpdateFunc(Note myNote) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("ID", myNote.getNoteID());
        intent.putExtra("name", myNote.getNoteName());
        intent.putExtra("description", myNote.getNoteDesc());
        /*
         * TODO: CONTROL ALARM DATE
         */
        intent.putExtra("date", myNote.getNoteDate());
        intent.putExtra("priority", myNote.getNotePriority());
        intent.putExtra("html_desc", myNote.getNoteHtmlDesc());
        startActivity(intent);
    }
}
