package com.suryatop.youtube_clone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suryatop.youtube_clone.Adopter.ViewPagerAdopter;
import com.suryatop.youtube_clone.Dashboard.AboutDashboard;
import com.suryatop.youtube_clone.Dashboard.HomeDashboard;
import com.suryatop.youtube_clone.Dashboard.PlaylistDashboard;
import com.suryatop.youtube_clone.Dashboard.SubscriptionDashboard;
import com.suryatop.youtube_clone.Dashboard.VideoDashboard;

public class ChannelDashboardFragment extends Fragment {

    TextView user_channal_name;

    ViewPagerAdopter adopter;
    ViewPager viewPager;
    TabLayout tabLayout;

    public ChannelDashboardFragment() {
        // Required empty public constructor
    }
    public static ChannelDashboardFragment newInstance() {
        ChannelDashboardFragment fragment = new ChannelDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel_dashboard, container, false);
        user_channal_name = view.findViewById(R.id.user_channel_name);
        tabLayout = view.findViewById(R.id.tab);
        viewPager = view.findViewById(R.id.viewPage);

        getInitedAdopter();

        // Check if the user is authenticated
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Channels");
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String name = "";
                        DataSnapshot channelNameSnapshot = snapshot.child("Channel_name");
                        if(channelNameSnapshot.exists() && channelNameSnapshot.getValue() != null) {
                            name = channelNameSnapshot.getValue().toString();
                        }
                        user_channal_name.setText(name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        return view;
    }

    private void getInitedAdopter() {
        adopter = new ViewPagerAdopter(getChildFragmentManager());
        adopter.add(new HomeDashboard(),"Home");
        adopter.add(new VideoDashboard(),"Videos");
        adopter.add(new PlaylistDashboard(),"Playlist");
        adopter.add(new SubscriptionDashboard(),"Subscription");
        adopter.add(new AboutDashboard(),"About");

        viewPager.setAdapter(adopter);
        tabLayout.setupWithViewPager(viewPager);
    }

}