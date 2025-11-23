package com.example.tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tracker.model.User;
import com.example.tracker.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("TrackerPrefs", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        if (userId != null) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Login button - show dialog
        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            showLoginDialog();
        });
    }

    private void showLoginDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Login");

        android.view.View view = getLayoutInflater().inflate(R.layout.activity_register, null);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);

        builder.setView(view);
        builder.setPositiveButton("Login", (dialog, which) -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(email, password);
            RetrofitClient.getApiService().login(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User loggedInUser = response.body();
                        saveUserId(loggedInUser.getId());
                        startActivity(new Intent(WelcomeActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(WelcomeActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(WelcomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveUserId(String userId) {
        SharedPreferences prefs = getSharedPreferences("TrackerPrefs", MODE_PRIVATE);
        prefs.edit().putString("userId", userId).apply();
    }
}
