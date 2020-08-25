package android.example.firebaseapp.adapter;

import android.content.Context;
import android.example.firebaseapp.R;
import android.example.firebaseapp.model.User;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    // se pune m inaintea variabilei pentru a se arata ca e private
    private Context mContext;
    private List<User> mUsers;

    // vom folosi acest adapter pentru "fragment" dar si pentru "activity". De aceea trebuie sa stiu care e care.
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_search_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        User user = mUsers.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.fullName.setText(user.getName());

        // înlocuiesc poza de profil a cuiva cu ic_launcher până se încarcă a lor
        Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

        isFollowed(user.getId(), holder.btnFollow);

        if (user.getId().equals(firebaseUser.getUid())) {
            holder.btnFollow.setVisibility(View.GONE);
        }
    }

    private void isFollowed(final String id, final Button btnFollow) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(firebaseUser.getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists()) {
                    btnFollow.setText("Already following");
                } else {
                    btnFollow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageProfile;
        public TextView username;
        public TextView fullName;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.fullName);
            btnFollow = itemView.findViewById(R.id.btn_follow);

        }
    }
}
