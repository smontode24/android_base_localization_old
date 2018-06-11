package com.example.test2;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceLocation extends Service{

    private LocationListener listener;
    private LocationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (LocationManager)((MyController)getApplication()).getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateUserLocation(location);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Notifica segundo solo si el usuario ha caminado 5 metros
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000
                    , 10, listener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000
                    , 10, listener);
            getLastKnownLocation();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getLastKnownLocation() {
        List<String> providers = manager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location l = manager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Encontrada mejor localizacion
                bestLocation = l;
                updateUserLocation(bestLocation);
                return;
            }
        }
        return;
    }

    private void updateUserLocation(Location location){
        if(FirebaseAuth.getInstance().getCurrentUser() == null) return;
        Map<String,Object> map = new HashMap<>();
        map.put("latitude",location.getLatitude());
        map.put("longitude",location.getLongitude());
        FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("location",map);
    }
}
