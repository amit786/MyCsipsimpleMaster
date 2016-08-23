package com.csipsimple.f5chat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.csipsimple.R;
import com.csipsimple.f5chat.adapter.StickerListAdapter;
import com.csipsimple.f5chat.bean.StickerCategory;

import java.util.ArrayList;

public class StickerHot extends Fragment {
	ArrayList<StickerCategory> stickerList;
	ListView stickerListView;
	StickerListAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.new_games, container, false);
		stickerList = new ArrayList<StickerCategory>();

		for (int i = 1; i <= 10; i++)
		{
			StickerCategory sc = new StickerCategory();
			sc.setStickerName("Sticker "+i);
			sc.setImgUrl("Add Caption");
			sc.setStickerSize(i+"k");
			stickerList.add(sc);

		}

		stickerListView = (ListView) rootView.findViewById(R.id.new_listview);
		adapter = new StickerListAdapter(getActivity(), R.layout.sticker_view, stickerList);
		stickerListView.setAdapter(adapter);
		
		return rootView;
	}

}
