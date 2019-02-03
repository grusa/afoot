package ru.ai.afoot;
/**
 * @author sgrushin70@gmail.com
 * @version 1.0
 * @param
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Date;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AfootDB extends SQLiteOpenHelper{

    private static final String DB_NAME="routes";
    private static final int DB_VERSION = 1;

    public SQLiteDatabase db;

    AfootDB (Context context) {
        super ( context, DB_NAME,null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE trainings (_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "date NUMERIC,\n" +
                "distance REAL,\n" +
                "avspeed REAL,\n" +
                "time INTEGER,\n" +
                "calories REAL,\n"+
                "description TEXT);");
        }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS trainings;");
        onCreate(db);

    }
}

