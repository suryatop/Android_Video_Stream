package com.suryatop.youtube_clone.Adopter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suryatop.youtube_clone.Models.PlayListModel;
import com.suryatop.youtube_clone.R;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    ArrayList<PlayListModel> list;
    OnItemClickListener listener;

    public PlaylistAdapter(Context context, ArrayList<PlayListModel> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_playlist_name, txt_videos_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_playlist_name = itemView.findViewById(R.id.txt_playlist_name);
            txt_videos_count = itemView.findViewById(R.id.txt_video_count);
        }

        public void bind(final PlayListModel model, final OnItemClickListener listener) {
            txt_playlist_name.setText(model.getPlaylist_name());
            txt_videos_count.setText("Videos: " + model.getVideos());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PlayListModel model);
    }
}
