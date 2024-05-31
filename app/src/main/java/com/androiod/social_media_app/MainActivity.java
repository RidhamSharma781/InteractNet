package com.androiod.social_media_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androiod.social_media_app.Fragments.AddFragment;
import com.androiod.social_media_app.Fragments.HomeFragment;
import com.androiod.social_media_app.Fragments.NotificationFragment;
import com.androiod.social_media_app.Fragments.ProfileFragment;
import com.androiod.social_media_app.Fragments.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {

    ReadableBottomBar bottomBar;
    Toolbar t1;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1 = findViewById(R.id.toolbar);
        setSupportActionBar(t1);
        t1.setVisibility(View.GONE);
        MainActivity.this.setTitle("My Profile");

        auth = FirebaseAuth.getInstance();


        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.green));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeFragment());
        transaction.commit();


        bottomBar = (ReadableBottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i){
                    case 0 : transaction.replace(R.id.container, new HomeFragment());
                        t1.setVisibility(View.GONE);
                        break;
                    case 1 : transaction.replace(R.id.container, new SearchFragment());
                        t1.setVisibility(View.GONE);
                        break;
                    case 2 : transaction.replace(R.id.container, new AddFragment());
                        t1.setVisibility(View.GONE);
                        break;
                    case 3 : transaction.replace(R.id.container, new NotificationFragment());
                        t1.setVisibility(View.GONE);
                        break;
                    case 4 : transaction.replace(R.id.container, new ProfileFragment());
                        t1.setVisibility(View.VISIBLE);
                        break;
                }
                transaction.commit();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_settings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int logout = R.id.logout;
        int editProfile = R.id.editProfile;
        int itemId = item.getItemId();

        if(itemId==logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Are you sure?")
                            .setMessage("This will log you out of this account")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            auth.signOut();
                                            Intent intent = new Intent(MainActivity.this,loginActivity.class);
                                            startActivity(intent);
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

        }else{
            Intent intent = new Intent(MainActivity.this,EditProfile.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }
}