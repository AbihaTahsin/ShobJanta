package com.example.mappro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signInmail,signInpass;
    private Button signInbutton;
    private TextView signUpreg;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.setTitle("Sign In Activity");

        mAuth = FirebaseAuth.getInstance();

        signInmail = findViewById(R.id.signInuserIdText);
        signInpass = findViewById(R.id.signInpassText);
        signInbutton = findViewById(R.id.signInbutton);
        signUpreg = findViewById((R.id.signUpregTextview));
        progressBar = findViewById(R.id.progressbarid);

        signInbutton.setOnClickListener(this);
        signUpreg.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signInbutton:
                userlogin();
                break;

            case R.id.signUpregTextview:
                Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
                break;
        }

    }

    private void userlogin() {
        String email = signInmail.getText().toString().trim();
        String pass = signInpass.getText().toString().trim();
        //checking the validity of the email
        if (email.isEmpty()) {
            signInmail.setError("Enter an email address");
            signInmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signInmail.setError("Enter a valid email address");
            signInmail.requestFocus();
            return;
        }

        //checking the validity of the password
        if (pass.isEmpty()) {
            signInpass.setError("Enter a password");
            signInpass.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            signInpass.setError("Minimum length of a password is 6");
            signInpass.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
                    Toast.makeText(SignInActivity.this, " LogIn Successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    Toast.makeText(SignInActivity.this, " LogIn Unsuccessful!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
