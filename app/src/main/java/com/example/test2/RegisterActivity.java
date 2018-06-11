package com.example.test2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationMenu;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

public class RegisterActivity extends AppCompatActivity implements AuthContract.View{
    private EditText text_email;
    private EditText text_name;
    private ImageView image;
    private Uri uri;
    private android.widget.EditText text_pass;
    private AuthContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        presenter = new AuthPresenter(this);
        text_email = findViewById(R.id.text_email);
        text_pass = findViewById(R.id.text_pass);
        text_name = findViewById(R.id.text_name);
        image = findViewById(R.id.imageView);
        ((Button)findViewById(R.id.btn_seleccionar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Selecciona perfil"),1);
            }
        });
        ((Button)findViewById(R.id.btn_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.register(text_email.getText().toString(),text_pass.getText().toString(),text_name.getText().toString(),
                        uri);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Register");


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            image.setImageURI(imageUri);
            uri = imageUri;
        }
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
    public void onLogout() {

    }
}
