package ru.ai.afoot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrainingAdapter {

    public static ArrayList<String> trainingList = new ArrayList<>();
    public static ArrayList<Integer> trainingListID = new ArrayList<>();
    public  static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public SQLiteDatabase open (Context ctx) {
        SQLiteOpenHelper helper = new AfootDB(ctx);
        SQLiteDatabase db=helper.getWritableDatabase();
        return db;
    }
    public void close (SQLiteDatabase db) {
        db.close();
    }
    public void openTrainingList (Context ctx,SQLiteDatabase db,
                                       long currentDate,
                                       double distance,
                                       double avspeed,
                                       Long time,
                                       double calories,
                                       String description) {
        try {
            ContentValues training = new ContentValues();
            training.put("DATE", currentDate);
            training.put("DISTANCE", distance);
            training.put("AVSPEED", avspeed);
            training.put("TIME", time);
            training.put("calories",calories);
            training.put("DESCRIPTION", description);
            db.insert("TRAININGS", null, training);
        } catch (Exception e) {
            Toast.makeText(ctx,"INSERT ERROR: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void getTrainingListID (Context ctx,SQLiteDatabase db) {
        try {
        Cursor cursor = db.query("TRAININGS",new String [] {"_id","DATE","DISTANCE","TIME","AVSPEED"},
                null,null,null,null,"DATE");
        while (cursor.moveToNext()) {
            trainingList.add(cursor.getPosition(),""+getDate(cursor.getLong(1))+" | "+String.format("%1$.2f",cursor.getFloat(2)));
            trainingListID.add(cursor.getPosition(),cursor.getInt(0));
             }
        }  catch (Exception e) {
            Toast.makeText(ctx,"Create training list error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private static String getDate(long timeFromDatabase) {
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        //Calendar calendar = Calendar.getInstance();
        Date date=new Date();
        date.setTime(timeFromDatabase);
        return dateFormat.format(date);
    }
}
