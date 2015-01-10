package com.example.admin.vkreader.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.activity.MainActivity;
import com.example.admin.vkreader.adapters.CustomAdapter;
import com.example.admin.vkreader.async_task.ParseTask;
import com.example.admin.vkreader.patterns.Singleton;
import com.facebook.internal.Utility;
import com.facebook.widget.LoginButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class ListFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        View.OnClickListener {
    private onSomeEventListener someEventListener;
    private ListView listView;
    private Fragment fragment2;
    private ParseTask parseTask;
    private FrameLayout frameLayout;
    private LinearLayout linearLayout;
    private boolean isOnline;
    private ArrayList list = new ArrayList();
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            savedInstanceState = getArguments();
            isOnline = savedInstanceState.getBoolean(MainActivity.IDE_BUNDLE_BOOL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_list, container, false);
        final String appId = Utility.getMetadataApplicationId(getActivity());
        AsyncTask<Void, Void, Utility.FetchedAppSettings> task = new AsyncTask<Void, Void,
                Utility.FetchedAppSettings>() {
            @Override
            protected Utility.FetchedAppSettings doInBackground(Void... params) {
                Utility.FetchedAppSettings settings = Utility.queryAppSettings(appId, true);
                return settings;
            }

            @Override
            protected void onPostExecute(Utility.FetchedAppSettings result) {
                LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
                authButton.setFragment(ListFragment.this);
                authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));
            }
        };
        task.execute((Void[]) null);
        singleton = Singleton.getInstance();
        imageView = (ImageView) getActivity().findViewById(R.id.image);
        textView = (TextView) getActivity().findViewById(R.id.text);
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.frm);
        fragment2 = getActivity().getSupportFragmentManager().findFragmentById(R.id.details_frag);
        if (fragment2 != null) {
            linearLayout = (LinearLayout) getActivity().findViewById(R.id.fragment2);
            linearLayout.setOnClickListener(this);
            textView.setOnClickListener(this);
        }
        listView = (ListView) view.findViewById(R.id.my_list);
        try {
            if (isOnline) {
                if (resultClass.getTitle() == null) {
                    resultClass.setTitle(new ArrayList<String>());
                    resultClass.setText(new ArrayList<String>());
                    resultClass.setUrls(new ArrayList<String>());

                    parseTask = new ParseTask(getResources().getString(R.string.url));
                    parseTask.execute();
                    if (singleton.getArrayAdapter() == null)
                        singleton.setArrayAdapter(new CustomAdapter(getActivity(), R.layout.row,
                                parseTask.get()));
                }
            } else {
                if (singleton.getArrayAdapter() == null)
                    singleton.setArrayAdapter(new CustomAdapter(getActivity(), R.layout.row, list));
            }
            listView.setAdapter(singleton.getArrayAdapter());
            singleton.getArrayAdapter().setNotifyOnChange(true);
            listView.setOnItemClickListener(this);
            someEventListener.someListView(listView);
        } catch (InterruptedException e) {
            System.out.println(e + " - in MyListFragment");
        } catch (ExecutionException e) {
            System.out.println(e + " - in MyListFragment");
        } catch (NullPointerException e) {
            System.out.println(e + " - in MyListFragment (onCreateView)");
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        frameLayout.setVisibility(View.GONE);
    }

    public interface onSomeEventListener {
        public void someEvent(Integer i);

        public void someListView(ListView listView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e) {
            System.out.println(e + " - in MyListFragment");
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        someEventListener.someEvent(position);
        if (fragment2 != null) {
            try {
                frameLayout.setVisibility(View.GONE);
                if (!singleton.isDataBase()) click();
                else clickOfDataBase();
            } catch (NullPointerException e) {
                System.out.println(e + " - in MyListFragment (onItemClick)");
            }
        }
    }
}