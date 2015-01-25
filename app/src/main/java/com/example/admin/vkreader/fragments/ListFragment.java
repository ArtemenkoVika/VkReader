package com.example.admin.vkreader.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.adapters.CustomAdapter;
import com.example.admin.vkreader.async_task.ParseTask;
import com.example.admin.vkreader.patterns.Singleton;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ListFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        View.OnClickListener {
    private onSomeEventListener someEventListener;
    private ListView listView;
    private Button buttonDeleteAll;
    private Fragment fragment2;
    private ParseTask parseTask;
    private ArrayList list = new ArrayList();
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_list, container, false);
        singleton = Singleton.getInstance();
        imageView = (ImageView) getActivity().findViewById(R.id.image);
        textView = (TextView) getActivity().findViewById(R.id.text);
        buttonDeleteAll = (Button) view.findViewById(R.id.button_delete_all);
        buttonDeleteAll.setOnClickListener(this);;
        fragment2 = getActivity().getSupportFragmentManager().findFragmentById(R.id.details_frag);
        listView = (ListView) view.findViewById(R.id.my_list);
        try {
            if (isOnline()) {
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
            someEventListener.someListView(listView, buttonDeleteAll);
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
        dataBase.deleteAll(getActivity());
        singleton.getArrayAdapter().clear();
    }

    public interface onSomeEventListener {
        public void someEvent(Integer i);

        public void someListView(ListView listView, Button buttonDeleteAll);
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
        singleton.setPosition(position);
        someEventListener.someEvent(position);
        if (fragment2 != null) {
            try {
                if (!singleton.isDataBase()) click();
                else clickOfDataBase();
            } catch (NullPointerException e) {
                System.out.println(e + " - in MyListFragment (onItemClick)");
            }
        }
    }
}