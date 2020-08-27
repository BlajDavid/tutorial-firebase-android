package android.example.firebaseapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.example.firebaseapp.CommentActivity;
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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Post post = mPosts.get(position);
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

        isLiked(post.getPostId(), holder.like);
        countNumberOfLikes(post.getPostId(), holder.numberOfLikes);
        countNumberOfComments(post.getPostId(), holder.numberOfComments);
        isSaved(post.getPostId(), holder.save);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getPostId())
                            .child(firebaseUser.getUid())
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getPostId())
                            .child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.numberOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Saves")
                            .child(firebaseUser.getUid())
                            .child(post.getPostId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Saves")
                            .child(firebaseUser.getUid())
                            .child(post.getPostId()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void isLiked(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // if it has already been liked by the current user:
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countNumberOfLikes(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countNumberOfComments(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference()
                .child("Comments")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(final String postId, final ImageView image) {
        FirebaseDatabase.getInstance().getReference()
                .child("Saves")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()) {
                    image.setImageResource(R.drawable.ic_save_black);
                    image.setTag("saved");
                } else {
                    image.setImageResource(R.drawable.ic_save);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
