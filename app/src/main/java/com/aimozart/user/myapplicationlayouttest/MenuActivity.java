package com.aimozart.user.myapplicationlayouttest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;

import com.hb.views.PinnedSectionListView;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends FragmentActivity implements OnGestureListener{

    private GestureDetector detector;
    public long alarmtimer;
    private List<Member> memberList;
    private MySQLiteOpenHelper helper;
    private MemberAdapter memberAdapter;
    //view in list
    TextView countMainTV;
    ImageButton additional;
    ImageButton listDelete;
    ImageButton listRename;
    ImageButton optioncancel;
    LinearLayout listcontent;
    LinearLayout option;
    ArrayList<LinearLayout> LayoutArray;
    ArrayList<LinearLayout> LayoutArray1;
    private String []defaultList = new String[]{"預設清單","全部歌曲","α-alpha","β-beta","θ-theta","自訂清單"};
    boolean optionnotuse = true;

    private LinearLayout layer3;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay3_menu);
        layer3 = (LinearLayout)findViewById(R.id.layer3);
        layer3.setBackground(getLocalBitmap(this,R.drawable.listbg));
        if(helper == null){
            helper = new MySQLiteOpenHelper(this);
        }

        detector = new GestureDetector(this, this);
    }

    protected void onStart(){
        super.onStart();

        memberList = helper.getAllSpots();
        memberList.add(new Member(memberList.size(),"+新增清單",""));
        //新增清單選項
        LayoutArray = new ArrayList<LinearLayout>();
        LayoutArray1 = new ArrayList<LinearLayout>();
        ListView lvMember = (ListView) findViewById(R.id.listView);
        memberAdapter = new MemberAdapter(this);
        lvMember.setAdapter(memberAdapter);
        lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if(position==memberList.size()-1+defaultList.length)
                {
                    Intent intent = new Intent(MenuActivity.this, AddActivity.class);
                    startActivity(intent);
                }//點擊新增清單
                else if(position==0 | position==5){}
                else {
                    Intent intent = new Intent(MenuActivity.this, DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", (Serializable) memberList);
                    bundle.putInt("position", position);
                    if(position<defaultList.length)
                        intent.putExtra("listName", defaultList[position]);
                    else
                        intent.putExtra("listName", memberList.get(position-defaultList.length).getName());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        //countMainTV = (TextView)findViewById(R.id.countMainTV);
        //countMainTV.setText("共 " + memberList.size() + " 個清單");
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
        if(e1.getX() - e2.getX()>distance&&Math.abs(velocityX)>speed) {//左滑
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            // 設置切換動畫，從右邊進入，左邊退出
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return true;
        }
        else if (e2.getX() - e1.getX()>distance&&Math.abs(velocityX)>speed) {//右滑
            Intent intent = new Intent(this, IdentufyActivity.class);
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
    public void Page4btn(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
        // 設置切換動畫，從右邊進入，左邊退出
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private class MemberAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {
        private LayoutInflater layoutInflater;

        public MemberAdapter(Context context){
            layoutInflater = LayoutInflater.from(context);

        }
        @Override
        public int getCount() {
            return memberList.size()+defaultList.length;
        }

        @Override
        public Object getItem(int position) {
            if(position<defaultList.length)
                return defaultList[position];
            else
                return memberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(position<defaultList.length)
                return position;
            else
            return memberList.get(position-defaultList.length).getId()+defaultList.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.listview_item, parent, false);
            }
                TextView nameTV = (TextView) convertView.findViewById(R.id.nameTV);
            if(position>=defaultList.length) {
                Member member = memberList.get(position-defaultList.length);
                nameTV.setText(member.getName());
                additional = (ImageButton) convertView.findViewById(R.id.additional);
                listDelete = (ImageButton) convertView.findViewById(R.id.listDelete);
                listRename = (ImageButton) convertView.findViewById(R.id.listRename);
                optioncancel = (ImageButton) convertView.findViewById(R.id.opitoncancel);
                listcontent = (LinearLayout) convertView.findViewById(R.id.listcontent);
                option = (LinearLayout) convertView.findViewById(R.id.option);
                //get listview_item view
                LayoutArray.add(position-defaultList.length, listcontent);
                LayoutArray1.add(position-defaultList.length, option);
                ////儲存layout 來做點擊隱藏用
                additional.setVisibility(View.VISIBLE);
                additional.setOnClickListener(new listButtonListener(position));
                listDelete.setOnClickListener(new listButtonListener(position));
                listRename.setOnClickListener(new listButtonListener(position));
                optioncancel.setOnClickListener(new listButtonListener(position));
            }
            else
              nameTV.setText(defaultList[position]);
            //button set listener
            if(position==memberList.size()-1+defaultList.length)
                additional.setVisibility(View.GONE);
            else if(position==0 | position==5) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // , 1是可選寫的
                lp.setMargins(0, 0, 0, 0);
                nameTV.setLayoutParams(lp);//修改marginLayout
                nameTV.setTextSize(15);
                nameTV.setTextColor(0xFFFFFFFF);
                nameTV.setBackgroundColor(0xFFAB5F5F);
            }
            return convertView;
        }//若listview height 無固定會導致 position 亂序

        //pinnedSectionList set
        @Override public int getViewTypeCount() {
            return 2;
        }
        @Override
        public int getItemViewType(int position) {
            if(position==0 | position==6)
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

    class listButtonListener implements View.OnClickListener {
        private int position;

        listButtonListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            int vid=v.getId();
            Log.d("adapteraction", "" + vid);
            if(optionnotuse) {
                if (vid == additional.getId()) {
                    optionnotuse = false;
                    LayoutArray.get(position-defaultList.length).setVisibility(View.GONE);
                    LayoutArray1.get(position-defaultList.length).setVisibility(View.VISIBLE);
                }
            }
            if(vid == listDelete.getId())
            {
                Log.d("adapteraction", "delete");
                helper.deleteByName(memberList.get(position - defaultList.length).getName());
                memberList.remove(position - defaultList.length);
                memberAdapter.notifyDataSetChanged();
                returnlist(position);
            }
            //刪除
            else if(vid == listRename.getId())
            {
                Log.d("adpateraction","rename");
                showInputBox(memberList.get(position-defaultList.length).getName(),position);
                returnlist(position);
            }
            //重新命名
            else if(vid == optioncancel.getId())
            {
                Log.d("adpateraction", "cancel");

                returnlist(position);
            }
            //option 取消

            //removeItem(position);
        }
    }

    public void returnlist(int position)
    {
        LayoutArray.get(position-defaultList.length).setVisibility(View.VISIBLE);
        LayoutArray1.get(position-defaultList.length).setVisibility(View.GONE);
        optionnotuse = true;
    }
    public void showInputBox(String oldItem, final int index){
        final Dialog dialog=new Dialog(MenuActivity.this);
        dialog.setTitle("重新命名");
        dialog.setContentView(R.layout.input_box);
        final EditText editText=(EditText)dialog.findViewById(R.id.txtinput);
        editText.setText(oldItem);
        Button bt=(Button)dialog.findViewById(R.id.btdone);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Member m = memberList.get(index - defaultList.length);
                String oldName = m.getName();
                m.setName(editText.getText().toString());
                memberList.set(index - defaultList.length, m);
                helper.rename(m, oldName);
                memberAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void addBtn(View view){
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    public void newExampleBtn(View view){
        Member spot1 = new Member("測試清單");

        helper.insert(spot1);

        memberList = helper.getAllSpots();
        memberAdapter.notifyDataSetChanged();
        countMainTV.setText("共 " + memberList.size() + " 筆資料");
        Toast.makeText(MenuActivity.this, "已新增一筆範例", Toast.LENGTH_SHORT).show();
    }

    public void deleteAllBtn(View view){
        helper.deleteAll();
        memberList = helper.getAllSpots();
        memberAdapter.notifyDataSetChanged();
        countMainTV.setText("共 " + memberList.size() + " 筆資料");
        Toast.makeText(MenuActivity.this, "已刪除所有資料", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPause() {
        super.onPause();
        System.gc();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(layer3.getBackground()==null)
            layer3.setBackgroundResource(R.drawable.listbg);
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
