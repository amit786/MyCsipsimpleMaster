package com.csipsimple.f5chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.GamesCategory;

import java.util.List;


public class ListAdapter extends ArrayAdapter<GamesCategory> {
    boolean listColorChanger = true;

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<GamesCategory> items) {
        super(context, resource, items);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.games_view, null);

        }
        v.setBackgroundColor(position % 2 == 0 ? Color.WHITE : getContext().getResources().getColor(R.color.grey));

        final GamesCategory p = getItem(position);

        if (p != null) {
            ImageView tt1 = (ImageView) v.findViewById(R.id.imageview);
            TextView tt2 = (TextView) v.findViewById(R.id.tv_games_name);
            TextView tt3 = (TextView) v.findViewById(R.id.tv_caption_name);
            LinearLayout llDownload = (LinearLayout) v.findViewById(R.id.ll_donwload);


            if (tt2 != null) {
                tt2.setText(p.getAppName());
            }

            if (tt3 != null) {
                tt3.setText(p.getCaption());
            }

            if (llDownload != null) {

                llDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(p.getDownloadLink())));
//                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//                        try {
//                            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                        } catch (android.content.ActivityNotFoundException anfe) {
//                            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(p.getDownloadLink())));
//                        }
                    }
                });
            }
        }

        return v;
    }

}