//package com.csipsimple.f5chat.fragments;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//
//import com.csipsimple.f5chat.adapter.BlockAdapter;
//import com.csipsimple.R;
//import com.csipsimple.f5chat.bean.Block;
//
//import java.util.ArrayList;
//
///**
// * Created by Kashish1 on 7/18/2016.
// */
//public class BlockList extends RootFragment implements View.OnClickListener {
//    View rootView;
//    ListView blockList;
//    BlockAdapter blockAdapter;
//    ArrayList<Block> blockArrayList = new ArrayList<Block>();
//    LinearLayout back;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.block_list, container, false);
//
//        blockList=(ListView)rootView.findViewById(R.id.block_listview);
//        back=(LinearLayout)rootView.findViewById(R.id.back);
//
//        back.setOnClickListener(this);
//        for(int i=0;i<=10;i++) {
//            blockArrayList.add(new Block("amit"));
//        }
//        allBlockListItem();
//        return rootView;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId())
//        {
//            case R.id.back:
//                back();
//                break;
//        }
//    }
//
//    public void allBlockListItem()
//    {
//        blockAdapter=new BlockAdapter(this,blockArrayList);
//        blockList.setAdapter(blockAdapter);
//        blockAdapter.notifyDataSetChanged();
//    }
//    @Override
//    public boolean onBackPressed() {
//        back();
//        return super.onBackPressed();
//    }
//
//    public void back()
//    {
//
//        if (getFragmentManager().getBackStackEntryCount() == 0) {
//            getActivity().finish();
//        } else {
//            getFragmentManager().popBackStack();
//        }
//    }
//
//}
