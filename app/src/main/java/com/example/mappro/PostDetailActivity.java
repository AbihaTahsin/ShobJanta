package com.example.mappro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mappro.CommentAdapter;
import com.example.mappro.Comment;
import com.bumptech.glide.Glide;
import com.example.mappro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity<CommentAdapter> extends AppCompatActivity {

    public static final String EXTRA_TEXT = "com.example.mappro.EXTRA_TEXT";

    ImageView imgPost,imgUserPost,imgCurrentUser;
    TextView txtPostDesc,txtPostDateName,txtPostLoc;
    EditText editTextComment;
    Button btnAddComment,btnOpenMap;
    String PostKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "Comment" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        RvComment = findViewById(R.id.rv_comment);
        imgPost =findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detail_user_img);
        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);

        txtPostLoc = findViewById(R.id.post_detail_loc);
        txtPostDesc = findViewById(R.id.post_detail_desc);
        txtPostDateName = findViewById(R.id.post_detail_date_name);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);
        btnOpenMap = findViewById(R.id.open_map);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // add Comment button click listner

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();
                Comment comment = new Comment(comment_content,uid,uimg,uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("comment added");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("fail to add comment : "+e.getMessage());
                    }
                });



            }
        });

        String postImage = getIntent().getExtras().getString("postImage") ;
        Glide.with(this).load(postImage).into(imgPost);

        final String postLoc = getIntent().getExtras().getString("location");
        txtPostLoc.setText(postLoc);

        String userpostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userpostImage).into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);

        PostKey = getIntent().getExtras().getString("postKey");

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);
        // ini Recyclerview Comment
        iniRvComment();

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(PostDetailActivity.this, LocationActivity.class);
                intent3.putExtra(EXTRA_TEXT,postLoc);
                startActivity(intent3);
            }
        });

    }
    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment) ;

                }

                commentAdapter = (CommentAdapter) new com.example.mappro.CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter((RecyclerView.Adapter) commentAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void showMessage(String message) {

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }


    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MMM-yyyy",calendar).toString();
        return date;
    }
}
