package android.example.firebaseapp.adapter;

import android.content.Context;
import android.example.firebaseapp.R;
import android.example.firebaseapp.model.Post;
import android.example.firebaseapp.model.User;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = mPosts.get(position);
        Picasso.get().load(post.getImageUrl()).into(holder.postImage);
        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user.getImageUrl().equals("default")) {
                    holder.profileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImageUrl()).into(holder.profileImage);
                }
                holder.username.setText(user.getUsername());
                holder.author.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // aici facem legătura cu toate componentele din "post_item"!
        public ImageView more;

        public ImageView profileImage;
        public TextView username;

        public ImageView postImage;

        public ImageView like;
        public ImageView comment;
        public ImageView save;


        public TextView numberOfLikes;
        public TextView author;
        public SocialTextView description;
        public TextView numberOfComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            more = itemView.findViewById(R.id.more);
            profileImage = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            postImage = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            numberOfLikes = itemView.findViewById(R.id.number_of_likes);
            author = itemView.findViewById(R.id.author);
            description = itemView.findViewById(R.id.description);
            numberOfComments = itemView.findViewById(R.id.number_of_comments);

        }
    }

}
