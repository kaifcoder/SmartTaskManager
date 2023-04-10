package com.example.smarttaskmanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smarttaskmanager.Models.Passwords;
import com.example.smarttaskmanager.R;

import java.util.List;

public class PasswordAdapter extends BaseAdapter {
    Context context;
    List<Passwords> passwordsList;

    public PasswordAdapter(Context context, List<Passwords> passwordsList) {
        this.context = context;
        this.passwordsList = passwordsList;
    }

    @Override
    public int getCount() {
        return passwordsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.password_list_item,null);
        TextView title = view.findViewById(R.id.accountName);
        Passwords passwords = passwordsList.get(position);
        title.setText(passwords.getAccid());
        return view;
    }
}
