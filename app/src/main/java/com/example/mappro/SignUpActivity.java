package com.example.mappro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    static  int PReqCode = 1 ;
    static int REQUESCODE = 1 ;
    private Button signUpbutton;
    private EditText signUpmail, signUppass,userName;
    private TextView signInreg;
    private FirebaseAuth mAuth;
    private ProgressBar progressbar;
     ImageView userdp;
     Uri pickedImgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.setTitle("Sign Up Activity");
        mAuth = FirebaseAuth.getInstance();

        userdp=findViewById(R.id.Userdp);
        progressbar= findViewById(R.id.progressbarid);
        signUpmail = findViewById(R.id.signUpuserIdText);
        signUppass = findViewById(R.id.signUppassText);
        userName = findViewById(R.id.Username);
        signUpbutton = findViewById(R.id.signUpbutton);
        signInreg = findViewById((R.id.signInregTextview));

        userdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
                openGallery();

            }
        });

        signUpbutton.setOnClickListener(this);
        signInreg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpbutton:
                userregister();

                break;
            case R.id.signInregTextview:
                Intent signUpIntent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(signUpIntent);
                break;
        }
    }

    private void userregister() {
        final String email =signUpmail.getText().toString().trim();
        String pass = signUppass.getText().toString().trim();
        final String name = userName.getText().toString().trim();
        //checking the validity of the email
        if (name.isEmpty()) {
            userName.setError("Enter User Name");
            userName.requestFocus();
            return;
        }
        if(email.isEmpty())
        {
            signUpmail.setError("Enter an email address");
            signUpmail.requestFocus();
            return;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            signUpmail.setError("Enter a valid email address");
            signUpmail.requestFocus();
            return;
        }

        //checking the validity of the password
        if(pass.isEmpty())
        {
            signUppass.setError("Enter a password");
            signUppass.requestFocus();
            return;
        }
        if (pass.length()<6)
        {
            signUppass.setError("Minimum length of a password is 6");
            signUppass.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressbar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    finish();
                    Toast.makeText(SignUpActivity.this,"Registration Is Successful",Toast.LENGTH_LONG).show();
                   updateUserInfo(name,pickedImgUri,mAuth.getCurrentUser());
                    Intent intent = new Intent(SignUpActivity.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    // If sign in fails, display a message to the user.
                    if(task.getException() instanceof FirebaseAuthUserCollisionException )
                    {
                        Toast.makeText(SignUpActivity.this," User Is Already Registered!",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(SignUpActivity.this,"Error ! "+task.getException(). getMessage(),Toast.LENGTH_LONG).show();
                    }

                }


            }
        });
    }

  private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        //first to upload yser photo to firebase storsage
        StorageReference mStorage= FirebaseStorage.getInstance().getReference().child("user photo");
        final StorageReference imageFilePath= mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image uploaded successfully
                //we can get our image url
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate= new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();

                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this," Sign up Complete",Toast.LENGTH_LONG).show();
                                        updateUI();
                                }
                            }
                        });



                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent Home = new Intent(getApplicationContext(), Home.class);
        startActivity(Home);
        finish();
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !
      //  userdp.setVisibility(View.INVISIBLE);
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }
    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(SignUpActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(SignUpActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            // everything goes well : we have permission to access user gallery
            openGallery();

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            userdp.setImageURI(pickedImgUri);

        }


    }


}
