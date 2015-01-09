package com.example.admin.vkreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.activity.FacebookShareActivity;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FacebookFragment extends Fragment implements View.OnClickListener {
    private Button shareButton;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    private boolean pendingPublishReauthorization = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);
        if (savedInstanceState != null) {
            pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
        }
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));
        shareButton = (Button) view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        return view;
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(state);
        }
    };

    public void onSessionStateChange(SessionState state) {
        if (state.isOpened()) {
            shareButton.setVisibility(View.VISIBLE);
            shareButton.setOnClickListener(this);
            if (pendingPublishReauthorization &&
                    state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
                pendingPublishReauthorization = false;
                publishStory();
            } else if (state.isClosed()) {
                shareButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        ((FacebookShareActivity) getActivity()).getUiHelper().onSaveInstanceState(outState);
    }

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session != null) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                pendingPublishReauthorization = true;
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(getActivity(), PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }

            Bundle postParams = new Bundle();
            postParams.putString("name", "Facebook SDK for Android");
            postParams.putString("caption", "Build great social apps and get more installs.");
            postParams.putString("description", "The Facebook SDK for Android makes it easier and " +
                    "faster to develop Facebook integrated Android apps.");
            postParams.putString("link", "https://developers.facebook.com/android");
            postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master" +
                    "/Images/iossdk_logo.png");

            Request.Callback callback = new Request.Callback() {
                public void onCompleted(Response response) {
                    JSONObject graphResponse = response
                            .getGraphObject()
                            .getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        Log.i("TAG",
                                "JSON error " + e.getMessage());
                    }
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Toast.makeText(getActivity()
                                        .getApplicationContext(),
                                error.getErrorMessage(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity()
                                        .getApplicationContext(),
                                postId,
                                Toast.LENGTH_LONG).show();
                    }
                }
            };

            Request request = new Request(session, "me/feed", postParams,
                    HttpMethod.POST, callback);

            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
        }

    }

    public final void facebookPublish(String name, String caption, String description, String link, String pictureLink) {
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
            ((FacebookShareActivity) getActivity()).getUiHelper().trackPendingDialogCall
                    (shareDialog.present());
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
                                        "Запись опубликована",
                                        Toast.LENGTH_LONG);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Запись не опубликована", Toast.LENGTH_LONG);
                            }
                            ;
                        }

                        ;
                    })
                    .build();
            feedDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareButton:
                facebookPublish("name", "caption", "description", "https://developers.facebook.com/android",
                        "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
                break;
            default:
                break;
        }

    }
}
