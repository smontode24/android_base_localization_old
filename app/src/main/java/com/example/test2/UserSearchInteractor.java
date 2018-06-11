package com.example.test2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserSearchInteractor implements UserSearchContract.Interactor{

    private UserSearchContract.Presenter presenter;
    private List<User> usersLocateds;
    private ListenerRegistration listener;

    public UserSearchInteractor(UserSearchContract.Presenter presenter){
        this.presenter = presenter;
    }

    @Override
    public void startSearch(final boolean connected) {
        if(listener != null) return;
        if(usersLocateds == null) usersLocateds = new ArrayList<>();
        listener = FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                for(DocumentSnapshot d: queryDocumentSnapshots.getDocuments()){
                    if(d != null) {
                        User user = d.toObject(User.class);
                        user.setId(d.getId());
                        if(user.getLocation() == null) continue;
                        if(user.getLocation().size() == 0) continue;
                        final User userN = user;
                        if(connected) {
                            FirebaseDatabase.getInstance().getReference(Constants.USERS_PATH).child(user.getId()).child("connected").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Boolean bool = (Boolean) dataSnapshot.getValue();
                                    if (usersLocateds.contains(userN)) {
                                        if (bool) {
                                            presenter.userChanged(userN);
                                        } else {
                                            presenter.userRemove(userN);
                                            usersLocateds.remove(userN);
                                        }
                                    } else {
                                        if (bool) {
                                            usersLocateds.add(userN);
                                            presenter.userAdd(userN);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else{
                            if (usersLocateds.contains(userN)) {

                                    presenter.userChanged(userN);
                            } else {
                                usersLocateds.add(userN);
                                    presenter.userAdd(userN);

                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void stopSearch() {
        if(listener == null) return;
        listener.remove();
        usersLocateds = new ArrayList<>();
        listener = null;
    }
}
