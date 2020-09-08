package com.example.mappro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUpbutton;
    private EditText signUpmail, signUppass;
    private TextView signInreg;
    private FirebaseAuth mAuth;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.setTitle("Sign Up Activity");
        mAuth = FirebaseAuth.getInstance();


        progressbar= findViewById(R.id.progressbarid);
        signUpmail = findViewById(R.id.signUpuserIdText);
        signUppass = findViewById(R.id.signUppassText);
        signUpbutton = findViewById(R.id.signUpbutton);
        signInreg = findViewById((R.id.signInregTextview));

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
        String email =signUpmail.getText().toString().trim();
        String pass = signUppass.getText().toString().trim();
        //checking the validity of the email
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
}
