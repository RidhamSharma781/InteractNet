package com.androiod.social_media_app.Fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androiod.social_media_app.Adapter.PostAdapter;
import com.androiod.social_media_app.Adapter.StoryAdapter;
import com.androiod.social_media_app.Model.Post;
import com.androiod.social_media_app.Model.Story;
import com.androiod.social_media_app.Model.UserModel;
import com.androiod.social_media_app.Model.UserStories;
import com.androiod.social_media_app.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    RoundedImageView addStory;
    ActivityResultLauncher galleryLauncher;
    ProgressDialog dialog;
    CircleImageView profilePhoto;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);

        ShimmerRecyclerView dashboardRv,storyRv;


        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dashboardRv = view.findViewById(R.id.dashboardRv);
        dashboardRv.showShimmerAdapter();

        storyRv = view.findViewById(R.id.storyRv);
        storyRv.showShimmerAdapter();
        ArrayList<Story> storyList = new ArrayList<>();


        StoryAdapter storyAdapter = new StoryAdapter(storyList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRv.setLayoutManager(layoutManager);
        storyRv.setNestedScrollingEnabled(false);


        database.getReference()
                .child("stories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            storyList.clear();
                            for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                                Story story = new Story();
                                story.setStoryBy(storySnapshot.getKey());
                                story.setStoryAt((String) storySnapshot.child("postedBy").getValue());

                                ArrayList<UserStories> userStories = new ArrayList<>();
                                for (DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()) {
                                    UserStories stories = snapshot1.getValue(UserStories.class);
                                    userStories.add(stories);
                                }
                                story.setList(userStories);
                                storyList.add(story);
                            }
                        }
                        storyRv.setAdapter(storyAdapter);
                        storyRv.hideShimmerAdapter();
                        storyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        ArrayList<Post> postList = new ArrayList<>();

        PostAdapter postAdapter = new PostAdapter(postList, getContext());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());

        dashboardRv.setLayoutManager(layoutManager1);
        dashboardRv.setNestedScrollingEnabled(false);


        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                    post.setPostId(dataSnapshot.getKey());
                }
                dashboardRv.hideShimmerAdapter();
                dashboardRv.setAdapter(postAdapter);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addStory = view.findViewById(R.id.story);
        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                addStory.setImageURI(result);
                dialog.show();
                final StorageReference reference = storage.getReference()
                        .child("stories")
                        .child(auth.getUid())
                        .child(new Date().getTime() + "");
                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Story story = new Story();
                                story.setStoryAt(new Date().getTime() + "");

                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("postedBy")
                                        .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                UserStories stories = new UserStories(uri.toString(), story.getStoryAt() + "");

                                                database.getReference()
                                                        .child("stories")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("userStories")
                                                        .push()
                                                        .setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                    }
                });

            }
        });
        profilePhoto = view.findViewById(R.id.profileImg);
        database.getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfilePhoto())
                                .placeholder(R.drawable.placeholder)
                                .into(profilePhoto);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;

    }
}