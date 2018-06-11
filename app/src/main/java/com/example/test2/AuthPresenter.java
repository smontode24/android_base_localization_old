package com.example.test2;

import android.net.Uri;

import com.google.android.gms.auth.api.Auth;

public class AuthPresenter implements AuthContract.Presenter{

    private AuthContract.View view;
    private AuthContract.Interactor interactor;

    public AuthPresenter(AuthContract.View view){
        this.view = view;
        interactor = new AuthInteractor(this);
    }

    @Override
    public void onSuccess() {
        view.onSuccess();
    }

    @Override
    public void onFailure() {
        view.onFailure();
    }

    @Override
    public void loggin(String email, String pass) {
        interactor.loggin(email,pass);
    }

    @Override
    public void register(String email, String pass, String name, Uri image) {
        if(email == null || pass == null || name == null || image == null) {
            onFailure();
            return;
        }
        if(email.equals("") || pass.equals("") || name.equals("") || image.equals("")) {
            onFailure();
            return;
        }
        interactor.register(email,pass,name,image);
    }

    @Override
    public void logout() {
        interactor.logout();
    }

    @Override
    public void onLogout() {
        view.onLogout();
    }
}
