package com.example.test2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkAndLocationReceiver extends BroadcastReceiver{

    private NetworkAndLocationNotify interested;

    public NetworkAndLocationReceiver(NetworkAndLocationNotify interested){
        this.interested = interested;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.location.PROVIDERS_CHANGED")){
            LocationManager lm = (LocationManager) MyController.getContext().getSystemService(Context.LOCATION_SERVICE);
            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
                interested.gpsConnected();
            else
                interested.gpsDisconnected();
        }else if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
            ConnectivityManager conn =  (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            //if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI){

            if(networkInfo != null && networkInfo.isConnected()){
                interested.networkConnected();
            }else{
                interested.networkDisconnected();
            }
        }
    }

    public interface NetworkAndLocationNotify{
        void networkConnected();
        void networkDisconnected();
        void gpsConnected();
        void gpsDisconnected();
    }

}
