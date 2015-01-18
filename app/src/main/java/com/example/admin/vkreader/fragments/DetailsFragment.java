package com.example.admin.vkreader.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.patterns.Singleton;

public class DetailsFragment extends BaseFragment {
    public static final String ARG_POSITION = "param_det";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            savedInstanceState = getArguments();
            position = savedInstanceState.getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        singleton = Singleton.getInstance();
        imageView = (ImageView) view.findViewById(R.id.image);
        textView = (TextView) view.findViewById(R.id.text);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            try {
                if (!singleton.isDataBase()) click();
                else clickOfDataBase();
            } catch (NullPointerException e) {
                System.out.println(e + " - in DetailsFragment");
                e.printStackTrace();
            }
        }
    }
}
