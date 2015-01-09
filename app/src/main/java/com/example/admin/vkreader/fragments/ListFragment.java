package com.example.admin.vkreader.fragments;

import android.app.Activity;
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

import java.util.ArrayList;
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
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);
        singleton = Singleton.getInstance();;
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
        if (isOnline) {
            try {
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
            } catch (InterruptedException e) {
                System.out.println(e + " - in MyListFragment");
            } catch (ExecutionException e) {
                System.out.println(e + " - in MyListFragment");
            } catch (NullPointerException e) {
                System.out.println(e + " - in MyListFragment (onCreateView)");
            }
        } else {
            if (singleton.getArrayAdapter() == null)
                singleton.setArrayAdapter(new CustomAdapter(getActivity(), R.layout.row, list));
        }
        listView.setAdapter(singleton.getArrayAdapter());
        singleton.getArrayAdapter().setNotifyOnChange(true);
        listView.setOnItemClickListener(this);
        someEventListener.someListView(listView);
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