package com.androiod.social_media_app.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androiod.social_media_app.Adapter.NotificationAdapter;
import com.androiod.social_media_app.Adapter.ViewPagerAdapter;
import com.androiod.social_media_app.Model.Notification;
import com.androiod.social_media_app.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    ShimmerRecyclerView recyclerView;
    ArrayList<Notification> list;
    FirebaseDatabase database;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.notification2Rv);
        recyclerView.showShimmerAdapter();
        list = new ArrayList<>();
//        list.add(new Notification(R.drawable.mp,"<b>mp</b> commented on your post : Nice","Just now"));
//        list.add(new Notification(R.drawable.mp,"<b>mp</b> liked your post","20 min ago"));
//        list.add(new Notification(R.drawable.mp,"<b>taran</b> commented on your post","1 hour ago"));
//        list.add(new Notification(R.drawable.mp,"<b>taran</b> liked your post","2 hour ago"));
//        list.add(new Notification(R.drawable.mp,"<b>mp</b> commented on your post","Just now"));

        NotificationAdapter adapter = new NotificationAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);

        recyclerView.setLayoutManager(linearLayoutManager);

        database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            notification.setNotificationId(dataSnapshot.getKey());
                            list.add(notification);
                        }
                        recyclerView.setAdapter(adapter);
                        recyclerView.hideShimmerAdapter();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        return view;
    }
}