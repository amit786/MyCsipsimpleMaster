//package com.csipsimple.f5chat.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.csipsimple.R;
//import com.csipsimple.f5chat.bean.Block;
//import com.csipsimple.f5chat.fragments.BlockList;
//
//import java.util.ArrayList;
//
///**
// * Created by Kashish1 on 7/18/2016.
// */
//public class BlockAdapter extends BaseAdapter {
//    ArrayList<Block> blockArrayList;
//    BlockList blockList;
//    private static LayoutInflater inflater = null;
//
//    public BlockAdapter(BlockList blockList, ArrayList<Block> blocklist) {
//        blockArrayList = blocklist;
//        this.blockList = blockList;
//        inflater = (LayoutInflater) blockList.getActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//    }
//
//    @Override
//    public int getCount() {
//        return blockArrayList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//    public class Holder {
//        //private section items
//        TextView username;
//
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        Holder holder=new Holder();
//        View rootview=null;
//
//            rootview=inflater.inflate(R.layout.block_list_row,null);
//            holder.username=(TextView)rootview.findViewById(R.id.block_user);
//
//            Block block=blockArrayList.get(position);
//            holder.username.setText(block.getName());
//
//        return rootview;
//    }
//}
