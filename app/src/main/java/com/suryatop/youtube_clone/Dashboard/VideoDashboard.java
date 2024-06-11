package com.suryatop.youtube_clone.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.suryatop.youtube_clone.ChannelDashboardFragment;
import com.suryatop.youtube_clone.R;

public class VideoDashboard extends Fragment {


    public VideoDashboard() {
        // Required empty public constructor
    }
    public static VideoDashboard newInstance() {
        VideoDashboard fragment = new VideoDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dashboard_video, container, false);
    }
}