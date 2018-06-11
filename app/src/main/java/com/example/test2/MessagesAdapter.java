package com.example.test2;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private ArrayList<String> user;
    private ArrayList<String> message;

    /**
     * Redefinició de la classe ViewHolder per aquest adapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userText;
        public TextView messageText;

        public ViewHolder(View v) {
            super(v);
            userText = v.findViewById(R.id.name_mes);
            messageText = (TextView) v.findViewById(R.id.messages_mes);
        }
    }

    /**
     * Constructor de la classe
     */
    public MessagesAdapter() {
        this.message = new ArrayList<>();
        this.user = new ArrayList<>();
    }

    /**
     * Mètode que crea el ViewHolder donat el layout que hem creat per l'adapter
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_item, parent, false);
        return new MessagesAdapter.ViewHolder(v);
    }
    /**
     * Mètode que afegeix la bandera i el nom de l'idioma al ViewHolder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position) {
        holder.messageText.setText(user.get(position));
        holder.userText.setText(message.get(position));
    }


    /**
     * Mètode que retorna el nombre d'idiomes que hi haurà a la llista.
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return user.size();
    }

    public void addMessage(String user, String message) {
        this.user.add(user);
        this.message.add(message);
        notifyDataSetChanged();
    }

    public void removeUser(String user, String message) {
        this.user.remove(user);
        this.message.remove(message);
        notifyDataSetChanged();
    }

    public void resetUsers() {
        user = new ArrayList<>();
        message = new ArrayList<>();
        notifyDataSetChanged();
    }
}