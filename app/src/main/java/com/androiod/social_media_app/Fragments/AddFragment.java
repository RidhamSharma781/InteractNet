package com.androiod.social_media_app.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiod.social_media_app.Model.Post;
import com.androiod.social_media_app.Model.UserModel;
import com.androiod.social_media_app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddFragment extends Fragment {

    Button postBtn;
    EditText postDrescp;
    ImageView addPost,postImg;
    TextView name,profession;
    CircleImageView profileImg;
    Uri uri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    public AddFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        postBtn = view.findViewById(R.id.postBtn);
        postDrescp = view.findViewById(R.id.postDescrp);
        addPost = view.findViewById(R.id.addPost);
        postImg = view.findViewById(R.id.postImg);
        name = view.findViewById(R.id.username);
        profession = view.findViewById(R.id.about);
        profileImg = view.findViewById(R.id.profilePhoto);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        database.getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            UserModel user = snapshot.getValue(UserModel.class);
                            Picasso.get()
                                    .load(user.getProfilePhoto())
                                    .placeholder(R.drawable.placeholder)
                                    .into(profileImg);
                            name.setText(user.getUsername());
                            profession.setText(user.getProfession());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        postDrescp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String caption = postDrescp.getText().toString();
                if(!caption.isEmpty()){
                    postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.follow_btn));
                    postBtn.setTextColor(getResources().getColor(R.color.white));
                    postBtn.setEnabled(true);
                }else{
                    postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.following_btn));
                    postBtn.setTextColor(getResources().getColor(R.color.black));
                    postBtn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);

            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                final StorageReference reference = storage.getReference().child("posts")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime()+"");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Post post = new Post();
                                post.setPostedBy(FirebaseAuth.getInstance().getUid());
                                post.setPostImg(uri.toString());
                                post.setPostDescription(postDrescp.getText().toString());
                                post.setPostedAt(String.valueOf(new Date().getTime()));

                                database.getReference().child("posts")
                                        .push()
                                        .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference().child("Users")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                UserModel user = snapshot.getValue(UserModel.class);
                                                                database.getReference().child("Users")
                                                                        .child(FirebaseAuth.getInstance().getUid())
                                                                        .child("postCount")
                                                                        .setValue(user.getPostCount()+1);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toast.makeText(getContext(),"Posted Successfully" , Toast.LENGTH_SHORT).show();
                                                postDrescp.setText("");
                                                postImg.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });

        return  view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData() != null){
            uri = data.getData();
            postImg.setImageURI(uri);
            postImg.setVisibility(View.VISIBLE);

            postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.follow_btn));
            postBtn.setTextColor(getResources().getColor(R.color.white));
            postBtn.setEnabled(true);
        }
    }
}