package ru.ai.afoot;
/*****
@author sgrushin@gmail.com
@version 1.2
@param trip is going the trip
@param timing is going timing
@param timingpause is
*/
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import java.util.Date;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.View;
import java.util.Calendar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private MyService speedometer;
    private boolean bound=false;
    private TextView mTextMessage;
    private FloatingActionButton buttonFab,buttonFabRun,buttonFabStop,buttonFabPause;
    private boolean trip=false;
    private boolean pressedFAB = false;
    private long timing,timingPause,secondsPause,seconds;
    private String distanceStr,speedStr,timingStr;
    private double speed,distance;
    TableLayout tableLayoutGo,tableLayoutWear;
    ConstraintLayout contaner;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.SpeedometerBinder speedometerBinder=
                    (MyService.SpeedometerBinder)service;
            speedometer=speedometerBinder.getSpeedometer();
            bound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound=false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = findViewById(R.id.message);
        buttonFab       = findViewById(R.id.floatingActionButton);
        buttonFabRun    = findViewById(R.id.floatingActionButtonRun);
        buttonFabStop   = findViewById(R.id.floatingActionButtonStop);
        buttonFabPause  = findViewById(R.id.floatingActionButtonPause);
        tableLayoutGo = findViewById(R.id.tableLayout);
        tableLayoutWear = findViewById(R.id.tableLayoutWear);
        contaner = findViewById(R.id.container);
        secondsPause=0; //@param pressed Pause in seconds
        if (savedInstanceState!=null) {
            trip = savedInstanceState.getBoolean("trip");
            timing = savedInstanceState.getLong("timing");
            timingPause= savedInstanceState.getLong("timingPause");  //@param
            if (trip) {
                tableLayoutGo.setVisibility(TableLayout.VISIBLE);
                tableLayoutWear.setVisibility(TableLayout.VISIBLE);
            }
        }
        watchMileage(); //handle with data show
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("trip",trip);
        savedInstanceState.putLong("timing",timing);
        savedInstanceState.putLong("timingPause",timingPause);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound=false;
        }
    }
    public void onClickedFab (View view) {
        if (pressedFAB) {
            buttonFab.setImageDrawable(getDrawable(R.drawable.ic_add_black_48dp));
            buttonFabRun.hide();
            buttonFabPause.hide();
            buttonFabStop.hide();
            contaner.setBackgroundColor(getColor(R.color.colorWhite));
            pressedFAB=!pressedFAB;
        } else {
            buttonFab.setRippleColor(getColor(R.color.colorPrimaryText));
            buttonFab.setImageDrawable(getDrawable(R.drawable.ic_clear_white_48dp));
            buttonFabRun.show();
            buttonFabPause.show();
            buttonFabStop.show();
            contaner.setBackgroundColor(getColor(R.color.colorGrey));
            pressedFAB=!pressedFAB;
        }
    }
    public void onClickedTrip (boolean trip) {
        if (trip) {
            timing=Calendar.getInstance().getTimeInMillis()/1000;
            tableLayoutGo.setVisibility(TableLayout.VISIBLE);
            tableLayoutWear.setVisibility(TableLayout.VISIBLE);
        } else {
            tableLayoutGo.setVisibility(TableLayout.INVISIBLE);
            tableLayoutWear.setVisibility(TableLayout.INVISIBLE);
            timing=0;
        }
    }
    public void onClickStart (View view) {
        trip=true;
        timingPause=0;
        secondsPause=0;
        onClickedTrip(trip);
        onClickedFab(view);
    }

    public void onClickPause (View view) {
        if (trip) {timingPause = Calendar.getInstance().getTimeInMillis()/1000;}
        else  {
            secondsPause=secondsPause+Calendar.getInstance().getTimeInMillis()/1000-timingPause;
            timingPause=0;}
        trip = !trip;
        onClickedFab(view);
    }

    public void onClickStop (View view) {
        trip=false;
        timingPause=0;
        secondsPause=0;
        try {
            TrainingAdapter trainingAdapter = new TrainingAdapter();
            SQLiteDatabase db=trainingAdapter.open(this);
            Date date = new Date();
            String tv_date=TrainingAdapter.dateFormat.format(date);
            trainingAdapter.openTrainingList(this,db,date.getTime(),
                    distance,
                    (distance)*3600/seconds,
                    seconds,Math.round(0.5*90*distance),null);
            trainingAdapter.getTrainingListID(this,db);
            trainingAdapter.close(db);
        } catch (SQLException e) {
            Toast.makeText(this,"DB open error"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        onClickedTrip(trip);
        onClickedFab(view);
        Intent intent = new Intent(this,TrainingList.class);
        startActivity(intent);
    }

    private void watchMileage () {
        final TextView speedView = (TextView)findViewById(R.id.message);
        final TextView distanceView = (TextView) findViewById(R.id.tv_trip);
        final TextView timeView = (TextView) findViewById(R.id.tv_time);
        final TextView avView = (TextView) findViewById(R.id.tv_av);
        final TextView calloriesView = (TextView) findViewById(R.id.tv_calories);
        final Handler handler=new Handler();
        //TODO Calories!
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (speedometer!=null) {
                speed  =speedometer.speed*60*60/1000;
                distance = speedometer.distanceKilometter/1000; }// in KM
                distanceStr = String.format("%1$.2f",distance);
                speedStr = String.format("%1$.2f",speed);
                speedView.setText(speedStr);
                //TODO Callories
                calloriesView.setText(""+Math.round(0.5*90*distance));
                //Callories = 0.5 * weigh * distance
                //Callories = 0.5 * weigh * distance
                seconds=Calendar.getInstance().getTimeInMillis()/1000-timing-secondsPause;//in Sec
                if (trip) {
                    distanceView.setText(distanceStr);
                    timingStr = String.format("%02d",seconds/(60*60))+":"+String.format("%02d", (seconds / 60) % 60 )
                            + ":" + String.format("%02d", seconds % 60);
                    timeView.setText(timingStr);
                    avView.setText(String.format("%1$.2f",(distance)*3600/seconds));
                }
                handler.postDelayed(this,1000);
            }
        });
    }
}
