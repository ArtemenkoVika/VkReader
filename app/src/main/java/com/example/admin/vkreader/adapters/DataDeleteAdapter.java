package com.example.admin.vkreader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.vkreader.R;

import java.util.List;

public class DataDeleteAdapter extends ArrayAdapter {
    private LayoutInflater inflater;
    private ViewHolder holder;

    public DataDeleteAdapter(Context context, int textViewResourceId, List items) {
        super(context, textViewResourceId, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder {
        public TextView textView;
        public Button button;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        String item = (String) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.row_delete, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.option_delete_text);
            holder.button = (Button) view.findViewById(R.id.button_delete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.button.setTag(position);
        holder.textView.setText(item);
        return view;
    }
}