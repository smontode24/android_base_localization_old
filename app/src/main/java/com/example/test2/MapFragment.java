package com.example.test2;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapFragment extends Fragment implements UserSearchContract.View,OnMapReadyCallback,NetworkAndLocationReceiver.NetworkAndLocationNotify{

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private User userSelected;
    private UserSearchContract.Presenter presenter;
    private HashMap<Marker,User> users;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_mapa, container, false);
        mapFragment = new SupportMapFragment();
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        //Introduïm el fragment en el FrameLayout
        getFragmentManager().beginTransaction().add(R.id.fragment_map, mapFragment).commit();
        presenter = new UserSearchPresenter(this);
        return v;
    }

    @Override
    public void userRemove(User user) {
        if(users == null) return;
        Set<Marker> markers = users.keySet();
        for(Marker marker: markers){
            if(users.get(marker).equals(user)){
                marker.remove();
                users.remove(marker);
                return;
            }
        }
    }

    @Override
    public void userAdd(User user) {
        if(users == null) users = new HashMap<>();
        Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(user.getLocation().get("latitude"),user.getLocation().get("longitude"))));
        users.put(marker,user);
    }

    @Override
    public void userChanged(User user) {
        if(users == null) return;
        Set<Marker> markers = users.keySet();
        for(Marker marker: markers){
            if(users.get(marker).equals(user)){
                users.remove(marker);
                users.put(marker,user);
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(new MarkerListener());
        startListening();
        try {
            googleMap.setMyLocationEnabled(true);
        }catch (SecurityException e){}
    }

    public void onAttach(Context context){
        super.onAttach(context);
        stopListening();
        startListening();
    }

    public void onStart(){
        super.onStart();
        ((MyController)getActivity().getApplication()).startListeningReceiver(this);
    }

    public void onStop(){
        ((MyController)getActivity().getApplication()).stopListeningReceiver();
        super.onStop();
    }

    private void permissionGps(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
    }

    private void externalStorage(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            } else {
                //do nothing
            }
            return;
        }
        if(requestCode == 1){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
                Toast.makeText(this.getActivity(),"Buena esa",Toast.LENGTH_LONG).show();
            } else {
                //do nothing
            }
            return;
        }
    }

    private void startListening(){
        if(getActivity() == null) return;
        MyController c = (MyController)getActivity().getApplication();
        if(!c.gpsPermission()){
            permissionGps();
            return;
        }
        if(!c.externalStoragePermission()){
            externalStorage();
            return;
        }

        if(c.validLocationEthernet()){
            if(presenter != null)
                presenter.startSearch(true);
        }else{
            Toast.makeText(this.getActivity(),"connectat",Toast.LENGTH_SHORT).show();
        }
    }

    private void stopListening(){
        if(presenter != null)
            presenter.stopSearch();
    }
    @Override
    public void networkConnected() {
        startListening();
    }

    @Override
    public void networkDisconnected() {
        stopListening();
    }

    @Override
    public void gpsConnected() {
        startListening();
    }

    @Override
    public void gpsDisconnected() {
        stopListening();
    }

    class MarkerListener implements GoogleMap.OnMarkerClickListener{

        @Override
        public boolean onMarkerClick(Marker marker) {

            if(users.containsKey(marker)){
                userSelected = users.get(marker);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                 v = getLayoutInflater().inflate(R.layout.dialog_message,null);
                ((TextView)v.findViewById(R.id.text_name)).setText(userSelected.getName());
                AlertDialog ad = builder.setView(v).setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                User user = task.getResult().toObject(User.class);
                                final Message message = new Message(user.getName(),((EditText)v.findViewById(R.id.message_enviar)).getText().toString());
                                FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(userSelected.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        User userMessage = task.getResult().toObject(User.class);
                                        List<String> messages = userMessage.getMessages();
                                        List<String> froms = userMessage.getFroms();
                                        if(messages == null) messages = new ArrayList<>();
                                        if(froms == null) froms = new ArrayList<>();
                                        messages.add(message.getMessage());
                                        froms.add(message.getFrom());
                                        FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(userSelected.getId())
                                                .update("messages",messages);
                                        FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(userSelected.getId())
                                                .update("froms",froms);
                                    }
                                });
                            }
                        });
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                ad.show();
            }
            return false;
        }
    }
}
