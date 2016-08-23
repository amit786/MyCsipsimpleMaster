package com.csipsimple.f5chat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.csipsimple.R;

/**
 * Created by Kashish1 on 7/13/2016.
 */
public class SettingArrayAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular txtTitle;
    public SettingArrayAdapter(Activity context,
                      String[] web, Integer[] imageId) {
        super(context, R.layout.list_items, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_items, null, true);
        txtTitle = (com.csipsimple.f5chat.font_family.Font_welcome_scr_Tv_regular) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img2);
        txtTitle.setText(web[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
