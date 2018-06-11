package com.example.test2;

public class UserSearchPresenter implements UserSearchContract.Presenter{

    private UserSearchContract.View view;
    private UserSearchContract.Interactor interactor;

    public UserSearchPresenter(UserSearchContract.View view){
        this.view = view;
        interactor = new UserSearchInteractor(this);
    }

    @Override
    public void userRemove(User user) {
        view.userRemove(user);
    }

    @Override
    public void userAdd(User user) {
        view.userAdd(user);
    }

    @Override
    public void userChanged(User user) {
        view.userChanged(user);
    }

    @Override
    public void startSearch(boolean connected) {
        interactor.startSearch(connected);
    }

    @Override
    public void stopSearch() {
        interactor.stopSearch();
    }
}
