package com.example.admin.vkreader.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.patterns.Singleton;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

public class DetailsFragment extends BaseFragment {
    public static final String ARG_POSITION = "param_det";
    private LoginButton authButton;

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
        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(DetailsFragment.this);
        authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));
        if (isOnline()) authButton.setVisibility(View.VISIBLE);
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
