package com.aimozart.user.myapplicationlayouttest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/7/5.
 */
public class ButtonAdapterID extends BaseAdapter {
    private class buttonViewHolder {
        TextView appName;
        ImageButton buttonClose;
        LinearLayout songBg;
    }

    private ArrayList<HashMap<String, Object>> mAppList;
    private LayoutInflater mInflater;
    private Context mContext;
    private String[] keyString;
    private int[] valueViewID;
    private buttonViewHolder holder;
    public  ArrayList<ImageButton> buttonArray;
    public  ArrayList<LinearLayout>bgArray;

    public ButtonAdapterID(Context c, ArrayList<HashMap<String, Object>> appList, int resource,
                           String[] from, int[] to) {
        mAppList = appList;
        mContext = c;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        keyString = new String[from.length];
        valueViewID = new int[to.length];
        buttonArray = new ArrayList<ImageButton>();
        bgArray = new ArrayList<LinearLayout>() ;
        System.arraycopy(from, 0, keyString, 0, from.length);
        System.arraycopy(to, 0, valueViewID, 0, to.length);
    }

    @Override
    public int getCount() {
        return mAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position){
        mAppList.remove(position);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            holder = (buttonViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.liststyle, null);
            holder = new buttonViewHolder();
            holder.appName = (TextView)convertView.findViewById(valueViewID[0]);
            holder.buttonClose = (ImageButton)convertView.findViewById(valueViewID[1]);
            holder.songBg = (LinearLayout)convertView.findViewById(valueViewID[2]);

            convertView.setTag(holder);
        }

        HashMap<String, Object> appInfo = mAppList.get(position);
        if (appInfo != null) {
            String aname = (String) appInfo.get(keyString[0]);
            //int mid = (Integer)appInfo.get(keyString[0]);
            //int bid = (Integer)appInfo.get(keyString[1]);
            holder.appName.setText(aname);
            //holder.buttonClose.setImageResource(R.drawable.select);
            buttonArray.add(position, holder.buttonClose);
            bgArray.add(position,holder.songBg);
            holder.buttonClose.setOnClickListener(new lvButtonListener(position));
        }
        Log.d("position", ""+position);
        return convertView;
    }

    class lvButtonListener implements View.OnClickListener {
        private int position;

        lvButtonListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            int vid=v.getId();
            if (vid == holder.buttonClose.getId())
            {
                if(IdentufyActivity.detect.getVisibility()==View.VISIBLE)
                {
                    Log.d("numstate",""+position);
                    IdentufyActivity.list.remove(position);
                    buttonArray.remove(position);
                    ButtonAdapterID.this.notifyDataSetChanged();
                }
                /*if(MainActivity.selectTolist[position]==false)
                {
                    MainActivity.selectTolist[position]=true;
                    buttonArray[position].setImageResource(R.drawable.select);
                }
                else
                {
                    MainActivity.selectTolist[position]=false;
                    buttonArray[position].setImageResource(R.drawable.cancel);
                }*/

            }
                //removeItem(position);
        }
    }
}
