package android.example.firebaseapp.fragments;

import android.content.Context;
import android.example.firebaseapp.R;
import android.example.firebaseapp.adapter.PhotoAdapter;
import android.example.firebaseapp.model.Post;
import android.example.firebaseapp.model.User;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private Button btnEditProfile;

    private ImageButton imgMyPictures;
    private ImageButton imgSavedPictures;

    private RecyclerView rvPhotos;
    private PhotoAdapter photoAdapter;
    private List<Post> photosList;

    private RecyclerView rvSaves;
    private PhotoAdapter postAdapterSaves;
    private List<Post> savedPostsList;

    private FirebaseUser firebaseUser;

    private String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("SharedPreferencesProfile", Context.MODE_PRIVATE).getString("profileId", "none");

        if (data.equals("none")) {
            profileId = firebaseUser.getUid();

        } else {
            profileId = data;
        }


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
        btnEditProfile = view.findViewById(R.id.edit_profile);

        rvPhotos = view.findViewById(R.id.recycler_view_pictures);
        rvPhotos.setHasFixedSize(true);
        rvPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));

        photosList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), photosList);
        rvPhotos.setAdapter(photoAdapter);

        rvSaves = view.findViewById(R.id.recycler_view_saved);
        rvSaves.setHasFixedSize(true);
        rvSaves.setLayoutManager(new GridLayoutManager(getContext(), 3));

        savedPostsList = new ArrayList<>();
        postAdapterSaves = new PhotoAdapter(getContext(), savedPostsList);
        rvSaves.setAdapter(postAdapterSaves);

        getUserInfo();
        countFollowersAndPersonsFollowing();
        countNumberOfPosts();
        getPhotosForUser();
        getSavedPosts();

        if (profileId.equals(firebaseUser.getUid())) {
            btnEditProfile.setVisibility(View.VISIBLE);
        } else {
            checkFollowingStatus();
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = btnEditProfile.getText().toString();

                if (text.equals("Edit profile")) {
                    // TODO edit activity
                } else {
                    if (text.equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("following").child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profileId).child("followers").child(firebaseUser.getUid()).setValue(true);
                    } else if (text.equals("Following")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("following").child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profileId).child("followers").child(firebaseUser.getUid()).removeValue();
                    }
                }
            }
        });

        rvPhotos.setVisibility(View.VISIBLE);
        rvSaves.setVisibility(View.GONE);

        imgMyPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvPhotos.setVisibility(View.VISIBLE);
                rvSaves.setVisibility(View.GONE);
            }
        });

        imgSavedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvPhotos.setVisibility(View.GONE);
                rvSaves.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(firebaseUser.getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    btnEditProfile.setText("Following");
                } else {
                    btnEditProfile.setText("Follow");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void getPhotosForUser() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photosList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        photosList.add(post);
                    }
                }

                Collections.reverse(photosList);
                photoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSavedPosts() {
        final List<String> savedIds = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference()
                .child("Saves")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    savedIds.add(snap.getKey());
                }

                FirebaseDatabase.getInstance().getReference()
                        .child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        savedPostsList.clear();

                        for (DataSnapshot snap1 : snapshot.getChildren()) {
                            Post post = snap1.getValue(Post.class);

                            for (String id : savedIds) {
                                if (post.getPostId().equals(id)) {
                                    savedPostsList.add(post);
                                }
                            }

                        }
                        postAdapterSaves.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}