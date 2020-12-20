package com.digitalnx.digitalnxclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.digitalnx.digitalnxclient.networkUtils.NetworkCallbackError;
import com.digitalnx.digitalnxclient.networkUtils.NetworkUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private RequestQueue queue;
    private JSONObject loginResponse;
    EditText serverAddressView, usernameView, passwordView;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serverAddressView = findViewById(R.id.login_form_server);
        usernameView = findViewById(R.id.login_form_username);
        passwordView = findViewById(R.id.login_form_password);

        queue = Volley.newRequestQueue(this);
        sharedPref = getSharedPreferences("AUTH_CREDENTIALS", MODE_PRIVATE);
        String username = sharedPref.getString("USERNAME", null);
        String password = sharedPref.getString("PASSWORD", null);
        String serverAddress = sharedPref.getString("SERVER_ADDRESS", null);

        if(username != null &&  password != null && serverAddress != null) {
            login(true);
        } else {
            findViewById(R.id.login_button).setOnClickListener((view) -> { login(false); });
        }
    }

    private void login(boolean userPassStored) {
        String serverAddress, username, password;
        if(userPassStored) {
            serverAddress = sharedPref.getString("SERVER_ADDRESS", null);
            username = sharedPref.getString("USERNAME", null);
            password = sharedPref.getString("PASSWORD", null);
        } else {
            serverAddress = serverAddressView.getText().toString();
            username = usernameView.getText().toString();
            password = passwordView.getText().toString();

            if(serverAddress.length() == 0 || username.length() == 0 || password.length() == 0) {
                Toast.makeText(this, "Fill all input fields please.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            final String requestBody = jsonBody.toString();

            NetworkCallbackError onErrorFunc = () -> {
                Toast.makeText(this, "Error! Please try again.", Toast.LENGTH_SHORT).show();
            };

            NetworkUtils.Login(response -> {
                try {
                    loginResponse = new JSONObject(response);
                    int statusCode = (int) loginResponse.get("result");
                    if (statusCode == 200) {
                        String token = loginResponse.get("token").toString();
                        String backendVer = loginResponse.get("version").toString();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("SERVER_ADDRESS", serverAddress);
                        editor.putString("USERNAME", username);
                        editor.putString("PASSWORD", password);
                        editor.putString("TOKEN", token);
                        editor.putString("BACKEND_VER", backendVer);
                        editor.apply();
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Setting token for NetworkUtils
                        NetworkUtils.setToken(token);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if (statusCode == 401) {
                        Toast.makeText(this, "Authentication failed! Please try again.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Login failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }, requestBody, serverAddress, "/auth/login", queue, onErrorFunc);
        } catch (JSONException e) {
            Toast.makeText(this, "Invalid server! Please try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}