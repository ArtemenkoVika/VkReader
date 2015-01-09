package com.example.admin.vkreader.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.activity.FacebookShareActivity;
import com.example.admin.vkreader.activity.GoogleShareActivity;
import com.example.admin.vkreader.patterns.Singleton;
import com.google.android.gms.plus.PlusShare;

public class DetailsFragment extends BaseFragment implements View.OnClickListener {
    public static final String ARG_POSITION = "param";
    private Button buttonFacebook;
    private Button buttonGoogle;

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
        buttonFacebook = (Button) view.findViewById(R.id.btn_facebook);
        buttonFacebook.setOnClickListener(this);
        buttonGoogle = (Button) view.findViewById(R.id.btn_g_plus);
        buttonGoogle.setOnClickListener(this);
        if (isOnline()) {
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
                if (isOnline()) {
                    try {
                        //facebookPublish();

                Intent intent = new Intent();
                intent.setClass(getActivity(), FacebookShareActivity.class);
                startActivityForResult(intent, 1);
                    } catch (Exception e) {
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.net),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_g_plus:
                if (isOnline()) {
                    try {
                        googlePlusPublish();

//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), GoogleShareActivity.class);
//                    startActivityForResult(intent, 1);
                    } catch (Exception e) {
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.net),
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void facebookPublish() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, resultClass.getUrls().get(position));
        shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, Html.fromHtml
                ("<p>" + resultClass.getText().get(position) + "</p>"));
        startActivity(shareIntent);
    }

    public void googlePlusPublish() {
        Intent shareIntent = new PlusShare.Builder(getActivity())
                .setType("text/plain")
                .setText(resultClass.getText().get(position))
                        //.setContentUrl(Uri.parse("https://developers.google.com/+/"))
                .getIntent();
        startActivityForResult(shareIntent, GoogleShareActivity.REQUEST_CODE_RESOLVE);
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() == null) {
            return false;
        } else return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
