package com.suryatop.youtube_clone;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener; // Import added here
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleImageView user_profile_image;
    private TextView username, email, txt_your_channel;
    private FirebaseAuth auth;
    private FirebaseUser user;
    String p;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        init();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            getData();
            txt_your_channel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the user has a channel
                    checkUserHaveChannel();
                }
            });
        } else {
            // Handle the case where the user is not authenticated
            Log.e("AccountActivity", "User is null");
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserHaveChannel() {
        reference.child("Channels").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User has a channel
                   Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                    intent.putExtra("type","channel");
                    startActivity(intent);
                } else {
                    // Show dialog to create a new channel
                    showDialogue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(AccountActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogue() {
        Dialog dialog = new Dialog(AccountActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.channel_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        EditText input_channel_name = dialog.findViewById(R.id.input_channel_name);
        EditText input_description = dialog.findViewById(R.id.input_description);
        TextView txt_create_channel = dialog.findViewById(R.id.create_channel);

        txt_create_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = input_channel_name.getText().toString();
                String description = input_description.getText().toString();
                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AccountActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    createNewChannel(name, description, dialog);
                }
            }
        });
        dialog.show();
    }

    private void getData() {
        reference.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String n = snapshot.child("username").getValue(String.class);
                    String e = snapshot.child("email").getValue(String.class);
                    p = snapshot.child("profile").getValue(String.class);

                    if (n != null) {
                        username.setText(n);
                    }
                    if (e != null) {
                        email.setText(e);
                    }

                    // Load profile image using Picasso
                    String profileImageUrl = snapshot.child("profile").getValue(String.class);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get().load(profileImageUrl)
                                .placeholder(R.drawable.profile_user_64572)
                                .into(user_profile_image);
                    } else {
                        // Set default profile image if no profile image is available
                        user_profile_image.setImageResource(R.drawable.profile_user_64572);
                    }
                } else {
                    Log.e("AccountActivity", "Snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AccountActivity", "Database Error: " + error.getMessage());
            }
        });
    }

    private void createNewChannel(String name, String description, Dialog dialog) {
        ProgressDialog progressDialog = new ProgressDialog(AccountActivity.this);
        progressDialog.setTitle("Creating New Channel");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String date = DateFormat.getDateInstance().format(new Date());
        HashMap<String, Object> map = new HashMap<>();
        map.put("Channel_name", name);
        map.put("Description", description);
        map.put("joined", date);
        map.put("uid", user.getUid());
        map.put("channel_logo",p);

        reference.child("Channels").child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this, name + " channel has been created", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this, "Failed to create channel:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        user_profile_image = findViewById(R.id.channel_logo);
        username = findViewById(R.id.user_name);
        email = findViewById(R.id.email);
        txt_your_channel = findViewById(R.id.txt_channel);
    }
}
