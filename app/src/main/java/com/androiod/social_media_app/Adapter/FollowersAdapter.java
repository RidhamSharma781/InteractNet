package com.androiod.social_media_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androiod.social_media_app.Model.Follow;
import com.androiod.social_media_app.Model.UserModel;
import com.androiod.social_media_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViweHolder> {

    Context context;
    ArrayList<Follow> list = new ArrayList<>();

    public FollowersAdapter(Context context, ArrayList<Follow> list) {
        this.context = context;
        this.list = list;
    }

    public FollowersAdapter(ArrayList<Follow> list, Context context) {
    }

    @NonNull
    @Override
    public ViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_rv_sample,parent,false);
        return new ViweHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViweHolder holder, int position) {

        Follow follow = list.get(position);
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(follow.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            Picasso.get()
                                    .load(user.getProfilePhoto())
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.profileImg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViweHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        public ViweHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profileImg);
        }
    }

}
