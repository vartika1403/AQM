package com.example.admin.aqm;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

public class WifiListAdapter extends ArrayAdapter<String> {
    private List<String> wifiList;
    private Context context;

    public WifiListAdapter(@NonNull Context context, @LayoutRes int resource, List<String> wifiList) {
        super(context, resource, wifiList);
        this.wifiList = wifiList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public String getItem(int i) {
        return wifiList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.wifi_list_item, null);
        }

        String wifiItemName = wifiList.get(i);
        if (wifiItemName != null && !wifiItemName.isEmpty()) {
            TextView wifiNameTextView = (TextView)v.findViewById(R.id.wifi_name_text);
            ImageView wifiIconImage = (ImageView)v.findViewById(R.id.wifi_icon_image);

            if(wifiNameTextView != null) {
                wifiNameTextView.setText(wifiItemName);
            }

            if (wifiIconImage != null) {
                wifiIconImage.setImageResource(R.drawable.ic_menu_wifi);
            }
        }
        return v;
    }
}
