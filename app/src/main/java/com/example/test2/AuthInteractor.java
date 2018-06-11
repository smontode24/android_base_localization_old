package com.example.test2;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class AuthInteractor implements AuthContract.Interactor{

    private AuthContract.Presenter presenter;

    public AuthInteractor(AuthContract.Presenter presenter){
        this.presenter = presenter;
    }

    @Override
    public void loggin(String email, String pass) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference(Constants.USERS_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("connected").setValue(true);
                    FirebaseDatabase.getInstance().getReference(Constants.USERS_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("connected").onDisconnect().setValue(false);
                    presenter.onSuccess();
                }else{
                    presenter.onFailure();
                }
            }
        });
    }

    @Override
    public void register(final String email, final String pass, final String name, final Uri image) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final User user = new User(name,email,pass,image);
                    FirebaseStorage.getInstance().getReference(Constants.USERS_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg")
                            .putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            User updatedUser = user;
                            updatedUser.setDownloadUri(task.getResult().getDownloadUrl().toString());
                            FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(updatedUser.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseDatabase.getInstance().getReference(Constants.USERS_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("connected").setValue(true);
                                    FirebaseDatabase.getInstance().getReference(Constants.USERS_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("connected").onDisconnect().setValue(false);
                                }
                            });
                        }
                    });

                    presenter.onSuccess();
                }else{
                    presenter.onFailure();
                }
            }
        });
    }

    @Override
    public void logout() {
        FirebaseDatabase.getInstance().getReference(Constants.USERS_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("connected").setValue(false);
        FirebaseAuth.getInstance().signOut();
        presenter.onLogout();
    }
}
