package sk.tuke.smart.makac.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static sk.tuke.smart.makac.helpers.SportActivities.countCalories;

public class TrackerService extends Service implements LocationListener {
    Location location;
    LocationManager locationManager;
    private static final long MIN_DISTANCE = 10;
    private static final long MIN_TIME = 3000;
    private long duration;
    private int state;
    private int STATE_STOPPED = 0;
    private int STATE_RUNNING = 1;
    private int STATE_PAUSED = 2;
    private int STATE_CONTINUE = 3;
    private double distance,pace;
    private ArrayList<Location> locationList;
    private long startTime;
    private long seconds =0;
    private long lastUpdate;
    private long diff;
    private Timer myTimer;
    private double distancePause;
    private List<Float> speedList;
    private Location lastLocation = null;
    private float calculatedSpeed = 0;
    private long timeBefore = 0;
    private long timeAfter = 0;
    private double timeFillingSpeedListInHours;
    private double calories=0;
    private int speedCount=0;


    @Override
    public void onCreate(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);




    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals("sk.tuke.smart.makac.COMMAND_START") || intent.getAction().equals("sk.tuke.smart.makac.COMMAND_CONTINUE") ) {
            state = intent.getAction().equals("sk.tuke.smart.makac.COMMAND_START") ? STATE_RUNNING : STATE_CONTINUE;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permissions");
            }
            if(speedList==null){
                speedList = new ArrayList<>();
            }

            if (location == null) {
                location = new Location("");
                location.setLatitude(48.69715527027366);
                location.setLongitude(21.2339755238645);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

            myTimer = new Timer();
            if(state==2){
                startTime = duration;
            }
            else {
                startTime = SystemClock.elapsedRealtime();
            }
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (state==1 || state==3) {
                        duration += SystemClock.elapsedRealtime() - startTime;
                        startTime = SystemClock.elapsedRealtime();
                        startBroadcast();
                    }
                }

            }, 0, 1000);
        }
        if(intent.getAction().equals("sk.tuke.smart.makac.COMMAND_PAUSE")) {
            locationManager.removeUpdates(this);
            state = STATE_PAUSED;
            distancePause = distance;
            startBroadcast();
        }
        if(intent.getAction().equals("sk.tuke.smart.makac.COMMAND_STOP")) {
            locationManager.removeUpdates(this);
            locationManager = null;
            if (myTimer != null) {
                myTimer.cancel();
            }
            stopSelf();
            state = STATE_STOPPED;
            startBroadcast();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(this.location.distanceTo(location)>=2) {
            if (lastLocation != null) {
                float elapsedTime = (location.getTime() - lastLocation.getTime()) / 1000; // Convert milliseconds to seconds
                calculatedSpeed = lastLocation.distanceTo(location) / elapsedTime;
            }
            this.lastLocation = location;

            float speed = location.hasSpeed() ? location.getSpeed() : calculatedSpeed;
            if(speedCount!=2){
                speedList.add(speed);
                speedCount++;
            }
            if(speedList.size()==1){
                timeBefore = duration;
            }
            else{
                timeAfter = duration;
                timeFillingSpeedListInHours=((((timeAfter-timeBefore)/1000)/60)/60) % 24;
            }

            if (distancePause != 0) {
                if ((this.location.distanceTo(location)) <= 100) {
                    this.distance += this.location.distanceTo(location);
                }
                distancePause = 0;
            } else {
                this.distance += this.location.distanceTo(location);
            }
            if (lastUpdate == 0) {
                lastUpdate = duration;

            } else {
                diff = ((duration/1000) % 60 - (lastUpdate / 1000)% 60);
                if (diff >= 6) {
                    lastUpdate = duration;
                    double kilometers = Math.round((distance / 1000) * 100) / 100.00;
                    pace = kilometers / diff;

                }
            }
            if (locationList == null || state==3) {
                locationList = new ArrayList<>();
            }
            locationList.add(location);
            this.location = location;
            startBroadcast();
        }
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


    public int getState(){
        return state;
    }
    public long getDuration(){
        return duration;
    }
    public double getDistance(){
        return  distance;
    }
    public double getPace(){
        return pace;
    }
    private ArrayList<Location> getLocationList(){
        return this.locationList;
    }


    private void startBroadcast(){
        Intent send=new Intent();
        send.setAction("sk.tuke.smart.makac.TICK");
        send.putExtra("duration",getDuration());
        send.putExtra("distance",getDistance());
        send.putExtra("pace",getPace());
        send.putExtra("state",getState());
        if(state==0 || state==2){
            send.putParcelableArrayListExtra("location", getLocationList());
        }
        send.putExtra("calories", calories);

        if(speedCount==2){
            speedCount=0;
            if(calories==0) {
                calories = countCalories(0, 80, speedList, timeFillingSpeedListInHours);
                System.out.println("tusmeeeee"+calories);
                send.putExtra("calories", calories);

            }
            else {
                calories += countCalories(0, 80, speedList, timeFillingSpeedListInHours);
                System.out.println("tupridadsff"+calories);
                send.putExtra("calories", calories);

            }

        }
        //send.putExtra("calories", calories);
        sendBroadcast(send);

    }


}
