package com.example.test2;

import android.Manifest;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

public class MyController extends Application {
    public static Context context;
    public boolean listening;
    private NetworkAndLocationReceiver receiver;
    private Intent service;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        listening = false;
    }

    public static Context getContext() {
        return context;
    }

    public boolean gpsPermission(){
        return (ContextCompat.checkSelfPermission(MyController.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    public boolean externalStoragePermission(){
        return (ContextCompat.checkSelfPermission(MyController.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    public boolean validLocationEthernet(){

        ConnectivityManager conn =  (ConnectivityManager)
                MyController.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        return ((LocationManager) MyController.getContext().getSystemService(LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)
                && networkInfo != null && networkInfo.isConnected();
    }

    public void startListening(){
        if(!listening){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                listening = true;
                service = new Intent(this, ServiceLocation.class);
                startService(service);
            }
        }
    }

    public void startListeningReceiver(NetworkAndLocationReceiver.NetworkAndLocationNotify interested){
        stopListeningReceiver();
        receiver = new NetworkAndLocationReceiver(interested);
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.location.PROVIDERS_CHANGED");
        registerReceiver(receiver,filter);
    }

    public void stopListeningReceiver(){
        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    public void stopListening(){
        if(service != null && listening){
            listening = false;
            stopService(service);
        }

    }
}
