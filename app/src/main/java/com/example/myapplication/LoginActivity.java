package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    public Button button;
    public EditText login, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = findViewById(R.id.sign_in);
        login = findViewById(R.id.login);
        login.setText("km");
        password = findViewById(R.id.password);
        password.setText("5");
        button.setOnClickListener(getOnClickDoSomething(login, password));
    }

    View.OnClickListener getOnClickDoSomething(final EditText login, final EditText password) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                String url = "http://192.168.150.200:9000/application_login_user/?format=json";

                OkHttpClient client = new OkHttpClient();
                final RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("login", login.getText().toString())
                        .addFormDataPart("password", password.getText().toString())
                        .build();
                final Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String myResponse = response.body().string();
                            Objects.requireNonNull(LoginActivity.this).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject json = new JSONObject(myResponse);
                                        String response = json.getString("response");
                                        if(json.length()!= 1) {
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("user_inf", json.toString()));
                                        }else {
                                            login.setText("");
                                            password.setText("");
                                        }
                                        Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    }
                });
            }
        };
    }

}
