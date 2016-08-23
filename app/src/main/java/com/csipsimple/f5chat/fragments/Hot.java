package com.csipsimple.f5chat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.csipsimple.R;
import com.csipsimple.f5chat.adapter.ListAdapter;
import com.csipsimple.f5chat.bean.GamesCategory;

import java.util.ArrayList;

public class Hot extends Fragment {
	ArrayList<GamesCategory> gamesList;
	ListView gamesListView;
	ListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.hot_games, container, false);
		gamesList = new ArrayList<GamesCategory>();


		for (int i = 1; i <= 10; i++) {
			GamesCategory gc = new GamesCategory();
			gc.setAppName("App Name "+i);
			gc.setCaption("Add Caption");
			gc.setImageUrl("");
			gc.setDownloadLink("https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en");
			gamesList.add(gc);

		}

		gamesListView = (ListView) rootView.findViewById(R.id.hot_listview);
		adapter = new ListAdapter(getActivity(), R.layout.games_view, gamesList);
		gamesListView.setAdapter(adapter);
		
		return rootView;
	}

}
