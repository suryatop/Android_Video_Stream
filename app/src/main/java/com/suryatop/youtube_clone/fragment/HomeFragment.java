package com.suryatop.youtube_clone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suryatop.youtube_clone.Adopter.*;
import com.suryatop.youtube_clone.Models.ContentModel;
import com.suryatop.youtube_clone.R;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<ContentModel> list;
    ContentAdopter contentAdapter;

    DatabaseReference reference;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        reference = FirebaseDatabase.getInstance().getReference().child("Videos");

        getAllVideos();

        return view;
    }

    private void getAllVideos() {
        list = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded()) { // Check if fragment is attached to activity
                    if (snapshot.exists()) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ContentModel model = dataSnapshot.getValue(ContentModel.class);
                            list.add(model);
                        }

                        Collections.shuffle(list);
                        contentAdapter = new ContentAdopter(getActivity(), list);
                        recyclerView.setAdapter(contentAdapter);
                        contentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) { // Check if fragment is attached to activity
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
