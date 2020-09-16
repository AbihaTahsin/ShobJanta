package com.example.mappro;
import android.content.Intent;
import android.os.Bundle;
import com.bumptech.glide.Glide;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatBox extends AppCompatActivity {
    private DatabaseReference myDatabase;

    TextView myText = findViewById(R.id.textbox);
@Override
    protected void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chatbox);
    myDatabase = FirebaseDatabase.getInstance().getReference("Message");
    myDatabase.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            myText.setText(dataSnapshot.getValue().toString());

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            myText.setText("CANCELLED");
        }
    });
}
    public void sendMesage (View view)
    {
        EditText myEditText = findViewById(R.id.editText);
        myDatabase.push().setValue(myEditText.getText().toString());
        myEditText.setText("");
        
    }
}
