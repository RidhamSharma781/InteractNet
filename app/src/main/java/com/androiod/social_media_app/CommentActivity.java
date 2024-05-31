package com.androiod.social_media_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiod.social_media_app.Adapter.CommentAdapter;
import com.androiod.social_media_app.Model.Comment;
import com.androiod.social_media_app.Model.Notification;
import com.androiod.social_media_app.Model.Post;
import com.androiod.social_media_app.Model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    Intent intent;
    String postId;
    String postedBy;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView postImg,commentPost;
    TextView like,postDescription,name,commentTv;
    CircleImageView profileImg;
    EditText commentBody;
    RecyclerView commentRv;
    ArrayList<Comment> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getWindow().setStatusBarColor(ContextCompat.getColor(CommentActivity.this,R.color.green));

        setSupportActionBar(findViewById(R.id.toolbar2));
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postImg = findViewById(R.id.postImg);
        like = findViewById(R.id.like);
        postDescription = findViewById(R.id.postDescrip);
        name = findViewById(R.id.name);
        profileImg = findViewById(R.id.profileImg);
        commentBody = findViewById(R.id.commentET);
        commentPost = findViewById(R.id.commentBTN);
        commentTv = findViewById(R.id.comment);
        commentRv = findViewById(R.id.commentRv);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        database.getReference()
                .child("Users")
                .child(postedBy).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfilePhoto())
                                .placeholder(R.drawable.placeholder)
                                .into(profileImg);
                        name.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);
                        Picasso.get()
                                .load(post.getPostImg())
                                .placeholder(R.drawable.placeholder)
                                .into(postImg);
                        like.setText(post.getPostLike()+"");
                        postDescription.setText(post.getPostDescription());
                        commentTv.setText(post.getCommentCount()+"");



                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        commentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment();
                comment.setCommentedBy(auth.getUid());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentBody(commentBody.getText().toString());

                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentCount = 0;
                                                if (snapshot.exists()){
                                                    commentCount = snapshot.getValue(Integer.class);
                                                }
                                                database.getReference()
                                                        .child("posts")
                                                        .child(postId)
                                                        .child("commentCount")
                                                        .setValue(commentCount+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                commentBody.setText("");
                                                                Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();

                                                                Notification notification = new Notification();
                                                                notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                notification.setNotificationAt(new Date().getTime());
                                                                notification.setPostedBy(postedBy);
                                                                notification.setPostId(postId);
                                                                notification.setType("comment");

                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("notification")
                                                                        .child(postedBy)
                                                                        .push()
                                                                        .setValue(notification);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            }
                        });

            }
        });

        CommentAdapter adapter = new CommentAdapter(this,list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        commentRv.setLayoutManager(linearLayoutManager);
        commentRv.setAdapter(adapter);

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            list.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}