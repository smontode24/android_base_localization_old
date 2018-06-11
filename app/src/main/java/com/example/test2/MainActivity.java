package com.example.test2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements AuthContract.View{

    private Activity thisActivity;
    private EditText text_email;
    private EditText text_pass;
    private AuthContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        presenter = new AuthPresenter(this);
        text_email = findViewById(R.id.text_email);
        text_pass = findViewById(R.id.text_pass);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference(Constants.USERS_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("connected").setValue(true);
            FirebaseDatabase.getInstance().getReference(Constants.USERS_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("connected").onDisconnect().setValue(false);
            onSuccess();
        }

        ((Button)findViewById(R.id.btn_log)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.loggin(text_email.getText().toString(),text_pass.getText().toString());
            }
        });
        ((Button)findViewById(R.id.btn_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onSuccess() {
        ((MyController)getApplication()).startListening();
        Intent intent = new Intent(this,ScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFailure() {
        Toast.makeText(this,"INCORRECT",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLogout() {}


}
