package com.aimozart.user.myapplicationlayouttest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.io.InputStream;

/**
 * Created by user on 2016/7/17.
 */
public class HelpActivity extends Activity implements OnGestureListener {

    private GestureDetector detector;
    private ListView helpList;
    private String[] item = new String[]{"Settings","畫面","聲音","Helps","使用指南","腦波學小常識","其他"};
    private HelpAdapter adapter;

    private LinearLayout layer4;
    BitmapDrawable bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay4_help);
        layer4 = (LinearLayout)findViewById(R.id.layer4);
        bitmap = getLocalBitmap(this, R.drawable.helpbg);
        layer4.setBackground(bitmap);
        detector = new GestureDetector(this, this);
        helpList = (ListView)findViewById(R.id.listView);
        adapter = new HelpAdapter(this);
        helpList.setAdapter(adapter);
        helpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==4 | position==5)
                {
                    Intent intent = new Intent();
                    intent.putExtra("position", String.valueOf(position));
                    intent.setClass(HelpActivity.this, UseGuide.class);
                    startActivity(intent);
                }
            }
        });
    }
    private class HelpAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {
        private LayoutInflater layoutInflater;

        public HelpAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return item.length;
        }

        @Override
        public Object getItem(int position) {
            return item[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.helper, parent, false);
            }
            TextView itemName = (TextView) convertView.findViewById(R.id.helpItem);
            LinearLayout helpLayout = (LinearLayout)convertView.findViewById(R.id.helpLayout);
            itemName.setText(item[position]);
            if (position == 0 | position == 3) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1); // , 1是可選寫的
                lp.setMargins(0, 0, 0, 0);
                itemName.setLayoutParams(lp);//修改marginLayout
                itemName.setTextSize(15);
                itemName.setTextColor(0xFFFFFFFF);
                itemName.setBackgroundColor(0xFF4F335B);
            }
            else
                helpLayout.setBackgroundColor(0xFFFFFFFF);
            return convertView;
        }//若listview height 無固定會導致 position 亂序
        //pinnedSectionList set
        @Override public int getViewTypeCount() {
            return 2;
        }
        @Override
        public int getItemViewType(int position) {
            if(position==0 | position==3)
                return 1;
            else
                return 0;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            if (viewType == 1) {
                return true;
            } else
                return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }//重寫dispatchTouchEvent 讓detector onTouchevent 優先listview onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        final int distance = 100;//滑动距离
        final int speed = 200;//滑动速度
        if (e2.getX() - e1.getX()>distance&&Math.abs(velocityX)>speed) {//右滑
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            // 設置切換動畫，從左邊進入，右邊退出
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }else
            return false;
    }

    public void Page1btn(View view) {
        Intent intent = new Intent(this, MediaActivity.class);
        startActivity(intent);
        // 設置切換動畫，從左邊進入，右邊退出
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

    }
    public void Page2btn(View view) {
        Intent intent = new Intent(this, IdentufyActivity.class);
        startActivity(intent);
        // 設置切換動畫，從左邊進入，右邊退出
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

    }
    public void Page3btn(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        // 設置切換動畫，從左邊進入，右邊退出
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

    }
    @Override
    public void onPause() {
        super.onPause();
        //layer4.setBackground(null);
        System.gc();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(layer4.getBackground()==null)
            layer4.setBackgroundResource(R.drawable.helpbg);
    }
    public BitmapDrawable getLocalBitmap(Context con, int resourceId){
        InputStream inputStream = con.getResources().openRawResource(resourceId);
        BitmapDrawable ob = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(inputStream, null, getBitmapOptions(1)));
        return ob;
    }
    public BitmapFactory.Options getBitmapOptions(int scale){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        return options;
    }
}
