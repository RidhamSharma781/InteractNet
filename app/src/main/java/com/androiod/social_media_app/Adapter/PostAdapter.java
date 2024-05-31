package com.androiod.social_media_app.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androiod.social_media_app.CommentActivity;
import com.androiod.social_media_app.Model.Notification;
import com.androiod.social_media_app.Model.Post;
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
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    ArrayList<Post> list;
    Context context;
    FirebaseDatabase database;
    FirebaseAuth auth;

    public PostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post model = list.get(position);
        Picasso.get()
                .load(model.getPostImg())
                .placeholder(R.drawable.placeholder)
                .into(holder.postImg);
        String description = model.getPostDescription();
        if (description == "") {
            holder.postDescription.setVisibility(View.GONE);
        } else {
            holder.postDescription.setText(model.getPostDescription());
            holder.postDescription.setVisibility(View.VISIBLE);
        }


        database.getInstance().getReference().child("Users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfilePhoto())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.profilePhoto);
                        holder.username.setText(user.getUsername());
                        holder.like.setText(model.getPostLike() + "");
                        holder.comment.setText(model.getCommentCount()+"");
                        holder.profession.setText(user.getProfession());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        database.getInstance().getReference().child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.liked, 0, 0, 0);
                        }else{
                            holder.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    database.getInstance().getReference().child("posts")
                                            .child(model.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    database.getInstance().getReference().child("posts")
                                                            .child(model.getPostId())
                                                            .child("postLike")
                                                            .setValue(model.getPostLike() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.liked, 0, 0, 0);

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setPostedBy(model.getPostedBy());
                                                                    notification.setPostId(model.getPostId());
                                                                    notification.setType("like");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(model.getPostedBy())
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
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy",model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Insert subject here");
                String url = "Share";
                intent.putExtra(Intent.EXTRA_TEXT,url);
                context.startActivity(Intent.createChooser(intent,"Share data on"));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(!Objects.equals(model.getPostedBy(), auth.getInstance().getUid())){
                   Toast.makeText(context, "You cannot delete this post", Toast.LENGTH_SHORT).show();
               }else{
                   AlertDialog.Builder builder = new AlertDialog.Builder(context);
                   builder.setTitle("Are you sure?")
                           .setMessage("This post will be permanently deleted")
                           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   database.getInstance().getReference().child("posts")
                                           .child(model.getPostId()).removeValue();


                               }
                           }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {

                               }
                           }).show();

               }


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView postImg, profilePhoto,delete;
        TextView username, postDescription, like,comment,share,profession;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImg = itemView.findViewById(R.id.postImage);
            profilePhoto = itemView.findViewById(R.id.profileImg);
            username = itemView.findViewById(R.id.userName);
            postDescription = itemView.findViewById(R.id.postDescription);
            like = itemView.findViewById(R.id.like);
            share = itemView.findViewById(R.id.share);
            comment = itemView.findViewById(R.id.comment);
            delete = itemView.findViewById(R.id.delete);
            profession = itemView.findViewById(R.id.professionPost);

        }
    }

}
