package com.example.test2;

import android.net.Uri;

public interface AuthContract {
    interface View{
        void onSuccess();
        void onFailure();
        void onLogout();
    }
    interface Presenter{
        void onSuccess();
        void onFailure();
        void loggin(String email,String pass);
        void register(String email, String pass, String name, Uri image);
        void logout();
        void onLogout();
    }
    interface Interactor{
        void loggin(String email,String pass);
        void register(String email, String pass, String name, Uri image);
        void logout();
    }
}
