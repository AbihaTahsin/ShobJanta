package com.example.mappro;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.example.mappro.ui.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Home extends AppCompatActivity  {

    private static  int PReqCode = 2 ;
    private static  int REQUESCODE = 2 ;
    private AppBarConfiguration mAppBarConfiguration;
    Dialog popAddpost;
    ImageView popupUserImage,popupPostImage,popupAddBtn;
    TextView popupTitle,popupDescription;
    Spinner popupSpinner;
    ProgressBar popupClickProgress;
     Uri pickedImgUri=null;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        //initialize popup
        iniPopup();

        setupPopupImageClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddpost.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        updateNavHeader();


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_map, R.id.nav_posts,R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(Home.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            // everything goes well : we have permission to access user gallery
            openGallery();

    }

    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popupPostImage.setImageURI(pickedImgUri);

        }


    }
    private void iniPopup() {

        popAddpost = new Dialog(this);
        popAddpost.setContentView(R.layout.popup_add_post);
        popAddpost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddpost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddpost.getWindow().getAttributes().gravity = Gravity.TOP;

        // load Current user profile photo
        //Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupUserImage);

        // ini popup widgets
        popupUserImage = popAddpost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddpost.findViewById(R.id.popup_img);
        popupTitle = popAddpost.findViewById(R.id.popup_title);
        popupDescription = popAddpost.findViewById(R.id.popup_description);
        popupAddBtn = popAddpost.findViewById(R.id.popup_add);
        popupClickProgress = popAddpost.findViewById(R.id.popup_progressBar);


        //App post click listener

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                if(!popupTitle.getText().toString().isEmpty()&&
                !popupDescription.getText().toString().isEmpty()
                && pickedImgUri!=null)
                {
                    // every input segment is filled
                    //TODO create post object and post it to firebase database
                    //1st need to upload post image
                    // access firebase storage

                }
            }
        });

    }





    private void showMessage(String message) {
        Toast.makeText(Home.this,message,Toast.LENGTH_LONG).show();
    }

    private void setupPopupImageClick() {


        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here when image clicked we need to open the gallery
                // before we open the gallery we need to check if our app have the access to user files
                // we did this before in register activity I'm just going to copy the code to save time ...

                openGallery();
                checkAndRequestForPermission();


            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mAuth = FirebaseAuth.getInstance();
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;*/

        getMenuInflater().inflate(R.menu.navigation_drawer,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.signout)
        {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavHeader()
    {
        NavigationView navigationView=(NavigationView) findViewById(R.id.nav_view);
        View headerView= navigationView.getHeaderView(0);
        TextView navUsername= headerView.findViewById(R.id.nav_usename);
        TextView navUsermail= headerView.findViewById(R.id.nav_user_mail);
       ImageView navUserPhoto= headerView.findViewById(R.id.nav_user_photo);

        navUsermail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());
        //now we will use Glide to load image
        //1st we need to import library

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
    }

}
