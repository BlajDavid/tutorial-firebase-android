package android.example.firebaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnRegister;
    private TextView loginUser;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUsername = findViewById(R.id.username);
        txtName = findViewById(R.id.fullName);
        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);

        btnRegister = findViewById(R.id.register);
        loginUser = findViewById(R.id.login_user);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString();
                String name = txtName.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(name) ||
                        TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    makeText(RegisterActivity.this, "Empty credentials", LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    makeText(RegisterActivity.this, "Password too short", LENGTH_SHORT).show();
                } else {
                    registerUser(username, name, email, password);
                }
            }
        });
    }

    private void registerUser(final String username, final String name, final String email, String password) {
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("username", username);
                map.put("email", email);
                map.put("id", Objects.requireNonNull(auth.getCurrentUser()).getUid());
                map.put("bio", "");
                map.put("imageUrl", "default");
                databaseReference
                        .child("Users")
                        .child(auth.getCurrentUser().getUid())
                        .setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    makeText(
                                            RegisterActivity.this,
                                            "Update the profile for better experience",
                                            LENGTH_SHORT
                                    ).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                makeText(
                        RegisterActivity.this,
                        e.getMessage(),
                        LENGTH_SHORT
                ).show();
            }
        });
    }
}