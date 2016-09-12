package com.aimozart.user.myapplicationlayouttest;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends FragmentActivity {
    private List<Music> musicList;
    //private MemberAdapter memberAdapter;
    private ButtonAdapter buttonAdapter;
    private ViewPager vpMember;
    private TextView tvTitle;
    private MySQLitePlayListOpenHelper plhelper;
    private MySQLiteOpenHelper helper;
    private String playListName;

    private LinearLayout DialogLayout;
    private ListView songlist;
    private SimpleAdapter sadapter;
    List<Map<String, String>> songArray = new ArrayList<Map<String, String>>();
    private int selectedSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        /*musicList = plhelper.getAllSpots();
        ListView lvMember = (ListView) findViewById(R.id.playlistView);
        memberAdapter = new MemberAdapter(this);
        lvMember.setAdapter((ListAdapter) memberAdapter);
        lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Intent intent = new Intent(DetailActivity.this, DetailMusicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", (Serializable) musicList);
                bundle.putInt("position", position);
                //intent.putExtras(bundle);
                //startActivity(intent);
            }
        });*/
        //Bundle bundle = getIntent().getExtras();
        //musicList = (List<Music>)bundle.getSerializable("list");
        //MemberAdapter memberAdapter = new MemberAdapter(getSupportFragmentManager(), musicList);
        //vpMember = (ViewPager)findViewById(R.id.vpMember);
        //vpMember.setAdapter(memberAdapter);
        //vpMember.setCurrentItem(bundle.getInt("position"));
        //tvTitleCount = (TextView)findViewById(R.id.tvTitleCount);
        Intent intent = getIntent();
        playListName = intent.getStringExtra("listName");
        //從menuactivity get list name
        if(plhelper == null){
            plhelper = new MySQLitePlayListOpenHelper(this);
        }
        if(helper == null){
            helper = new MySQLiteOpenHelper(this);
        }
        List<Music> spots = plhelper.getAllSpots();
        for(int i=0; i<spots.size(); i++)
        {
            Map<String, String> nameMap = new HashMap<String, String>();
            nameMap.put("id",""+spots.get(i).getId());
            nameMap.put("name", spots.get(i).getName());
            songArray.add(nameMap);
        }
        addSongListInit();
    }

    protected void onStart(){
        super.onStart();
        if(playListName.equals("全部歌曲")) {
            musicList = plhelper.getAllSpots();
            String []s = new String[musicList.size()];
            for(int i=0; i<s.length; i++) {
                s[i] = musicList.get(i).getName();
            }
            tvTitle = (TextView) findViewById(R.id.tvTitle);
            tvTitle.setText(playListName);
            ListView lvMember = (ListView) findViewById(R.id.playlistView);
//            ArrayAdapter<String> auttonAdapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,s);
//            lvMember.setAdapter(auttonAdapter);
            buttonAdapter = new ButtonAdapter(this, musicList, R.layout.playlistview, new int[]{R.id.list_name, R.id.additional, R.id.deletelist, R.id.movetolist,R.id.canceladdition, R.id.listcontent, R.id.option,R.id.songType}, playListName);
            lvMember.setAdapter(buttonAdapter);
            lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                        getSongDetail(position);
                }
            });
        }
        else {
            tvTitle = (TextView) findViewById(R.id.tvTitle);
            tvTitle.setText(playListName);
            Member tempmember = helper.getSpots(playListName).get(0);
            String musicContent = tempmember.getMusic();
            musicList = new ArrayList<Music>();
            musicList.clear();
            if(!playListName.equals("α-alpha") && !playListName.equals("β-beta") && !playListName.equals("θ-theta") && !playListName.equals("全部歌曲"))
               musicList.add(new Music(0, "+新增歌曲", "0"));
            if (!musicContent.equals("")) {
                String[] musicOrder = musicContent.split(",");
                for (int i = 0; i < musicOrder.length; i++)//id 從1開始
                {
                    Music musicSpot = plhelper.findById(Integer.parseInt(musicOrder[i]));
                    if (musicSpot != null)
                        musicList.add(musicSpot);
                }
            }
            //先去list table拿music 序列  再去music table 找id對應的音樂名稱
            //musicList = plhelper.getAllSpots();
            ListView lvMember = (ListView) findViewById(R.id.playlistView);
            //memberAdapter = new MemberAdapter(this);
            //lvMember.setAdapter((ListAdapter) memberAdapter);
            buttonAdapter = new ButtonAdapter(this, musicList, R.layout.playlistview, new int[]{R.id.list_name, R.id.additional, R.id.deletelist, R.id.movetolist,R.id.canceladdition, R.id.listcontent, R.id.option,R.id.songType}, playListName);
            lvMember.setAdapter(buttonAdapter);
            lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //Intent intent = new Intent(DetailActivity.this, DetailMusicActivity.class);
                    if (position == 0 && !playListName.equals("α-alpha") && !playListName.equals("β-beta") && !playListName.equals("θ-theta")&& !playListName.equals("全部歌曲")) {
                        moveListDialog();
                    }
                    //新增歌曲
                    else {
                    /*Bundle bundle = new Bundle();
                    bundle.putSerializable("list", (Serializable) musicList);
                    bundle.putInt("position", position);*/
                        getSongDetail(position);
                    }
                    //intent.putExtras(bundle);
                    //startActivity(intent);
                }
            });
        }
   }
    private class MemberAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;

        public MemberAdapter(Context context){
            layoutInflater = LayoutInflater.from(context);

        }
        @Override
        public int getCount() {
            return musicList.size();
        }

        @Override
        public Object getItem(int position) {
            return musicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return musicList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.listview_item, parent, false);
            }
            Music music = musicList.get(position);
            TextView nameTV = (TextView) convertView
                    .findViewById(R.id.nameTV);
            nameTV.setText(music.getName());

            return convertView;
        }
    }

    /*private class MemberAdapter extends FragmentStatePagerAdapter{
        List<Music> musicList;
        public MemberAdapter(FragmentManager fm, List<Music> musicList) {
            super(fm);
            this.musicList = musicList;
        }

        @Override
        public Object getItem(int position) {
            return musicList.get(position);
        }

        @Override
        public int getCount() {
            return musicList.size();
        }
    }*/
    public void addmusicBtn(View view){
        //Intent intent = new Intent(this, AddActivity.class);
        //startActivity(intent);
        Toast.makeText(DetailActivity.this, "還沒新增，目前測試", Toast.LENGTH_SHORT).show();
    }
    public void backToMenu(View view){
        finish();
    }
    public void toFirstBtn(View view){
        vpMember.setCurrentItem(0);
    }
    public void toEndBtn(View view){
        vpMember.setCurrentItem(musicList.size() - 1);
    }
    public void newtestBtn(View view){
        /*Music spot1 = new Music("測試曲2");
        //Music spot2 = new Music("測試曲2");
        long id;//
        Music tempspot;
        if(plhelper.getSpots(spot1.getName()).size()>0){
            tempspot = plhelper.getSpots(spot1.getName()).get(0);
            id = tempspot.getId();
        }
        else {
            id = plhelper.insert(spot1);
            tempspot = new Music((int)id, spot1.getName());
        }
        //若清單內無重複歌名 則新增
        Member tempmember = helper.getSpots(playListName).get(0);
        String musicContent = tempmember.getMusic();
        if(musicContent==null)
            musicContent = ""+id;
        else
        {
            String checkid[] = musicContent.split(",");
            boolean checkflag = true;
            for(int i=0; i<checkid.length; i++)
            {
                if(Integer.parseInt(checkid[i])==(int)id)
                {
                    checkflag = false;
                    break;
                }
            }//檢查是否有重複id
            if(checkflag)
                musicContent+=","+id;
        }
        tempmember.setMusic(musicContent);
        helper.update(tempmember);
        //取得清單歌曲內容  並加上新增歌曲id(用,分隔)
        //plhelper.insert(spot2);

        //musicList = plhelper.getAllSpots();
        musicList.add(tempspot);
        //memberAdapter.notifyDataSetChanged();
        buttonAdapter.notifyDataSetChanged();
        Toast.makeText(DetailActivity.this, "已新增一首測試曲目", Toast.LENGTH_SHORT).show();*/
    }
    public void getSongDetail(int position)
    {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST
        };
        Cursor cursor = contentResolver.query(uri, columns, MediaStore.Audio.Media.TITLE +"=?",new String[]{musicList.get(position).getName()},null);
        String songDetail = "";
        if (cursor.moveToNext()) {
            String title = cursor.getString(0);
            String album = cursor.getString(1);
            String artist = cursor.getString(2);
            songDetail = "曲目: "+title+
                    "\n"+"專輯: "+album+
                    "\n"+"歌手: "+artist;
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("歌曲資訊")
                .setMessage(songDetail)
                .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
    }
    private void addSongListInit()
    {
        DialogLayout = new LinearLayout(this);
        DialogLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        songlist = new ListView(this);
        songlist.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //設定listview 讓highlight match_parent(預設為wrap_content)
        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                songlist.getChildAt(selectedSong).setBackgroundColor(0xFFFF);
                selectedSong = position;
                songlist.getChildAt(position).setBackgroundColor(0x66FFFF93);
                //標為被選
            }
        });
        sadapter = new SimpleAdapter(this,
                songArray, R.layout.playerlist,
                new String[] { "name" },
                new int[] { R.id.playerListSongName });
        songlist.setAdapter(sadapter);
        DialogLayout.addView(songlist);
    }
    private void moveListDialog()
    {
        AlertDialog dialog = new AlertDialog.Builder(this,R.style.Dialog_Fullscreen)
                .setTitle(Html.fromHtml("<font color='#FFFFFF'>清單名</font>"))
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if(songArray.size()>0)
                        {
                            final int position = selectedSong;
                            addSongToList(position);
                        }
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
        int titleId = this.getResources().getIdentifier("alertTitle", "id", "android");
        TextView dialogTitle = (TextView) dialog.findViewById(titleId);
        dialogTitle.setTextSize(30);
    }
    public void addSongToList(int position)
    {
        String listName = playListName;
        List<Member> allList = helper.getSpots(listName);
        String musicContent = allList.get(0).getMusic();
        boolean checkflag = true;
        if(musicContent.equals("")) {
            musicContent = "" + songArray.get(position).get("id");
            musicList.add(new Music(songArray.get(position).get("name"),""));
            buttonAdapter.notifyDataSetChanged();
        }
        else
        {
            String checkid[] = musicContent.split(",");
            for(int j=0; j<checkid.length; j++)
            {
                Log.d("songid",songArray.get(position).get("name"));
                if(checkid[j].equals(songArray.get(position).get("id")))
                {
                    checkflag = false;
                    break;
                }
            }//檢查是否有重複id
            if(checkflag) {
                musicContent += "," + songArray.get(position).get("id");
                musicList.add(new Music(songArray.get(position).get("name"),""));
                buttonAdapter.notifyDataSetChanged();
            }
        }
        //檢查list content 是否有重複歌曲
        Member m = new Member(listName);
        m.setMusic(musicContent);
        helper.update(m);
        //取得清單歌曲內容  並加上新增歌曲id(用,分隔)
        if(checkflag)
            Toast.makeText(this, "已將歌曲加入" + listName + "清單", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "歌曲已存在" + listName + "清單", Toast.LENGTH_SHORT).show();
    }

    public void deleteAllmusBtn(View view){
        plhelper.deleteAll();
        musicList.clear();
        musicList.add(new Music(0, "+新增歌曲", "0"));
        //musicList = plhelper.getAllSpots();
        //memberAdapter.notifyDataSetChanged();
        buttonAdapter.notifyDataSetChanged();
        Toast.makeText(DetailActivity.this, "已刪除所有資料", Toast.LENGTH_SHORT).show();
    }
}

