package com.androiod.social_media_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.androiod.social_media_app.Model.Story;
import com.androiod.social_media_app.Model.UserModel;
import com.androiod.social_media_app.Model.UserStories;
import com.androiod.social_media_app.R;
import com.devlomi.circularstatusview.CircularStatusView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    public StoryAdapter(ArrayList<Story> list, Context context) {
        this.list = list;
        this.context = context;
    }

    ArrayList<Story> list;
    Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.story_rv_design,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Story story = list.get(position);
        if(story.getList().size()>0) {
            UserStories lastStory = story.getList().get(story.getList().size() - 1);
            Picasso.get()
                    .load(lastStory.getImage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.story);
            holder.circleStatus.setPortionsCount(story.getList().size());

            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(story.getStoryBy())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            Picasso.get()
                                    .load(user.getProfilePhoto())
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.profile);
                            holder.name.setText(user.getUsername());

                            holder.story.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (UserStories stories : story.getList()) {
                                        myStories.add(new MyStory(
                                                stories.getImage()
                                        ));
                                    }
                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories) // Required
                                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                            .setTitleText(user.getUsername()) // Default is Hidden
                                            .setSubtitleText("") // Default is Hidden
                                            .setTitleLogoUrl(user.getProfilePhoto()) // Default is Hidden
                                            .setStoryClickListeners(new StoryClickListeners() {
                                                @Override
                                                public void onDescriptionClickListener(int position) {
                                                    //your action
                                                }

                                                @Override
                                                public void onTitleIconClickListener(int position) {
                                                    //your action
                                                }
                                            }) // Optional Listeners
                                            .build() // Must be called before calling show method
                                            .show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView story,storyType,profile;
        TextView name;
        CircularStatusView circleStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story = itemView.findViewById(R.id.story);
            storyType = itemView.findViewById(R.id.storyType);
            profile = itemView.findViewById(R.id.profileImg);
            name = itemView.findViewById(R.id.username);
            circleStatus = itemView.findViewById(R.id.circle);

        }
    }

}
