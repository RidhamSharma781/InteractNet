package com.androiod.social_media_app.Adapter;

import static com.androiod.social_media_app.R.drawable.following_btn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.androiod.social_media_app.Model.Follow;
import com.androiod.social_media_app.Model.Notification;
import com.androiod.social_media_app.Model.UserModel;
import com.androiod.social_media_app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    ArrayList<UserModel> list;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public UserAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        View view = LayoutInflater.from(context).inflate(R.layout.user_rv_sample, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserModel userModel = list.get(position);
        Picasso.get()
                .load(userModel.getProfilePhoto())
                .placeholder(R.drawable.placeholder)
                .into(holder.profilePhoto);

        holder.username.setText(userModel.getUsername());
        holder.about.setText(userModel.getAbout());
        database.getReference()
                .child("Users")
                .child(userModel.getUserID())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, following_btn));
                            holder.followBtn.setText("Following");
                            holder.followBtn.setTextColor(context.getColor(R.color.black));
                            holder.followBtn.setEnabled(false);
                        }else{
                            holder.followBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Follow follow = new Follow();
                                    follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                    follow.setFollowedAt(new Date().getTime());

                                    database.getInstance().getReference()
                                            .child("Users")
                                            .child(userModel.getUserID())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    database.getInstance().getReference()
                                                            .child("Users")
                                                            .child(userModel.getUserID())
                                                            .child("followersCount")
                                                            .setValue(userModel.getFollowersCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                }
                                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    database.getInstance().getReference()
                                                                            .child("Users")
                                                                            .child(FirebaseAuth.getInstance().getUid())
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    UserModel user = snapshot.getValue(UserModel.class);
                                                                                    database.getInstance().getReference()
                                                                                            .child("Users")
                                                                                            .child(FirebaseAuth.getInstance().getUid())
                                                                                            .child("followingCount")
                                                                                            .setValue(user.getFollowingCount()+1);
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                }
                                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, following_btn));
                                                                    holder.followBtn.setText("Following");
                                                                    holder.followBtn.setTextColor(context.getColor(R.color.black));
                                                                    holder.followBtn.setEnabled(false);
                                                                    Toast.makeText(context, "Your followed " + userModel.getUsername(), Toast.LENGTH_SHORT).show();

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setType("follow");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(userModel.getUserID())
                                                                            .push()
                                                                            .setValue(notification);
                                                                }
                                                            });
                                                }
                                            });


                                }
                            });
                        }
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePhoto;
        TextView username, about;
        Button followBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            username = itemView.findViewById(R.id.username);
            about = itemView.findViewById(R.id.about);
            followBtn = itemView.findViewById(R.id.followBtn);
        }
    }
}
