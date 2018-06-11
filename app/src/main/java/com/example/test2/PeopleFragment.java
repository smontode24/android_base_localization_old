package com.example.test2;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PeopleFragment extends Fragment implements UserSearchContract.View,NetworkAndLocationReceiver.NetworkAndLocationNotify{

    private RecyclerView rv;
    private UsersAdapter adapter;
    private Fragment fragment;
    private UserSearchContract.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_people, container, false);
        rv = v.findViewById(R.id.rv_people);
        fragment = this;
        rv.setLayoutManager(new LinearLayoutManager(MyController.getContext(), LinearLayout.VERTICAL,false));
        adapter = new UsersAdapter();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = task.getResult().toObject(User.class);
                    if(user == null) return;
                    if(user.getLocation() != null)
                        adapter.setLocationUser(new LatLng(user.getLocation().get("latitude"),user.getLocation().get("longitude")));
                }
            });
        }

        adapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fragment.getActivity(),((TextView)v.findViewById(R.id.name_people)).getText(),Toast.LENGTH_SHORT).show();
            }
        });
        presenter = new UserSearchPresenter(this);

        ((Button)v.findViewById(R.id.btn_refresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopListening();
                startListening();
            }
        });
        rv.setAdapter(adapter);
        return v;
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
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
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
                presenter.startSearch(false);
        }else{
            Toast.makeText(this.getActivity(),"connectat",Toast.LENGTH_SHORT).show();
        }
    }

    private void stopListening(){
        if(presenter != null)
            presenter.stopSearch();
        if(adapter != null)
            adapter.resetUsers();
    }

    @Override
    public void userRemove(User user) {
        adapter.removeUser(user);
    }

    @Override
    public void userAdd(User user) {
        adapter.addUser(user);
    }

    @Override
    public void userChanged(User user) {
        adapter.updateUser(user);
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
}
