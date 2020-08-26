package android.example.firebaseapp.fragments;

import android.example.firebaseapp.model.Post;
import android.example.firebaseapp.model.User;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.example.firebaseapp.R;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private ImageView imgOptions;
    private TextView txtNumberOfFollowers;
    private TextView txtNumberOfPersonsFollowing;
    private TextView txtNumberOfPosts;
    private TextView txtUsername;
    private TextView txtFullName;
    private TextView txtBio;

    private ImageButton imgMyPictures;
    private ImageButton imgSavedPictures;

    private FirebaseUser firebaseUser;

    private String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileId = firebaseUser.getUid();

        profileImage = view.findViewById(R.id.profile_image);
        imgOptions = view.findViewById(R.id.options);
        txtNumberOfFollowers = view.findViewById(R.id.number_of_followers);
        txtNumberOfPersonsFollowing = view.findViewById(R.id.number_of_persons_following);
        txtNumberOfPosts = view.findViewById(R.id.number_of_posts);
        txtUsername = view.findViewById(R.id.username);
        txtFullName = view.findViewById(R.id.fullName);
        txtBio = view.findViewById(R.id.bio);
        imgMyPictures = view.findViewById(R.id.my_pictures);
        imgSavedPictures = view.findViewById(R.id.saved_pictures);


        getUserInfo();
        countFollowersAndPersonsFollowing();
        countNumberOfPosts();


        return view;
    }

    private void getUserInfo() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Picasso.get().load(user.getImageUrl()).into(profileImage);

                txtUsername.setText(user.getUsername());
                txtFullName.setText(user.getName());
                txtBio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countFollowersAndPersonsFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtNumberOfFollowers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtNumberOfPersonsFollowing.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void countNumberOfPosts() {
        FirebaseDatabase.getInstance().getReference()
                .child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        counter++;
                    }
                }
                txtNumberOfPosts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}