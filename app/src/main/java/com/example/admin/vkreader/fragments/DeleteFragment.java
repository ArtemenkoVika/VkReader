package com.example.admin.vkreader.fragments;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.adapters.DataDeleteAdapter;
import com.example.admin.vkreader.java_classes.DataBase;
import com.example.admin.vkreader.patterns.Singleton;

public class DeleteFragment extends Fragment implements View.OnClickListener {
    private ListView listView;
    private DataBase dataBase = new DataBase();
    private Singleton singleton = Singleton.getInstance();
    private ListFragment listFragment;
    private Button buttonDeleteAll;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete, container, false);
        listView = (ListView) view.findViewById(R.id.list_delete);
        listFragment = new ListFragment();
        buttonDeleteAll = (Button) view.findViewById(R.id.button_delete_all);
        buttonDeleteAll.setOnClickListener(this);
        singleton.setDeleteAdapter(new DataDeleteAdapter(getActivity(), R.layout.row_delete, dataBase.
                showSavedArticles(getActivity())));
        listView.setAdapter(singleton.getDeleteAdapter());
        singleton.getDeleteAdapter().setNotifyOnChange(true);
        return view;
    }

    @Override
    public void onClick(View v) {
        singleton.setDataBase(false);
        dataBase.deleteAll(getActivity());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frm,
                listFragment).commit();
    }
}
