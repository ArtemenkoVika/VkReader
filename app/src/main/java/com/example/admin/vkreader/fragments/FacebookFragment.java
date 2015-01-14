package com.example.admin.vkreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.activity.FacebookShareActivity;
import com.example.admin.vkreader.async_task.LoadImageFromNetwork;
import com.example.admin.vkreader.entity.ResultClass;
import com.example.admin.vkreader.patterns.Singleton;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class FacebookFragment extends Fragment implements View.OnClickListener {
    private Singleton singleton = Singleton.getInstance();
    private ResultClass resultClass = ResultClass.getInstance();
    private LoginButton authButton;
    private Button shareButton;
    private Button profileButton;
    private TextView textView;
    private ProfilePictureView profilePictureView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FacebookShareActivity) getActivity()).setUiHelper(new UiLifecycleHelper(getActivity(), null));
        ((FacebookShareActivity) getActivity()).getUiHelper().onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);

        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(FacebookFragment.this);
        authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));

        shareButton = (Button) view.findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);

        profileButton = (Button) view.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(this);

        textView = (TextView) view.findViewById(R.id.nik);
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profileView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((FacebookShareActivity) getActivity()).getUiHelper().onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ((FacebookShareActivity) getActivity()).getUiHelper().onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((FacebookShareActivity) getActivity()).getUiHelper().onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((FacebookShareActivity) getActivity()).getUiHelper().onDestroy();
    }

    public final void facebookPublish(String name, String caption, String description, String link,
                                      String pictureLink) {
        if (FacebookDialog.canPresentShareDialog(getActivity().getApplicationContext(), FacebookDialog.
                ShareDialogFeature.SHARE_DIALOG)) {
            //Facebook-client is installed
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
                    .setName(name)
                    .setCaption(caption)
                    .setDescription(description)
                    .setLink(link)
                    .setPicture(pictureLink)
                    .build();
            try {
                ((FacebookShareActivity) getActivity()).getUiHelper().trackPendingDialogCall
                        (shareDialog.present());
            } catch (NullPointerException e) {
                System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
            }
        } else {
            //Facebook-client is not installed – use web-dialog
            Bundle params = new Bundle();
            params.putString("name", name);
            params.putString("caption", caption);
            params.putString("description", description);
            params.putString("link", link);
            params.putString("picture", pictureLink);
            WebDialog feedDialog = new WebDialog.FeedDialogBuilder(getActivity(),
                    Session.getActiveSession(), params)
                    .setOnCompleteListener(new WebDialog.OnCompleteListener() {
                        //Listener for web-dialog
                        @Override
                        public void onComplete(Bundle values, FacebookException error) {
                            if ((values != null) && (values.getString("post_id") != null) &&
                                    (error == null)) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "This entry was posted",
                                        Toast.LENGTH_LONG);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "This entry was posted", Toast.LENGTH_LONG);
                            }
                        }
                    })
                    .build();
            feedDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_button:
                facebookPublish(resultClass.getTitle().get(singleton.getPosition()), "",
                        resultClass.getText().get(singleton.getPosition()),
                        "https://vk.com/christian_parable",
                        resultClass.getUrls().get(singleton.getPosition()));
                break;
            case R.id.profile_button:
                textView.setText(singleton.getUserName());

                System.out.println(singleton.getUserId());
                profilePictureView.setProfileId(singleton.getUserId());

//                LoadImageFromNetwork load = new LoadImageFromNetwork(getActivity());
//                load.execute("http://graph.facebook.com/" + singleton.getUserId() +
//                        "/picture?type=large");
//                try {
//                    profilePictureView.setDefaultProfilePicture(load.get());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }

                textView.setVisibility(View.VISIBLE);
                profilePictureView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}