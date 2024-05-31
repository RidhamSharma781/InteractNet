package com.androiod.social_media_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {

    TextView goToSignUp;
    TextInputEditText email,password;
    Button login;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        goToSignUp = findViewById(R.id.singUP);
        email = findViewById(R.id.emailET);
        password = findViewById(R.id.passET);
        login = findViewById(R.id.loginBtn);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(loginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null){
            Intent intent = new Intent(loginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    private void loginUser() {
        String emailDb = email.getText().toString();
        String passDb = password.getText().toString();
        auth.signInWithEmailAndPassword(emailDb,passDb).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(loginActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(loginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}