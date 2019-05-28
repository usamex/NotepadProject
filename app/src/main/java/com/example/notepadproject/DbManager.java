package com.example.notepadproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;

public class DbManager {
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
    private int dbVersion = 2;
    private final String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + dbTable + "(" +
            colID + " INTEGER PRIMARY KEY, " + colTitle + " TEXT, " + colDesc + " TEXT, " +
            colPriority + " INTEGER, " + colDate + " TEXT, " + colAlarmDate + " INTEGER, " +
            colLocation + " TEXT, " + colHtmlDesc + " TEXT);";

    SQLiteDatabase sqlDB;

    DbManager(Context context){
        DatabaseHelperNotes db = new DatabaseHelperNotes(context);
        sqlDB = db.getWritableDatabase();
    }
    class DatabaseHelperNotes extends SQLiteOpenHelper{
        public Context context = null;
        DatabaseHelperNotes(Context context){
            super(context, dbName, null, dbVersion);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(sqlCreateTable);
            Toast.makeText(this.context, "Database created..", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN " + colHtmlDesc + " TEXT;");
            }
            else{
                db.execSQL("DROP TABLE IF EXISTS " + dbTable);
            }
        }
    }

    public long insert(ContentValues values){
        long id = sqlDB.insert(dbTable, "", values);
        return id;
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs,
                        String sorOrder){
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(dbTable);
        Cursor cursor = qb.query(sqlDB, projection, selection, selectionArgs, null,
                null, sorOrder);
        return cursor;
    }
    public Note getNote(Cursor cursor, int id) {
        if (cursor.moveToFirst()) {
            do {
                int db_id = cursor.getInt(cursor.getColumnIndex(colID));
                if (db_id == id) {
                    String db_title = cursor.getString(cursor.getColumnIndex(colTitle));
                    String db_description = cursor.getString(cursor.getColumnIndex(colDesc));
                    int db_priority = cursor.getInt(cursor.getColumnIndex(colPriority));
                    String db_date = cursor.getString(cursor.getColumnIndex(colDate));
                    Long db_alarm_date = cursor.getLong(cursor.getColumnIndex(colAlarmDate));
                    String db_location = cursor.getString(cursor.getColumnIndex(colLocation));
                    String db_html_desc = cursor.getString(cursor.getColumnIndex(colHtmlDesc));
                    return new Note(db_id, db_title, db_description, db_priority, db_location,
                            db_date, db_alarm_date, db_html_desc);
                }
            } while (cursor.moveToNext());
        }
        return null;
    }
    public int delete(String selection, String[] selectionArgs){
        final int count = sqlDB.delete(dbTable, selection, selectionArgs);
        return count;
    }

    public int update(ContentValues values, String selection, String[] selectionArgs){
        final int count = sqlDB.update(dbTable, values, selection, selectionArgs);
        return count;
    }
}
