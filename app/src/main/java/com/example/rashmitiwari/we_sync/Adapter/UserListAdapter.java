package com.example.rashmitiwari.we_sync.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rashmitiwari.we_sync.R;
import com.example.rashmitiwari.we_sync.model.Information;
import com.example.rashmitiwari.we_sync.model.Message;

import java.util.List;

/**
 * Created by Rashmi on 8/4/2017.
 */

public class UserListAdapter extends ArrayAdapter<Information> {
    public UserListAdapter(Context context, int resource, List<Information> objects) {
        super(context, resource, objects);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_user_list, parent, false);
        }

        TextView userTextView = (TextView) convertView.findViewById(R.id.userTextView);

        Information information = getItem(position);


        userTextView.setText(information.getUserName());


        return convertView;
    }
}
