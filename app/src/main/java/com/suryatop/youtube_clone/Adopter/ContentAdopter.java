package com.suryatop.youtube_clone.Adopter;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.suryatop.youtube_clone.Models.ContentModel;
import com.suryatop.youtube_clone.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentAdopter extends RecyclerView.Adapter<ContentAdopter.ViewHolder> {
    Context context;
    ArrayList<ContentModel> list;
    DatabaseReference reference;

    public ContentAdopter(Context context, ArrayList<ContentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentModel contentModel = list.get(position);
        if(contentModel != null){
            //thumbnail
            Glide.with(context).asBitmap().load(contentModel.getVideoUrl()).into(holder.thumbnail);
            holder.videoTitle.setText(contentModel.getVideoTitle());
            holder.views.setText(contentModel.getViews()+"Views");
            holder.date.setText(contentModel.getDate());

            setData(contentModel.getPublisher(), holder.channel_logo, holder.channel_name);

        }
    }

    private void setData(String publisher, CircleImageView logo, TextView channel_name){
            reference = FirebaseDatabase.getInstance().getReference().child("Channels");
            reference.child(publisher).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String cName = snapshot.child("Channel_name").getValue(String.class);
                        String clogo = snapshot.child("Channel_logo").getValue(String.class);
                            channel_name.setText(cName);
                            Picasso.get().load(clogo).placeholder(R.drawable.profile_user_64572).into(logo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView videoTitle, channel_name, views, date;
        CircleImageView channel_logo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoTitle = itemView.findViewById(R.id.VideoTitle);
            channel_name = itemView.findViewById(R.id.channel_name);
            views = itemView.findViewById(R.id.views);
            channel_logo = itemView.findViewById(R.id.channel_logo);
            date = itemView.findViewById(R.id.date);
        }
    }
}
