package com.example.test2;

public interface UserSearchContract {
    interface View{
        void userRemove(User user);
        void userAdd(User user);
        void userChanged(User user);
    }

    interface Presenter{
        void userRemove(User user);
        void userAdd(User user);
        void userChanged(User user);
        void startSearch(boolean connected);
        void stopSearch();
    }

    interface Interactor{
        void startSearch(boolean connected);
        void stopSearch();
    }
}
