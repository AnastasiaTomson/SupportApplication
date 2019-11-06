package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private TabLayout tablayout;
    private ViewPager viewPager;
    public JSONObject user;
    public Handler mHandler = new Handler();

    public LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        tablayout = findViewById(R.id.tabLayout_id);
        viewPager = findViewById(R.id.viewpager_id);

        ViewPagerAdapterMain adapter = new ViewPagerAdapterMain(getSupportFragmentManager());
        if (getIntent().hasExtra("user_inf")) {
            try {
                user = new JSONObject(getIntent().getStringExtra("user_inf"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
//            Toast.makeText(getBaseContext(), user.getString("staff_id"), Toast.LENGTH_SHORT).show();
            adapter.AddFragmentMain(new TodayFragment(user.getString("staff_id"), user.getString("group_id"), user), "Сегодня");
            adapter.AddFragmentMain(new NextDateFragment(user.getString("staff_id"), user.getString("group_id"), user), "Завтра");
            adapter.AddFragmentMain(new ThisWeekFragment(user.getString("staff_id"), user.getString("group_id"), user), "На неделю");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewPager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewPager);

    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                1000 * 10, 10, locationListener);
//        locationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
//                locationListener);
//        checkEnabled();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        1000 * 10, 10, locationListener);
                                locationManager.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                                        locationListener);
                                checkEnabled();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            try {
                showLocation(location);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void showLocation(final Location location) throws JSONException {
        if (location == null)
          return;
        String url = "http://192.168.150.200:9000/tickets/add_coordinate/?format=json";
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        String datetime = String.valueOf(new Date(location.getTime()));
        OkHttpClient client = new OkHttpClient();
        final RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user", user.getString("staff_id"))
                .addFormDataPart("latitude", lat)
                .addFormDataPart("longitude", lon)
                .addFormDataPart("datetime", datetime)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Objects.requireNonNull(MainActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject json = new JSONObject(myResponse);
                                String response = json.getString("response");
                                TextView text = findViewById(R.id.coordinate);
                                text.setText(formatLocation(location));
//                                Toast.makeText(getBaseContext(), formatLocation(location), Toast.LENGTH_LONG).show();

                                Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });

    }

    private String formatLocation(Location location) {
        if (location == null)
          return "";
        return String.format(
            "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
            location.getLatitude(), location.getLongitude(), new Date(
                location.getTime()));
    }

    private void checkEnabled() {

    }
}