package com.example.hunter95.mynewtrackingapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Hunter95 on 1/2/2017.
 */
public class TrackingService extends IntentService {
    private double longitude;
    private double latitude;
    private String id;
    private DatabaseReference database;

    public  TrackingService()
    {
        super("TrackingSerivce");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TrackingService(String name) {
        super(name);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        database = FirebaseDatabase.getInstance().getReference();
        id = intent.getStringExtra("Id");
        if(getCurrentLoc()) {
            database.child("Account").child(id).child("curAlditude").setValue(latitude);
            database.child("Account").child(id).child("curLongditude").setValue(longitude);
        }
    }
    public boolean getCurrentLoc() {
        TrackGPS gps = new TrackGPS(this);
        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            return true;
        } else {
            Handler mHandler = new Handler();
            mHandler.post(new DisplayToast("did something"));
            //gps.showSettingsAlert();
            return false;
        }
    }
    private class DisplayToast implements Runnable{
        String mText;

        public DisplayToast(String text){
            mText = text;
        }

        public void run(){
            Toast.makeText(TrackingService.this, mText, Toast.LENGTH_SHORT).show();
        }
    }
}
