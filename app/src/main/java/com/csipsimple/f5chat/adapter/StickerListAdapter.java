package com.csipsimple.f5chat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.StickerCategory;

import java.util.List;

/**
 * Created by Kashish1 on 7/20/2016.
 */
public class StickerListAdapter extends ArrayAdapter<StickerCategory> {
    public StickerListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public StickerListAdapter(Context context, int resource, List<StickerCategory> items) {
        super(context, resource, items);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.sticker_view, null);
        }
        v.setBackgroundColor(position % 2 == 0 ? Color.WHITE : getContext().getResources().getColor(R.color.grey));

        final StickerCategory p = getItem(position);

        if (p != null) {
            ImageView tt1 = (ImageView) v.findViewById(R.id.imageview);
            TextView tt2 = (TextView) v.findViewById(R.id.tv_sticker_name);
            TextView tt3 = (TextView) v.findViewById(R.id.tv_sticker_size);


            if (tt2 != null) {
                tt2.setText(p.getStickerName());
            }

            if (tt3 != null) {
                tt3.setText(p.getStickerSize());
            }


        }

        return v;
    }
}
