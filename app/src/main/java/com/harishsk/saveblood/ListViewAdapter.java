package com.harishsk.saveblood;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    private Activity activity;
    private List<Blood> BlList;

    ListViewAdapter(Activity activity, List<Blood> blList) {
        this.activity = activity;
        BlList = blList;
    }

    @Override
    public int getCount() {
        return BlList.size();
    }

    @Override
    public Object getItem(int position) {
        return BlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemview = inflater.inflate(R.layout.list_adapter_data, null);

        TextView name = itemview.findViewById(R.id.lt_name);
        TextView gender = itemview.findViewById(R.id.lt_gender);
        TextView age = itemview.findViewById(R.id.lt_age);
        TextView blgrp = itemview.findViewById(R.id.lt_blgrp);
        TextView phno = itemview.findViewById(R.id.lt_phno);
        TextView place = itemview.findViewById(R.id.lt_place);

        name.setText(BlList.get(position).getName());
        gender.setText(BlList.get(position).getGender());
        age.setText(BlList.get(position).getAge());
        blgrp.setText(BlList.get(position).getBlgrp());
        phno.setText(BlList.get(position).getPhno());
        place.setText(BlList.get(position).getPlace());

        return itemview;
    }
}
