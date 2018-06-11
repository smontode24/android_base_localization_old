package com.example.test2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MessagesActivity extends AppCompatActivity{
    private RecyclerView rv;
    private MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        rv = findViewById(R.id.rv_messages);
        adapter = new MessagesAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rv.setAdapter(adapter);
    }


    public void onStart(){
        super.onStart();
        FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                for(int i = 0; i < user.getMessages().size(); i++){
                    adapter.addMessage(user.getMessages().get(i),user.getFroms().get(i));
                }
            }
        });
    }


    public void onStop(){
        adapter.resetUsers();
        super.onStop();
    }
}
