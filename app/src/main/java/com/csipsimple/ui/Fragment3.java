package com.csipsimple.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csipsimple.R;

public class Fragment3 extends Fragment1 {

	private FragmentTabHost mTabHost;
	private TextView text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//mTabHost = new FragmentTabHost(getActivity());
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout,
				null);
		text = (TextView) v.findViewById(R.id.text);
		text.setText("Current Tab is: ");
//		if (getArguments() != null) {
//			//
//			try {
//				String value = getArguments().getString("key");
//				text.setText("Current Tab is: " + value);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		return v;
	}
}
