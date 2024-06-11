package com.suryatop.youtube_clone;

import java.util.HashMap;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.suryatop.youtube_clone.fragment.HomeFragment;
import com.suryatop.youtube_clone.fragment.SettingFragment;
import com.suryatop.youtube_clone.fragment.ShortsFragment;
import com.suryatop.youtube_clone.fragment.SubscriptionFragment;



public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    AppBarLayout appBarLayout;
    Fragment fragment;
    ImageView user_profile_image;

    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private static final int PERMISSION = 101;
    private static final int PICK_VIDEO = 102;

    FirebaseAuth auth;
    FirebaseUser user;
    Uri videoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        bottomNavigationView = findViewById(R.id.bottomNavi);
        frameLayout = findViewById(R.id.frame_layout);
        appBarLayout = findViewById(R.id.AppBar);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize your ImageView
        user_profile_image = findViewById(R.id.channel_logo);

        checkPermission();
        getProfileImage();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    selectedFragment(new HomeFragment());
                    return true;
                } else if (itemId == R.id.shorts) {
                    selectedFragment(new ShortsFragment());
                    return true;
                } else if (itemId == R.id.publish) {


                    //publish
                    showPublishContentDialogue();


                    return true;
                } else if (itemId == R.id.subscription) {
                    selectedFragment(new SubscriptionFragment());
                    return true;
                } else if (itemId == R.id.setting) {
                    selectedFragment(new SettingFragment());
                    return true;
                }
                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.home);

        user_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                    getProfileImage();
                } else {
                    user_profile_image.setImageResource(R.drawable.profile_user_64572);
                    showDialogue();
                }
            }
        });
        showFragment();
    }


    private void showPublishContentDialogue() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.uplodedialogue);
        dialog.setCanceledOnTouchOutside(true);

        //textview
        TextView txt_upload_video = dialog.findViewById(R.id.txt_upload_video);
        TextView txt_make_post = dialog.findViewById(R.id.txt_upload_post);
        TextView txt_make_poll = dialog.findViewById(R.id.txt_upload_poll);


        //uplode video
        txt_upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*"); // Corrected the syntax
                startActivityForResult(Intent.createChooser(intent, "Select video"), PICK_VIDEO);

                // Logging the action
                Log.d("VideoPicker", "onClick: Video selection initiated");
            }
        });

        dialog.show();
    }


    private void showDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.signing_page, viewGroup, false);
        builder.setView(view);

        TextView txt_google_signin = view.findViewById(R.id.txt_google_signIn);
        txt_google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        builder.create().show();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK && data != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("username", account.getDisplayName());
                                    map.put("email", account.getEmail());
                                    map.put("profile", String.valueOf(account.getPhotoUrl()));
                                    map.put("uid", firebaseUser.getUid());
                                    map.put("search", account.getDisplayName().toLowerCase());

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                                    reference.child(firebaseUser.getUid()).setValue(map);

                                } else {
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (ApiException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            case PICK_VIDEO:
                if(resultCode == RESULT_OK && data != null){
                    videoUri = data.getData();
                    Intent intent = new Intent(MainActivity.this, PublishContentActivity.class);
                    intent.putExtra("type", "video");
                    intent.setData(videoUri);
                    startActivity(intent);
                }
        }

    }

    private void selectedFragment(Fragment fragment) {
        appBarLayout.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.notification) {
            Toast.makeText(this, "Notification", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.search) {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return false;
    }

    // Fetch profile picture
    private void getProfileImage() {
        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
            reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String profileUrl = snapshot.child("profile").getValue(String.class);
                        if (profileUrl != null && !profileUrl.isEmpty()) {
                            Picasso.get().load(profileUrl)
                                    .placeholder(R.drawable.profile_user_64572)
                                    .error(R.drawable.profile_user_64572)
                                    .into(user_profile_image);
                        } else {
                            // If profile URL is empty or null, set default profile image
                            user_profile_image.setImageResource(R.drawable.profile_user_64572);
                        }
                    } else {
                        // If user profile data doesn't exist, set default profile image
                        user_profile_image.setImageResource(R.drawable.profile_user_64572);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    Toast.makeText(MainActivity.this, "Error fetching profile image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where user is null (e.g., user not logged in)
            // For example, set a default profile image
            user_profile_image.setImageResource(R.drawable.profile_user_64572);
        }
    }


    private void showFragment() {
        String type = getIntent().getStringExtra("type");
        if (type != null) {
            switch (type) {
                case "channel":
                    setStatusBarColor("#FF0000");
                    if (appBarLayout != null) {
                        appBarLayout.setVisibility(View.GONE);
                    }
                    fragment = ChannelDashboardFragment.newInstance();
                    break;
                // Add other cases if needed
                default:
                    fragment = new HomeFragment(); // Default fragment if no type matches
                    break;
            }
        } else {
            fragment = new HomeFragment(); // Default fragment if no type is provided
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
        } else {
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void setStatusBarColor(String color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(color));
    }


    private void checkPermission() {
        // Check if the WRITE_EXTERNAL_STORAGE permission is not granted
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
        } else {
            Log.d("tag", "checkPermission: Permission granted");
        }
    }
}
