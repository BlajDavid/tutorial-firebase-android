package android.example.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout;
    private EditText txtEdit;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.logout);
        txtEdit = findViewById(R.id.edit);
        btnAdd = findViewById(R.id.add);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged out!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txtEdit.getText().toString();
                if(name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No data entered", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("catalysts").push().child("name").setValue(name);
                }
            }
        });

    }
}