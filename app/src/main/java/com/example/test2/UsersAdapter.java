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

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<User> users;
    private View.OnClickListener listener;
    private LatLng locationUser;

    /**
     * Redefinició de la classe ViewHolder per aquest adapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userText;
        public TextView distText;
        public ImageView image;

        public ViewHolder(View v) {
            super(v);
            userText = v.findViewById(R.id.name_people);
            distText = (TextView) v.findViewById(R.id.dist_people);
            image = v.findViewById(R.id.image_people);
        }
    }

    /**
     * Constructor de la classe
     */
    public UsersAdapter() {
        this.users = new ArrayList<>();
    }

    /**
     * Mètode que crea el ViewHolder donat el layout que hem creat per l'adapter
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_people_item, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked(v);
            }
        });
        return new UsersAdapter.ViewHolder(v);
    }

    public void setListener(View.OnClickListener list) {
        listener = list;
    }

    public void setLocationUser(LatLng ll){
        locationUser = ll;
        notifyDataSetChanged();
    }

    private void clicked(View v) {
        if (listener != null)
            listener.onClick(v);
    }

    /**
     * Mètode que afegeix la bandera i el nom de l'idioma al ViewHolder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.userText.setText(users.get(position).getName());

        if(locationUser != null) {
            float[] d = new float[1];
            Location.distanceBetween(users.get(position).getLocation().get("latitude"),users.get(position).getLocation().get("longitude"),
                    locationUser.latitude,locationUser.longitude,d);
            holder.distText.setText(d[0] + " km");

        }else
            holder.distText.setText("N/A");
        Picasso.get().load(users.get(position).getDownloadUri()).placeholder(R.drawable.common_full_open_on_phone).into(holder.image);
    }


    /**
     * Mètode que retorna el nombre d'idiomes que hi haurà a la llista.
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addUser(User user) {
        users.add(user);
        notifyDataSetChanged();
    }

    public void removeUser(User user) {
        users.remove(user);
        notifyDataSetChanged();
    }

    public void updateUser(User user) {
        int i = 0;
        for (User userX : users) {
            if (userX.equals(user)) {
                users.set(i, user);
                notifyDataSetChanged();
                return;
            }
            i++;
        }
    }

    public void resetUsers() {
        users = new ArrayList<>();
        notifyDataSetChanged();
    }
}
