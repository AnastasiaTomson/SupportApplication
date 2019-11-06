package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import info.androidhive.fontawesome.FontDrawable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NextDateFragment extends Fragment {

    View view;
    public String url;
    public RequestBody requestBody;
    public JSONObject user;

    public NextDateFragment(String staff, String group, JSONObject user_list) {
        url = "http://192.168.150.200:9000/api/tickets/support_application/?tab=2&staff_id=" + staff + "&group_id=" + group + "&format=json";
        user = user_list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.next_date_tasks, container, false);

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
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
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray json = new JSONArray(myResponse);
                                // Проходим циклом по списку словарей и формируем строки таблицы
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject jsonobj = json.getJSONObject(i);
                                    String id = jsonobj.getString("id");
                                    String type = jsonobj.getString("ticket_type");
                                    String phone = jsonobj.getString("phone");
                                    String message = jsonobj.getString("message");
                                    String city = jsonobj.getString("city");
                                    String building = jsonobj.getString("building");
                                    String street = jsonobj.getString("street");
                                    String connection_flat = jsonobj.getString("connection_flat");
                                    String address = address_constructor(city, street, building, connection_flat);

                                    // Передаем тип, сообщение, адрес
                                    addRow(id, type, message, address, phone);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        });
        return view;
    }


    public String address_constructor(String city, String street, String building, String flat){
        if (city.equals("null"))city = " ";
        if (street.equals("null"))street = " ";
        if (building.equals("null"))building = " ";
        if (flat.equals("0"))flat = " ";
        String address = city + " " + street + " " + building + " " + flat;
        return address;
    }


    public void addRow(String id, String type, String message, String address, String phone) {
        TableLayout tableLayout = view.findViewById(R.id.table);
        FontDrawable icon;

        // Инициализация строки таблицы
        TableRow tr = new TableRow(getContext());
        TextView td1 = new TextView(getContext());
        TextView td2 = new TextView(getContext());

        // При клике на строку появляется диалоговое окно
        tr.setOnClickListener(getOnClickDoSomething(id, type, phone, address, message));

        // Тип заявки -> иконки
        if (type.equals("Подключение")) {
            icon = new FontDrawable(Objects.requireNonNull(getActivity()), R.string.fa_plus_solid, true, false);
            icon.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark));
        } else {
            if (type.equals("Техподдержка")) {
                icon = new FontDrawable(Objects.requireNonNull(getActivity()), R.string.fa_wrench_solid, true, false);
                icon.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_blue_light));
            } else {
                if (type.equals("Подключен")) {
                    icon = new FontDrawable(Objects.requireNonNull(getActivity()), R.string.fa_check_solid, true, false);
                } else {
                    icon = new FontDrawable(Objects.requireNonNull(getActivity()), R.string.fa_ruble_sign_solid, true, false);
                }
                icon.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_blue));
            }
        }
        icon.setTextSize(20);
        int h = icon.getIntrinsicHeight();
        int w = icon.getIntrinsicWidth();
        icon.setBounds(w-10, 0, 0, h);
        td1.setCompoundDrawables(icon, null, null, null);

        TableRow.LayoutParams col1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
        TableRow.LayoutParams col2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 6f);
        col2.topMargin = 20;
        col2.bottomMargin = 20;

        td1.setCompoundDrawables(icon, null, null, null);
        td2.setText(message);

        td1.setLayoutParams(col1);
        td2.setLayoutParams(col2);

        tr.addView(td1);
        tr.addView(td2);

        td2.setTextSize(20);
        tr.setBackgroundResource(R.drawable.border);

        tableLayout.addView(tr); //добавляем созданную строку в таблицу

    }

    //, String address
    View.OnClickListener getOnClickDoSomething(final String id, final String type, final String phone, final String address, final String message) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<String> myList = new ArrayList<>();
                myList.add(id);
                myList.add(type);
                myList.add(phone);
                myList.add(address);
                myList.add(message);
                startActivity(new Intent(getActivity(), Main2Activity.class).putExtra("myList", myList).putExtra("user_inf", user.toString()));
            }
        };
    }

}
