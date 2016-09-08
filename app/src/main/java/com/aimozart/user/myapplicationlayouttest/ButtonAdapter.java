package com.aimozart.user.myapplicationlayouttest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2016/7/5.
 */
public class ButtonAdapter extends BaseAdapter{
    private class buttonViewHolder {
        TextView appName;
        ImageButton buttonClose;
        ImageButton buttonDelete;
        ImageButton buttonMove;
        ImageButton additionClose;
        LinearLayout listcontent;
        LinearLayout option;
        TextView songType;
    }

    private List<Music> mAppList;
    private LayoutInflater mInflater;
    private Context mContext;
    private String[] keyString;
    private int[] valueViewID;
    private buttonViewHolder holder;
    private ArrayList<LinearLayout>LayoutArray;
    private ArrayList<LinearLayout>LayoutArray1;
    //private LinearLayout[]LayoutArray;
    //private LinearLayout[]LayoutArray1;
    private boolean isflag = true;//判斷是否有list 啟動option(一次只有一個list 能用option)
    private String playListName;
    private MySQLiteOpenHelper listDataBase;

    private LinearLayout DialogLayout;
    private ListView playerlist;
    private SimpleAdapter sadapter;
    List<Map<String, String>> listArray = new ArrayList<Map<String, String>>();
    private int selectedList;

    public ButtonAdapter(Context c, List<Music> appList, int resource,
                         int[] to, String listname) {
        mAppList = appList;
        mContext = c;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        valueViewID = new int[to.length];
        LayoutArray = new ArrayList<LinearLayout>();
        LayoutArray1 = new ArrayList<LinearLayout>();
        //LayoutArray = new LinearLayout[appList.size()];
        //LayoutArray1 = new LinearLayout[appList.size()];
        System.arraycopy(to, 0, valueViewID, 0, to.length);
        playListName = listname;
        if(listDataBase==null)
            listDataBase = new MySQLiteOpenHelper(mContext);
        //get 清單(除目前清單與預設清單外)
        List<Member> lm = listDataBase.getAllSpots();
        for(int i=0; i<lm.size(); i++) {
            Member m = lm.get(i);
            Map<String, String> nameMap = new HashMap<String, String>();
            if(!m.getName().equals(playListName) && !m.getName().equals("α-alpha") && !m.getName().equals("β-beta") && !m.getName().equals("θ-theta"))
            {
                nameMap.put("name", m.getName());
                nameMap.put("music",m.getMusic());
                listArray.add(nameMap);
            }
        }
        // 移動清單dialog init
        DialogLayout = new LinearLayout(mContext);
        DialogLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        playerlist = new ListView(mContext);
        playerlist.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //設定listview 讓highlight match_parent(預設為wrap_content)
        playerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playerlist.getChildAt(selectedList).setBackgroundColor(0xFFFF);
                selectedList = position;
                playerlist.getChildAt(position).setBackgroundColor(0x66FFFF93);
                //標為被選
            }
        });
        sadapter = new SimpleAdapter(mContext,
                listArray, R.layout.playerlist,
                new String[] { "name" },
                new int[] { R.id.playerListSongName });
        playerlist.setAdapter(sadapter);
        DialogLayout.addView(playerlist);
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
            convertView = mInflater.inflate(R.layout.playlistview, null);
            holder = new buttonViewHolder();
            holder.appName = (TextView)convertView.findViewById(valueViewID[0]);
            holder.buttonClose = (ImageButton)convertView.findViewById(valueViewID[1]);
            holder.buttonDelete = (ImageButton)convertView.findViewById(valueViewID[2]);
            holder.buttonMove = (ImageButton)convertView.findViewById(valueViewID[3]);
            holder.additionClose = (ImageButton)convertView.findViewById(valueViewID[4]);
            holder.listcontent = (LinearLayout)convertView.findViewById(valueViewID[5]);
            holder.option = (LinearLayout)convertView.findViewById(valueViewID[6]);
            holder.songType = (TextView)convertView.findViewById(valueViewID[7]);
            //取得在playlistlayout中的view
            convertView.setTag(holder);
        }

        Music appInfo = mAppList.get(position);
        if (appInfo != null) {
            String aname = appInfo.getName();
            int type = 0;
            if(appInfo.getType()!=null)
                type = Integer.parseInt(appInfo.getType());
            String kind = "";
            if(type==1)
                kind = "種類: α";
            else if(type==2)
                kind = "種類: β";
            else if(type==3)
                kind = "種類: θ";

            //int mid = (Integer)appInfo.get(keyString[0]);
            //int bid = (Integer)appInfo.get(keyString[1]);
            holder.appName.setText(aname);
            holder.songType.setText(kind);
            //holder.buttonClose.setImageResource(R.drawable.select);
            LayoutArray.add(position,holder.listcontent);
            LayoutArray1.add(position, holder.option);
            //儲存layout 來做點擊隱藏用
            holder.buttonClose.setOnClickListener(new lvButtonListener(position));
            holder.buttonDelete.setOnClickListener(new lvButtonListener(position));
            holder.buttonMove.setOnClickListener(new lvButtonListener(position));
            holder.additionClose.setOnClickListener(new lvButtonListener(position));
            //為button 添加listener
        }
        if(position==0 && appInfo.getName().equals("+新增歌曲"))
            holder.buttonClose.setVisibility(View.GONE);
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
            Log.d("adapteraction", "" + vid);
            if(isflag) {
                if (vid == holder.buttonClose.getId()) {
               /* if(MainActivity.selectTolist[position]==false)
                {
                    MainActivity.selectTolist[position]=true;
                    buttonArray[position].setImageResource(R.drawable.select);
                }
                else
                {
                    MainActivity.selectTolist[position]=false;
                    buttonArray[position].setImageResource(R.drawable.cancel);
                }*/
                    isflag = false;
                    LayoutArray.get(position).setVisibility(View.GONE);
                    LayoutArray1.get(position).setVisibility(View.VISIBLE);
                }
            }
            if(vid == holder.buttonDelete.getId())
            {
                if(listDataBase.getSpots(playListName)!=null)
                {
                    Member m = listDataBase.getSpots(playListName).get(0);
                    String musicContent = m.getMusic();
                    if(musicContent.length()>1) {
                        if(musicContent.split("," + mAppList.get(position).getId() + ",").length==2) {
                            String[] contentArray = musicContent.split("," + mAppList.get(position).getId() + ",");
                            musicContent = contentArray[0] + "," + contentArray[1];
                        }//刪除序列中間
                        else if(musicContent.split(mAppList.get(position).getId() + ",").length==2) {
                            String[] contentArray = musicContent.split(mAppList.get(position).getId() + ",");
                            musicContent = contentArray[1];
                            Log.d("adapteraction1",musicContent+" "+contentArray.length);
                        }//刪除序列頭
                        else {
                            String[] contentArray = musicContent.split("," + String.valueOf(mAppList.get(position).getId()));
                            musicContent = contentArray[0];
                            Log.d("adapteraction2",musicContent+" "+contentArray.length+" "+mAppList.get(position).getId());
                        }//刪除序尾
                        //從清單中刪除音樂
                    }
                    else
                        musicContent = "";
                    m.setMusic(musicContent);
                    listDataBase.update(m);
                    //更新清單中的音樂序列
                    mAppList.remove(position);
                    notifyDataSetChanged();
                    //更新listview 內容
                }
                returnlist(position);
            }
            //刪除
            else if(vid == holder.buttonMove.getId())
            {
                Log.d("adpateraction","move");
                moveListDialog(position);
                returnlist(position);
            }
            //移動歌曲
            else if(vid== holder.additionClose.getId())
            {
                returnlist(position);
            }

            //removeItem(position);
        }
    }
    public void returnlist(int position)
    {
        LayoutArray.get(position).setVisibility(View.VISIBLE);
        LayoutArray1.get(position).setVisibility(View.GONE);
        isflag = true;
    }
    private void moveListDialog(final int position)
    {
        AlertDialog dialog = new AlertDialog.Builder(mContext,R.style.Dialog_Fullscreen)
                .setTitle(Html.fromHtml("<font color='#FFFFFF'>清單名</font>"))
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if(listArray.size()>0)
                            addSongToList(position);
                        dialog.cancel();
                    }
                }).create();
        if(DialogLayout.getParent()==null)
            dialog.setView(DialogLayout);//將listview 加入dialog
        else{
            //移除dialog 綁定再加入 否則會有illegalstateException
            ViewParent vp =DialogLayout.getParent();
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(DialogLayout);
            parent.removeAllViews();
            dialog.setView(DialogLayout);
        }
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.show();
        //set dialog title style
        // need to use after dialog.shoe()
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        int titleId = mContext.getResources().getIdentifier("alertTitle", "id", "android");
        TextView dialogTitle = (TextView) dialog.findViewById(titleId);
        dialogTitle.setTextSize(30);
    }
    public void addSongToList(int position)
    {
                String listName = listArray.get(selectedList).get("name");
                String musicContent = listArray.get(selectedList).get("music");
                boolean checkflag = true;
                if(musicContent.equals(""))
                    musicContent = ""+mAppList.get(position).getId();
                else
                {
                    String checkid[] = musicContent.split(",");
                    for(int j=0; j<checkid.length; j++)
                    {
                        if(Integer.parseInt(checkid[j])==mAppList.get(position).getId())
                        {
                            checkflag = false;
                            break;
                        }
                    }//檢查是否有重複id
                    if(checkflag)
                        musicContent+=","+mAppList.get(position).getId();
                }
                //檢查list content 是否有重複歌曲
                Member m = new Member(listName);
                m.setMusic(musicContent);
                listDataBase.update(m);
                //取得清單歌曲內容  並加上新增歌曲id(用,分隔)
        if(checkflag)
            Toast.makeText(mContext, "已將歌曲加入" + listName + "清單", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mContext, "歌曲已存在" + listName + "清單", Toast.LENGTH_SHORT).show();
    }



}

