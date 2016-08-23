package com.csipsimple.f5chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.f5chat.bean.CountryCodeBean;
import com.csipsimple.f5chat.utility.Chatutility;
import com.csipsimple.f5chat.utility.PreferenceConstants;
import com.csipsimple.f5chat.utility.SharedPrefrence;
import com.csipsimple.f5chat.view.*;


import java.util.ArrayList;

/**
 * Created by HP on 23-05-2016.
 */
public class CountryCode extends Activity {
    String cname,number,ccode;
    LinearLayout ll;
    ListView lv;
    com.csipsimple.f5chat.view.OpenRegularTextView tvnext;
    ArrayList<CountryCodeBean>clist;
    RelativeLayout relative_header;
    private String[] countryname,code;
    SharedPrefrence share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_list);
        share = SharedPrefrence.getInstance(this);

        relative_header=(RelativeLayout)findViewById(R.id.relative_header);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Chatutility.changeStatusBarCustomColor(this,"#2c3342");
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0, 20, 0, 0);
//            relative_header.setLayoutParams(params);

        }
       // Chatutility.changeStatusBarColor(this);
        countryname = getResources().getStringArray(R.array.country_names_arrays);
        code = getResources().getStringArray(R.array.country_codes_arrays);
        lv=(ListView) findViewById(R.id.countrylist);
        tvnext=(OpenRegularTextView)findViewById(R.id.tvnext);
        tvnext.setEnabled(false);
        tvnext.setAlpha(0.5f);

        ll=(LinearLayout)findViewById(R.id.back);
        clist=new ArrayList<CountryCodeBean>();
        for (int i = 0 ; i< countryname .length ; i++)
        {
            CountryCodeBean cBean = new CountryCodeBean();
            cBean.setCountryCode(code[i]);
            cBean.setCountryName(countryname[i]);
            clist.add(cBean);
        }

        MyAdapter md=new MyAdapter(clist);
        lv.setAdapter(md);
        ll.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                finish();
            }

        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                 cname=clist.get(position).getCountryName();
                ccode=clist.get(position).getCountryCode();
                tvnext.setEnabled(true);
                tvnext.setAlpha(1f);
            }
        });
        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share.setValue(SharedPrefrence.COUNTRY_NAME,cname);
                share.setValue(SharedPrefrence.COUNTRY_CODE, ccode.replace("+", ""));

                setResult(PreferenceConstants.countryCode);
                finish();
            }
        });
        Bundle in=getIntent().getExtras();
        if(in!= null) {
             number = in.getString("number");
        }
    }
    class MyAdapter extends BaseAdapter
    {
        ArrayList<CountryCodeBean> countryList;
        public MyAdapter(ArrayList<CountryCodeBean> list) {
            this.countryList =list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return countryList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater lif= getLayoutInflater();
            View v= lif.inflate(R.layout.country_code, null);
            TextView tv=(TextView) v.findViewById(R.id.codetext);
             CountryCodeBean c= clist.get(position);
            String countryCodeWithName = c.getCountryName()+" ("+c.getCountryCode()+")";
            tv.setText(countryCodeWithName);

            return v;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
