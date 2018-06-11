package com.example.test2;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.auth.api.Auth;

public class ScreenActivity extends AppCompatActivity implements AuthContract.View{
    private Activity thisActivity;
    private AuthContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        thisActivity = this;
        presenter = new AuthPresenter(this);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
        actionbar.setTitle("TEST_LOC");

        AHBottomNavigation btm = (AHBottomNavigation) findViewById(R.id.btm_nav);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Personas",getDrawable(R.drawable.common_google_signin_btn_icon_dark_focused));
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Mapa", getDrawable(R.drawable.common_google_signin_btn_icon_light_focused));
        btm.addItem(item1);
        btm.addItem(item2);
        btm.setCurrentItem(0);

        btm.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Fragment fragment = null;
                switch (position){
                    case 0:
                        fragment = new PeopleFragment();
                        break;
                    case 1:
                        fragment = new MapFragment();
                        break;
                }

                if(fragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment)
                            .commitNow();
                }
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new PeopleFragment())
                .commitNow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        Drawable d = menu.getItem(0).getIcon();
        if(d == null) return true;
        d.mutate();
        d.setTint(getResources().getColor(R.color.colorAccent,null));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.item_messages:
                Intent intent = new Intent(this,MessagesActivity.class);
                startActivity(intent);
                return true;
            case R.id.item_logout:
                presenter.logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSuccess() {
    }

    @Override
    public void onFailure() {
    }

    @Override
    public void onLogout() {
        ((MyController)getApplication()).stopListening();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
