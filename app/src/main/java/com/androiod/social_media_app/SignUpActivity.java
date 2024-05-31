package com.androiod.social_media_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.androiod.social_media_app.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    TextView goToLogin;
    Button signUp;
    TextInputEditText username,email,pass,confirmPass;
    FirebaseAuth auth;
    FirebaseDatabase database;
    LottieAnimationView lottie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().setStatusBarColor(ContextCompat.getColor(SignUpActivity.this,R.color.purple));
        goToLogin = (TextView) findViewById(R.id.singUP);
        signUp = findViewById(R.id.loginBtn);
        username = findViewById(R.id.usernameEt);
        email = findViewById(R.id.emailEt);
        pass = findViewById(R.id.passEt);
        confirmPass = findViewById(R.id.confirmPassEt);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        lottie = findViewById(R.id.lottie);
//        lottie.animate().translationX(2000).setDuration(2000).setStartDelay(2900);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(SignUpActivity.this,loginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createUser() {
        String emailDb = email.getText().toString();
        String usernameDb = username.getText().toString();
        String passDb = pass.getText().toString();
        String confirmPassDb = confirmPass.getText().toString();
        

        auth.createUserWithEmailAndPassword(emailDb,passDb).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    UserModel user = new UserModel(usernameDb,emailDb,passDb,confirmPassDb);
                    String id = task.getResult().getUser().getUid();
                    database.getReference().child("Users").child(id).setValue(user);
                    Toast.makeText(SignUpActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}