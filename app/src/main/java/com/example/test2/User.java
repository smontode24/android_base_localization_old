package com.example.test2;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private String name;
    private String email;
    private String pass;
    @Exclude private Uri image;
    @Exclude private String id;
    private String downloadUri;
    private Map<String,Double> location;
    private List<String> messages;
    private List<String> froms;

    public User(){}

    public User(String name,String email,String pass,Uri image){
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.image = image;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("email",email);
        map.put("pass",pass);
        map.put("downloadUri",downloadUri);
        map.put("location",location);
        map.put("messages",messages);
        map.put("froms",froms);
        return map;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public boolean equals(Object object){
        if(object instanceof User){
            User user = (User)object;
            if(user.getId().equals(getId()))
                return true;
        }
        return false;
    }

    public Map<String, Double> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Double> location) {
        this.location = location;
    }


    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getFroms() {
        return froms;
    }

    public void setFroms(List<String> froms) {
        this.froms = froms;
    }
}
