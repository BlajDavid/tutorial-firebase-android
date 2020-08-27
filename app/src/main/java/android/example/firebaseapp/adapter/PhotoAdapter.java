package android.example.firebaseapp.adapter;

import android.content.Context;
import android.example.firebaseapp.R;
import android.example.firebaseapp.fragments.PostDetailFragment;
import android.example.firebaseapp.model.Post;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context mContext;

    private List<Post> posts;

    public PhotoAdapter(Context mContext, List<Post> posts) {
        this.mContext = mContext;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Post post = posts.get(position);

        Picasso.get().load(post.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("SharedPreferencesPhoto", Context.MODE_PRIVATE).edit().putString("postId", post.getPostId()).apply();

                // call PostDetailFragment
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment())
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);

        }
    }
}
