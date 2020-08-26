package android.example.firebaseapp;

import android.content.Intent;
import android.example.firebaseapp.adapter.CommentAdapter;
import android.example.firebaseapp.model.Comment;
import android.example.firebaseapp.model.User;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class CommentActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText etComment;
    private TextView txtPost;

    private String postId;
    private String authorId;

    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<Comment> comments;


    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");

        // goes back for a single level(page) in UI
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rvComments = findViewById(R.id.recycler_view_comments);
        rvComments.setHasFixedSize(true);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, comments);

        rvComments.setAdapter(commentAdapter);

        etComment = findViewById(R.id.add_comment);
        profileImage = findViewById(R.id.profile_image);
        txtPost = findViewById(R.id.post);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();

        txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etComment.getText().toString())) {
                    makeText(CommentActivity.this, "No comment added!", LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        getComments();

    }

    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageUrl().equals("default")) {
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImageUrl()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getComments() {
        FirebaseDatabase.getInstance().getReference()
                .child("Comments")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for(DataSnapshot snap: snapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    comments.add(comment);
                }

                commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addComment() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", etComment.getText().toString());
        map.put("publisher", firebaseUser.getUid());

        FirebaseDatabase.getInstance().getReference()
                .child("Comments")
                .child(postId).push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    makeText(CommentActivity.this, "Comment added!", LENGTH_SHORT).show();

                } else {
                    makeText(CommentActivity.this, task.getException().getMessage(), LENGTH_SHORT).show();

                }
            }
        });
    }

}