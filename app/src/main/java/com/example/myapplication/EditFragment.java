package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditFragment extends Fragment {
    View view;
    public JSONObject user;

    public EditFragment(JSONObject user_list) {
        user = user_list;
    }

    public Button push;
    public EditText comment;
    public String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.action_fragment, container,false);
        ArrayList<String> myList = getArguments().getStringArrayList("myList");
        assert myList != null;
        id = myList.get(0);
        comment = view.findViewById(R.id.comment);
        comment.setOnClickListener(getOnClickDoSomething());
        push = view.findViewById(R.id.push);
        push.setOnClickListener(getOnClickDoSomething());
        return view;
    }

    View.OnClickListener getOnClickDoSomething() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case (R.id.comment): comment.setText(""); break;
                    case (R.id.push):
                        String fio = null;
                        try {
                            fio = user.getString("fio");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String text = "Ответ от " + fio + ": \n " + comment.getText().toString();
                        String url = "http://192.168.150.200:9000/tickets/application/";
                        OkHttpClient client = new OkHttpClient();
                        String staff = null;
                        try {
                            staff = user.getString("user_id");
                            Toast.makeText(getContext(), staff, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final RequestBody requestBody;
                        assert staff != null;
                        requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("user", staff)
                            .addFormDataPart("message", text)
                            .addFormDataPart("id", id)
                            .build();
                        final Request request = new Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response){
                            }

                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            }

                        });
                        startActivity(new Intent(getActivity(), MainActivity.class).putExtra("user_inf", user.toString()));
                        break;
                }
            }
        };
    }
}
