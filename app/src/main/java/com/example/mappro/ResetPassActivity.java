package com.example.mappro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mappro.ui.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {

    private TextView email;
    private Button btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        email=(TextView)findViewById(R.id.reset_email);
        btn=(Button)findViewById(R.id.reset_btn);
        mAuth=FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail=email.getText().toString().trim();
                if(TextUtils.isEmpty(useremail)){
                    Toast.makeText(ResetPassActivity.this, "Please Enter the Email", Toast.LENGTH_SHORT).show();

                }else{
                    mAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPassActivity.this, "Please check your email address", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Settings.class));
                            }else{
                                String message =task.getException().getMessage();
                                Toast.makeText(ResetPassActivity.this,"error Occured:"+message,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}