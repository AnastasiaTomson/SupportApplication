package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class InformationFragment extends Fragment {
    View view;
    public TextView type, phone, address, message;

    public InformationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.information_fragment, container,false);
        ArrayList<String> myList = getArguments().getStringArrayList("myList");
        assert myList != null;
        type = view.findViewById(R.id.type);
        type.setText(myList.get(1));
        phone = view.findViewById(R.id.phone);
        String st_phone = myList.get(2);
        if (st_phone.equals("null"))st_phone = " ";
        phone.setText(st_phone);
        address = view.findViewById(R.id.address);
        address.setText(myList.get(3));
        message = view.findViewById(R.id.message);
        message.setText(myList.get(4));
        return view;
    }

}
