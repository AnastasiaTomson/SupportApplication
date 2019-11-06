package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private TabLayout tablayout;
    private ViewPager viewPager;
    public JSONObject user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        Bundle arguments = getIntent().getExtras();
        ArrayList<String> myList = arguments.getStringArrayList("myList");
        if(getIntent().hasExtra("user_inf")) {
            try {
                user = new JSONObject(getIntent().getStringExtra("user_inf"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        tablayout = findViewById(R.id.tabLayout_id);
        viewPager = findViewById(R.id.viewpager_id);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new InformationFragment(), "Информация", myList);
        adapter.AddFragment(new EditFragment(user), "Действия", myList);

        viewPager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewPager);
    }
}
