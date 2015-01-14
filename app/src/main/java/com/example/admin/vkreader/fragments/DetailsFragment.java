package com.example.admin.vkreader.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.activity.FacebookShareActivity;
import com.example.admin.vkreader.activity.GoogleShareActivity;
import com.example.admin.vkreader.patterns.Singleton;
import com.google.android.gms.plus.PlusShare;

public class DetailsFragment extends BaseFragment implements View.OnClickListener {
    public static final String ARG_POSITION = "param_det";
    public static final String ARG_BOOL = "is_online";
    private Button buttonFacebook;
    private Button buttonGoogle;
    private boolean isOnline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            savedInstanceState = getArguments();
            position = savedInstanceState.getInt(ARG_POSITION);
            isOnline = savedInstanceState.getBoolean(ARG_BOOL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        singleton = Singleton.getInstance();
        buttonFacebook = (Button) view.findViewById(R.id.btn_facebook);
        buttonFacebook.setOnClickListener(this);
        buttonGoogle = (Button) view.findViewById(R.id.btn_g_plus);
        buttonGoogle.setOnClickListener(this);
        if (isOnline && !singleton.isDataBase()) {
            buttonFacebook.setVisibility(View.VISIBLE);
            buttonGoogle.setVisibility(View.VISIBLE);
        } else {
            buttonFacebook.setVisibility(View.GONE);
            buttonGoogle.setVisibility(View.GONE);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_facebook:
                try {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), FacebookShareActivity.class);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                }
                break;
            case R.id.btn_g_plus:
                try {
                    googlePlusPublish();

//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), GoogleShareActivity.class);
//                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
    }

    public void googlePlusPublish() {
        Intent shareIntent = new PlusShare.Builder(getActivity())
                .setType("text/plain")
                .setContentUrl(Uri.parse("https://vk.com/christian_parable"))
                .getIntent();
        startActivityForResult(shareIntent, GoogleShareActivity.REQUEST_CODE_RESOLVE_ERR);
    }
}
