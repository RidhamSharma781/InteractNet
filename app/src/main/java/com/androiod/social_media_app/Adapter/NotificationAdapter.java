package com.androiod.social_media_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.androiod.social_media_app.CommentActivity;
import com.androiod.social_media_app.Model.Notification;
import com.androiod.social_media_app.Model.UserModel;
import com.androiod.social_media_app.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHoder> {

    ArrayList<Notification> list;
    Context context;

    public NotificationAdapter(ArrayList<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification2_rv_design, parent, false);
        return new ViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {
        Notification model = list.get(position);
        String time = TimeAgo.using(model.getNotificationAt());
        holder.time.setText(time);
        String type = model.getType();

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            Picasso.get()
                                    .load(user.getProfilePhoto())
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.profile);
                            if (type.equals("like")) {
                                holder.notification.setText(Html.fromHtml("<b>" + user.getUsername() + "</b>" + " liked your post"));
                            } else if (type.equals("comment")) {
                                holder.notification.setText(Html.fromHtml("<b>" + user.getUsername() + "</b>" + " commented on your post"));
                            } else {
                                holder.notification.setText(Html.fromHtml("<b>" + user.getUsername() + "</b>" + " started following you"));
                            }
                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                if (!type.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("notification")
                            .child(model.getPostedBy())
                            .child(model.getNotificationId())
                            .child("checkOpen")
                            .setValue(true);
                    holder.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", model.getPostId());
                    intent.putExtra("postedBy", model.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    holder.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }

            }
        });

        boolean checkOpen = model.isCheckOpen();
        if(checkOpen == true){
            holder.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }else{

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHoder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView notification, time;
        ConstraintLayout openNotification;

        public ViewHoder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profileImg);
            notification = itemView.findViewById(R.id.notification);
            time = itemView.findViewById(R.id.time);
            openNotification = itemView.findViewById(R.id.openNotification);
        }
    }

}
