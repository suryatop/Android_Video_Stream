package com.suryatop.youtube_clone;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.suryatop.youtube_clone.Adopter.PlaylistAdapter;
import com.suryatop.youtube_clone.Models.PlayListModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PublishContentActivity extends AppCompatActivity {

    EditText inputVideoTitle, inputVideoDescription;
    LinearLayout progressLayout;
    ProgressBar progressBar;
    TextView progressText;

    VideoView videoView;
    Uri videoUri;
    MediaController mediaController;
    Dialog dialog;
    TextView txtChoosePlaylist;
    TextView txtUpload;

    FirebaseUser user;
    DatabaseReference videoReference;
    StorageReference storageReference;

    String selectedPlaylist;
    int videoCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_content);
        txtUpload = findViewById(R.id.txtUpload);
        txtChoosePlaylist = findViewById(R.id.chose_playlist);
        videoView = findViewById(R.id.video_view);
        inputVideoTitle = findViewById(R.id.input_video_title);
        inputVideoDescription = findViewById(R.id.input_video_description);
        progressLayout = findViewById(R.id.progressLyt);
        progressText = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progressBar);

        mediaController = new MediaController(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        videoReference = FirebaseDatabase.getInstance().getReference().child("Videos");
        storageReference = FirebaseStorage.getInstance().getReference().child("Videos");

        Intent intent = getIntent();
        if (intent != null) {
            videoUri = intent.getData();
            if (videoUri != null) {
                videoView.setVideoURI(videoUri);
                videoView.setMediaController(mediaController);
                videoView.start();
            } else {
                Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
            }
        }

        txtChoosePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlaylistDialog();
            }
        });

        txtUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = inputVideoTitle.getText().toString();
                String description = inputVideoDescription.getText().toString();


                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(PublishContentActivity.this, "Fill all fields...", Toast.LENGTH_SHORT).show();
                } else if (txtChoosePlaylist.getText().toString().equals("Choose Playlist")) {
                    Toast.makeText(PublishContentActivity.this, "Please select playlist", Toast.LENGTH_SHORT).show();
                } else {
                    // Call the method to upload videos
                    uploadVideoToStorage(title, description);
                }
            }
        });


    }

    private void uploadVideoToStorage(String title, String description) {
        progressLayout.setVisibility(View.VISIBLE);
        final StorageReference videoRef = storageReference.child(user.getUid())
                .child(System.currentTimeMillis() + ".mp4");

        UploadTask uploadTask = videoRef.putFile(videoUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String videoUrl = uri.toString();
                        saveVideoDataToFirebase(title, description, videoUrl);
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressLayout.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(PublishContentActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PublishContentActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else {
                    Toast.makeText(PublishContentActivity.this, "Failed to upload: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Add progress listener
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
                progressText.setText((int) progress + "%");
            }
        });
    }

    private void saveVideoDataToFirebase(String title, String description, String videoUrl) {
        String currentDate = DateFormat.getDateInstance().format(new Date());
        String videoId = videoReference.push().getKey();

        HashMap<String, Object> videoMap = new HashMap<>();
        videoMap.put("videoId", videoId);
        videoMap.put("title", title);
        videoMap.put("description", description);
        videoMap.put("playlist", selectedPlaylist);
        videoMap.put("videoUrl", videoUrl);
        videoMap.put("publisher", user.getUid());
        videoMap.put("type","video");
        videoMap.put("views",0);
        videoMap.put("date", currentDate);

        videoReference.child(videoId).setValue(videoMap);
    }

    private void showPlaylistDialog() {
        dialog = new Dialog(PublishContentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.play_list_dialog);
        dialog.setCancelable(true);


        EditText input_playlist_name = dialog.findViewById(R.id.input_playlist_name);
        TextView txt_add = dialog.findViewById(R.id.txt_add);

        ArrayList<PlayListModel> list = new ArrayList<>();
        PlaylistAdapter adapter;
        RecyclerView recyclerView = dialog.findViewById(R.id.RelativeView);
        recyclerView.setHasFixedSize(true);


        adapter = new PlaylistAdapter(PublishContentActivity.this, list, new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PlayListModel model) {
                dialog.dismiss();
                txtChoosePlaylist.setText("Playlist :"+model.getPlaylist_name());
            }
        });
        checkUserAlreadyHavePlaylist(recyclerView);
        recyclerView.setAdapter(adapter);

        showAllPlaylists(adapter,list);
        txt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = input_playlist_name.getText().toString();
                if (value.isEmpty()) {
                    Toast.makeText(PublishContentActivity.this, "Enter Playlist Name", Toast.LENGTH_SHORT).show();
                } else {
                    createNewPlaylist(value);
                }
            }
        });

        dialog.show();
    }

    private void showAllPlaylists(PlaylistAdapter adapter, ArrayList<PlayListModel> list) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlists");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Clear the existing list before adding new data
                    list.clear();
                    // Iterate through the dataSnapshot to get each playlist
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        // Get the playlist model from the dataSnapshot
                        PlayListModel playlistModel = dataSnapshot.getValue(PlayListModel.class);
                        // Add the playlist model to the list
                        list.add(playlistModel);
                    }
                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PublishContentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewPlaylist(String value) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlists");
        HashMap<String, Object> map = new HashMap<>();
        map.put("playlist_name", value);
        map.put("videos", 0);
        map.put("uid", user.getUid());

        reference.child(user.getUid()).child(value).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(PublishContentActivity.this, "New Playlist Created!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PublishContentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }
    private void checkUserAlreadyHavePlaylist(RecyclerView recyclerView) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlists");

        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(PublishContentActivity.this, "User has playlists", Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PublishContentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
