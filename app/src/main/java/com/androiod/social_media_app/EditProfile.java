package com.androiod.social_media_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androiod.social_media_app.Model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    CircleImageView profileImg;
    EditText name,profession,about;
    ImageView back;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setStatusBarColor(ContextCompat.getColor(EditProfile.this,R.color.green));

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        profileImg = findViewById(R.id.profileImg);
        name = findViewById(R.id.usernameEt);
        profession = findViewById(R.id.professionEt);
        about = findViewById(R.id.aboutEt);
        save = findViewById(R.id.save);
        back = findViewById(R.id.backEdit);

        database.getReference()
                .child("Users")
                .child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfilePhoto())
                                .placeholder(R.drawable.placeholder)
                                .into(profileImg);
                        name.setText(user.getUsername());
                        profession.setText(user.getProfession());
                        about.setText(user.getAbout());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> map = new HashMap<>();
                map.put("username",name.getText().toString());
                map.put("about",about.getText().toString());
                map.put("profession",profession.getText().toString());
                database.getReference()
                        .child("Users")
                        .child(auth.getUid())
                        .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditProfile.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}