package ru.ai.afoot;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Binder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;


public class MyService extends Service {
    public static double distanceKilometter;
    public static Location lastLocation = null;
    public static double speed;
    private LocationListener listener;
    private final IBinder binder = new SpeedometerBinder();
    public MyService() {
    }
    public class SpeedometerBinder extends Binder {
        MyService getSpeedometer() {
            return MyService.this;
        }
    }
    @Override
    public void onCreate(){
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation==null) {
                    lastLocation=location;
                }
                distanceKilometter+=location.distanceTo(lastLocation);
                speed=location.getSpeed();
                lastLocation=location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {LocationManager locationManager =(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10,listener);}
        else
        {
            Toast toast = Toast.makeText(this,"No permission",Toast.LENGTH_LONG);
            toast.show();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
