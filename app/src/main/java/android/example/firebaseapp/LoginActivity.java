package android.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.widget.Toast.*;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail;
    private EditText txtPassword;

    private Button btnLogin;
    private TextView txtRegisterUser;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);

        txtRegisterUser = findViewById(R.id.register_user);

        firebaseAuth = FirebaseAuth.getInstance();

        txtRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(LoginActivity.this, RegisterActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    makeText(LoginActivity.this, "Empty credentials", LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);
                }

            }
        });
    }

    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    makeText(LoginActivity.this, "Update the profile for better experience", LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeText(LoginActivity.this, e.getMessage(), LENGTH_SHORT).show();
            }
        });
    }
}
