package com.example.mappro;

import androidx.appcompat.app.AppCompatActivity;
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

import com.bumptech.glide.Glide;
import com.example.mappro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

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
    //RecyclerView RvComment;
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

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(PostDetailActivity.this, LocationActivity.class);
                intent3.putExtra(EXTRA_TEXT,postLoc);
                startActivity(intent3);
            }
        });

    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MMM-yyyy",calendar).toString();
        return date;
    }
}
