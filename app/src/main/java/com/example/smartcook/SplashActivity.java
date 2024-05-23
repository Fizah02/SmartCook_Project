package com.example.smartcook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button startLoginButton = findViewById(R.id.start_button);
        startLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    // User is already logged in, navigate to BottomTab
                    startActivity(new Intent(SplashActivity.this, BottomTab.class));
                } else {
                    // User is not logged in, navigate to LoginActivity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }

                finish();
            }
        });

    }

}
