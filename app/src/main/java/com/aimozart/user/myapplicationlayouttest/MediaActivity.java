package com.aimozart.user.myapplicationlayouttest;

/**
 * Created by user on 2016/5/23.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MediaActivity extends Activity implements OnClickListener, OnCompletionListener, OnGestureListener {
    //MediaPlayer物件
    private MediaPlayer mediaPlayer;
    private GestureDetector detector;

    private ImageButton btnPrev;
    private ImageButton btnNext;
    private ImageButton btnPlay;
    private ImageButton listOption;
    private Button btnStop;
    private ImageButton btnPlayMode;
    private ImageView btnSearch;
    public int state = 0;//記錄Rand狀態
    private TextView tvRand;
    /*state數字代表狀態
    0=歌單不循環(不隨機)
    1=歌單循環(不隨機)
    2=單曲循環
    3=隨機不循環
    4=隨機循環
    * */
    //TODO 搜尋歌曲 清單選歌
    private TextView txtSongName;
    private TextView ListName;
    private ImageView albumPicture;

    //儲存音樂清單
    private LinkedList<Song> songList;

    //音樂播放索引(播到哪一首)
    private int index = 0;
    private int[] preindex;
    private int prepointer = -1;
    //是否為暫停狀態
    private boolean isPause = true;
    //兩次返回間隔時間
    private long mExitTime = 0;
    //進度條
    private SeekBar ProceseekBar;
    private TextView nowPlayTime;
    int Duration;//音樂時間
    Handler handler=new Handler();

    private int position = 0;//播放位置
    private boolean isChang = false;

    private EditText searchArea;
    private ListPopupWindow relationList;
    private ListView playerlist;
    LinearLayout DialogLayout;
    List<Map<String, String>> nameList = new ArrayList<Map<String, String>>();
    List<Map<String, String>> songArray = new ArrayList<Map<String, String>>();
    SimpleAdapter adapter;
    SimpleAdapter sadapter;

    private MySQLiteOpenHelper listDataBase;
    private MySQLitePlayListOpenHelper songDataBase;

    private LinearLayout layer1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay1_playmusic);
        addShortcut();//自動建立捷徑
        detector = new GestureDetector(this, this);
        if(listDataBase==null)
            listDataBase = new MySQLiteOpenHelper(this);
        if(songDataBase==null)
            songDataBase = new MySQLitePlayListOpenHelper(this);
        layer1 = (LinearLayout)findViewById(R.id.layer1);
        layer1.setBackground(getLocalBitmap(this,R.drawable.playbg));
        initView();
        getMusics();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

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
        if ((e1.getX() - e2.getX()) > 50) {//左滑
            Intent intent = new Intent(this, IdentufyActivity.class);
            startActivity(intent);
            // 設置切換動畫，從右邊進入，左邊退出
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return true;
        } else
            return false;
    }

    public void Page2btn(View view) {
        Intent intent = new Intent(this, IdentufyActivity.class);
        startActivity(intent);
        // 設置切換動畫，從右邊進入，左邊退出
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void Page3btn(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        // 設置切換動畫，從右邊進入，左邊退出
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void Page4btn(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
        // 設置切換動畫，從右邊進入，左邊退出
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void initView() {
        preindex = new int[1000];
        btnPrev = (ImageButton) findViewById(R.id.btnPrev);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        //btnStop = (Button) findViewById(R.id.btnStop);
        btnPlayMode = (ImageButton) findViewById(R.id.playMode);
        btnSearch = (ImageView)findViewById(R.id.img_search);

        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        //btnStop.setOnClickListener(this);
        btnPlayMode.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        ProceseekBar=(SeekBar)findViewById(R.id.seekBar);
        ProceseekBar.setOnSeekBarChangeListener(new ProcessBarListener());
        nowPlayTime=(TextView)findViewById(R.id.nowPlayTime);
        nowPlayTime.setText("00:00");
        txtSongName = (TextView) findViewById(R.id.txtSongName);
        txtSongName.setSelected(true);
        txtSongName.setText("");
        ListName = (TextView)findViewById(R.id.ListName);
        albumPicture = (ImageView)findViewById(R.id.SongPicture);

        searchArea = (EditText)findViewById(R.id.searcharea);
        searchArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Member> listArray = listDataBase.getOrderSpots(searchArea.getText().toString());
                nameList.clear();
                for (int i = 0; i < listArray.size(); i++) {
                    Member m = listArray.get(i);
                    Map<String, String> nameMap = new HashMap<String, String>();
                    nameMap.put("name", m.getName());
                    nameList.add(nameMap);
                    try{
                    if(!relationList.isShowing()) {
                        relationList.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NEEDED);
                        relationList.show();
                    }}catch (Exception e){}
                    adapter.notifyDataSetChanged();
                }
                //關鍵字更新搜尋listview
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        relationList = new ListPopupWindow(this);
        adapter = new SimpleAdapter(this,
                nameList, R.layout.playerlist,
                new String[] { "name" },
                new int[] { R.id.playerListSongName });
        relationList.setAdapter(adapter);
        relationList.setAnchorView(searchArea);//設定彈出位置
        relationList.setModal(false);//包含setfocusable  需設為false 否則editText無法hasfocus
        relationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> nameMap = nameList.get(position);
                searchArea.setText(nameMap.get("name"));
                relationList.dismiss();
            }
        });
        //小清單初始化
        listOption = (ImageButton)findViewById(R.id.playeroption);
        listOption.setOnClickListener(this);
        DialogLayout = new LinearLayout(this);
        DialogLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        playerlist = new ListView(this);
        playerlist.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //設定清單title
        sadapter = new SimpleAdapter(this,
                songArray, R.layout.playerlist,
                new String[] { "name" },
                new int[] { R.id.playerListSongName });
        playerlist.setAdapter(sadapter);
        playerlist.setOnTouchListener(new OnTouchListener() {
            float x, y, upx, upy;

            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x = event.getX();
                    y = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    upx = event.getX();
                    upy = event.getY();
                    int position1 = ((ListView) view).pointToPosition((int) x, (int) y);
                    int position2 = ((ListView) view).pointToPosition((int) upx, (int) upy);
                    Log.d("isonTouch", "" + position1 + "" + position2);
                    if (Math.abs(x - upx) > 200) {
                        View v = ((ListView) view).getChildAt(position1);
                        removeListItem(v, position1);
                    }
                }
                return false;
            }

        });
        playerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                playing();
            }
        });//點擊清單播放該首歌
        DialogLayout.addView(playerlist);
    }

    protected void removeListItem(View rowView, final int positon) {
        if (rowView != null) {
            final Animation animation = (Animation) AnimationUtils.loadAnimation(rowView.getContext(), R.anim.item_anim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    songArray.remove(positon);
                    sadapter.notifyDataSetChanged();
                    animation.cancel();
                }
            });


            rowView.startAnimation(animation);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrev:
                doPrev();
                break;
            case R.id.btnNext:
                doNext();
                break;

            case R.id.btnPlay:
                doPlay();
                break;

            /*case R.id.btnStop:
                doStop();
                break;*/

            case R.id.playMode:
                doRand();
                break;
            case R.id.playeroption:
                optionClick();
                break;
            case  R.id.img_search:
                searchList();
                break;
        }
    }
    private void doRand() {
        if (state == 0){//目前狀態0
            state = 1;//轉成狀態1
            btnPlayMode.setImageResource(R.mipmap.mode1);
            Toast.makeText(this,"歌曲循環",Toast.LENGTH_SHORT).show();
            //btnRand.setText("S1");
            prepointer = -1;//歸零
        }
        else if (state == 1){
            state = 2;
            btnPlayMode.setImageResource(R.mipmap.mode2);
            Toast.makeText(this,"單曲循環",Toast.LENGTH_SHORT).show();
            //btnRand.setText("S2");
            prepointer = -1;//歸零

        }
        else if (state == 2){
            state = 3;
            btnPlayMode.setImageResource(R.mipmap.mode3);
            Toast.makeText(this,"隨機不循環",Toast.LENGTH_SHORT).show();
            //btnRand.setText("S3");
        }
        else if (state == 3){
            state = 4;
            btnPlayMode.setImageResource(R.mipmap.mode4);
            Toast.makeText(this,"隨機循環",Toast.LENGTH_SHORT).show();
            //btnRand.setText("S4");
        }
        else if (state == 4){
            state = 0;
            btnPlayMode.setImageResource(R.mipmap.mode0);
            Toast.makeText(this,"歌曲不循環",Toast.LENGTH_SHORT).show();
            //btnRand.setText("S0");
            prepointer = -1;//歸零
        }
        Log.d("showstate",""+state);

    }

    private void doStop() {
        if (mediaPlayer != null) {
            isPause = false;
            mediaPlayer.stop();
            //btnPlay.setText("Play");
        }

    }

    private void doPlay() {
        if (songArray == null || songArray.size() == 0) {
            return;
        }

        if (isPause) {
            isPause = false;
            playing();
            btnPlay.setImageResource(R.mipmap.pause);
            //btnPlay.setText("Pause");
        }else{
            isPause = true;
            isChang = false;
            playing();
            //mediaPlayer.pause();
            btnPlay.setImageResource(R.mipmap.play);
            //btnPlay.setText("Play");
        }

    }

    private void doNext() {
        position = 0;
        if (songArray == null || songArray.size() == 0) {
            return;
        }

        if (index < songArray.size()-1) {
            if (!isPause)
                btnPlay.setImageResource(R.mipmap.pause);
            if (isPause){
                btnPlay.setImageResource(R.mipmap.play);
                isChang = true;
                position = 0;
            }
            if (state == 3 || state == 4) {
                prepointer++;
                preindex[prepointer] = index;
                position = 0;
                doRandPlay();
            } else if (state == 0 || state == 1 || state == 2){
                index++;
                //isPause = false;
                position = 0;
                playing();
                //btnPlay.setText("Pause");
            }
        }
        /*else
           index++;*/
        else if (index == songArray.size()-1){
            if (!isPause)
                btnPlay.setImageResource(R.mipmap.pause);
            if (isPause){
                btnPlay.setImageResource(R.mipmap.play);
                isChang = true;
                position = 0;
            }
            if (state == 3 || state == 4) {
                prepointer++;
                preindex[prepointer] = index-1;
                position = 0;
                doRandPlay();
            } else if (state == 0 || state == 1 || state == 2) {
                index = 0;
                //isPause = false;
                position = 0;
                playing();
            }
        }
        /*else
            index++;*/
        /*if (index == songArray.size()-1){
            btnPlay.setImageResource(R.drawable.pause);
            if (state == 3 || state == 4) {
                prepointer++;
                preindex[prepointer] = index-1;
                doRandPlay();
            } else if (state == 0 || state == 1 || state == 2) {
                index = songArray.size()-1;
                isPause = false;
                playing();
            }
        }*/
    }

    private void doRandPlay() {
        position = 0;
        int max = songArray.size();
        int rang = (int)(Math.random()* max);
        index = rang;
        if (!isPause) {
            isPause = false;
            position = 0;
            playing();
        }
        if (isPause){
            position = 0;
            playing();
        }
        //btnPlay.setText("Pause");

    }

    private void doPrev() {
        position = 0;
        if (songArray == null || songArray.size() == 0) {
            return;
        }

        if (index > 0) {
            if (!isPause)
                btnPlay.setImageResource(R.mipmap.pause);
            if (isPause){
                btnPlay.setImageResource(R.mipmap.play);
                isChang = true;
                position = 0;
            }
            if (state == 3 || state == 4) {
                //如果隨機撥放存放上一首的陣列空了
                if (prepointer == -1 ){
                    prepointer = -1;
                    position = 0;
                    playing();
                }else{
                    index = preindex[prepointer];
                    prepointer--;
                }
            } else if (state == 0 || state == 1 || state == 2){
                index--;
            }
            //isPause = false;
            position = 0;
            playing();
            //btnPlay.setText("Pause");
        }
        /*else
           index--;*/
        else if (index< 0){
            if (!isPause)
                btnPlay.setImageResource(R.mipmap.pause);
            if (isPause){
                btnPlay.setImageResource(R.mipmap.play);
                isChang = true;
                position = 0;
            }
            if (state == 3 || state == 4) {
                //如果隨機撥放存放上一首的陣列空了
                if (prepointer == -1 ){
                    prepointer = -1;
                    position = 0;
                    playing();
                }else{
                    index = preindex[prepointer];
                    prepointer--;
                }
            } else if (state == 0 || state == 1 || state == 2){
                index = songArray.size() - 1;
            }
            //isPause = false;
            position = 0;
            playing();
        }
        else if (index == 0){
            if (!isPause)
                btnPlay.setImageResource(R.mipmap.pause);
            if (isPause){
                btnPlay.setImageResource(R.mipmap.play);
                isChang = true;
                position = 0;
            }
            if (state == 3 || state == 4) {
                //如果隨機撥放存放上一首的陣列空了
                if (prepointer == -1 ){
                    prepointer = -1;
                    position = 0;
                    playing();
                }else{
                    index = preindex[prepointer];
                    prepointer--;
                }
            } else if (state == 0 || state == 1 || state == 2){
                index = songArray.size() - 1;
            }
            //isPause = false;
            position = 0;
            playing();
        }
        //這邊的else加不加都會有各自的問題
        /*else
            index--;*/
    }

    private void playing(){
        if (mediaPlayer != null && !isPause) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (mediaPlayer == null) {
            //long id = songList.get(index).getId();
            //Uri songUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            Log.d("songarraysize", "" + index);
            Map<String, String> songMap = songArray.get(index);
            if(songMap.get("path")!=null) {
                Uri songUri = Uri.parse(songMap.get("path"));
                mediaPlayer = MediaPlayer.create(this, songUri);
                mediaPlayer.setOnCompletionListener(this);
                Duration=mediaPlayer.getDuration();
                //音乐文件持续时间
                ProceseekBar.setMax(Duration);
                //设置SeekBar最大值为音乐文件持续时间
            }
        }
        //if(mediaPlayer!=null)
        if (isPause){
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
        if (!isPause) {
            mediaPlayer.start();
            if (isChang == false){
                mediaPlayer.seekTo(position);
            }
        }

            handler.post(updatesb);


        /*txtSongName.setText("曲目: " + songList.get(index).getTitle() +
                "\n專輯: " + songList.get(index).getAlbum() +
                "\n(" + (index + 1) + "/" + songList.size() + ")");*/
        txtSongName.setText("曲目: " + songArray.get(index).get("name") +
                //"\n專輯: " + songList.get(index).getAlbum() +
                "\n(" + (index + 1) + "/" + songArray.size() + ")");
        if(songArray.get(index).get("picture")!=null)
        {
            Bitmap songCoverArt = null;
            Uri songCover = Uri.parse("content://media/external/audio/albumart");
            Uri uriSongCover = ContentUris.withAppendedId(songCover, Integer.parseInt(songArray.get(index).get("picture")));
            try {
                InputStream in = getContentResolver().openInputStream(uriSongCover);
                songCoverArt = BitmapFactory.decodeStream(in);
            }catch (FileNotFoundException e){
                Log.e("error", e.getMessage());
                albumPicture.setImageResource(R.mipmap.songpicture);
            }
            if(songCoverArt!=null)
                albumPicture.setImageBitmap(Bitmap.createScaledBitmap(songCoverArt, albumPicture.getWidth(), albumPicture.getHeight(), false));//set album picture and it's size
        }
    }

    private void optionClick()
    {
        AlertDialog dialog = new AlertDialog.Builder(this,R.style.Dialog_Fullscreen)
                .setTitle(Html.fromHtml("<font color='#FFFFFF'>清單名</font>"))
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
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
        int titleId = getResources().getIdentifier("alertTitle", "id", "android");
        TextView dialogTitle = (TextView) dialog.findViewById(titleId);
        dialogTitle.setTextSize(30);
    }

    private void getMusics(){
        songList = new LinkedList<Song>();


        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            Log.d("=======>", "查詢錯誤");
        } else if (!cursor.moveToFirst()) {
            Log.d("=======>", "沒有媒體檔");
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.AudioColumns.ALBUM);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int pictureColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                String thisAlbum = cursor.getString(albumColumn);
                String thisPath =  cursor.getString(pathColumn);
                Log.d("=======>", "id: " + thisId + ", title: " + thisTitle);
                Song song = new Song();
                song.setId(thisId);
                song.setTitle(thisTitle);
                song.setAlbum(thisAlbum);

                songList.add(song);

                Map<String, String> nameMap = new HashMap<String, String>();
                nameMap.put("name", song.getTitle());
                nameMap.put("path",thisPath);
                String albumID = "";
                Cursor albumCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Media.ALBUM_ID},
                        MediaStore.Audio.Media.TITLE +"=?",
                        new String[]{song.getTitle()},null);
                if (albumCursor.moveToNext()) {
                    albumID = albumCursor.getString(0);
                    nameMap.put("picture",albumID);
                }
                albumCursor.close();
                songArray.add(nameMap);
            } while (cursor.moveToNext());
        }

        txtSongName.setText("共有 " + songList.size() + " 首歌曲");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //先查看Rand的狀態
        /*state數字代表狀態
                 0=歌單不循環(不隨機)
                 1=歌單循環(不隨機)
                 2=單曲循環
                 3=隨機不循環
                 4=隨機循環
                * */
        position = 0;
        isChang = false;
        if (state == 0 && !isPause){//目前狀態0
            //加入判斷使否為歌單中最後一首
            if (index == songArray.size()-1){
                isPause = true;
                btnPlay.setImageResource(R.mipmap.play);
                doStop();
            } else{
                doNext();
            }
        }

        else if (state == 1 && !isPause){
            doNext();
        }

        else if (state == 2 && !isPause){
            //doPlay();
            playing();
        }

        else if (state == 3 && !isPause){
            //加入判斷使否為歌單中最後一首
            if (index == songArray.size() - 1) {
                isPause = true;
                btnPlay.setImageResource(R.mipmap.play);
                doStop();
            } else{
                prepointer++;
                preindex[prepointer] = index;
                doRandPlay();
            }
        }

        else if (state == 4 && !isPause){
            prepointer++;
            preindex[prepointer] = index;
            doRandPlay();
        }
    }

   public void searchList()
   {
                if(listDataBase.getSpots(searchArea.getText().toString()).size()>0)
                {
                    Member tempmember = listDataBase.getSpots(searchArea.getText().toString()).get(0);
                    if(!tempmember.getName().equals(ListName.getText().toString()))//搜尋名稱為當前播放清單  則無動作
                    {
                        String musicContent = tempmember.getMusic();
                        if(!musicContent.equals("")){
                            songArray.clear();
                            String [] musicOrder = musicContent.split(",");
                            for(int i=0; i<musicOrder.length; i++)//id 從1開始
                            {
                                Music musicSpot = songDataBase.findById(Integer.parseInt(musicOrder[i]));
                                if(musicSpot!=null) {
                                    Map<String, String> nameMap = new HashMap<String, String>();
                                    nameMap.put("name", musicSpot.getName());
                                    nameMap.put("path", musicSpot.getPath());
                                    //取得專輯ID來當專輯圖片搜尋條件
                                    String albumID = "";
                                    Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                            new String[]{MediaStore.Audio.Media.ALBUM_ID},
                                            MediaStore.Audio.Media.TITLE +"=?",
                                            new String[]{musicSpot.getName()},null);
                                    if (cursor.moveToNext()) {
                                        albumID = cursor.getString(0);
                                        nameMap.put("picture",albumID);
                                    }
                                    songArray.add(nameMap);
                                }
                            }
                            index = 0;//index 回歸 避免outofrange
                            //playing();//切換清單不撥放
                            sadapter.notifyDataSetChanged();
                            txtSongName.setText("共有 " + songArray.size() + " 首歌曲");
                            if(songArray.size()>0)
                                ListName.setText(searchArea.getText());
                        }
                        else
                            Toast.makeText(this, "清單內無歌曲", Toast.LENGTH_SHORT).show();
                    }
                }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(layer1.getBackground()==null)
            layer1.setBackgroundResource(R.drawable.playbg);
        if(listDataBase.getSpots(searchArea.getText().toString()).size()>0)
        {
            Member tempmember = listDataBase.getSpots(searchArea.getText().toString()).get(0);
            String musicContent = tempmember.getMusic();
            if(!musicContent.equals("")){
                String [] musicOrder = musicContent.split(",");
                if(musicOrder.length>songArray.size())
                {
                    songArray.clear();
                    for(int i=0; i<musicOrder.length; i++)//id 從1開始
                    {
                        Music musicSpot = songDataBase.findById(Integer.parseInt(musicOrder[i]));
                        if(musicSpot!=null) {
                            Map<String, String> nameMap = new HashMap<String, String>();
                            nameMap.put("name", musicSpot.getName());
                            nameMap.put("path", musicSpot.getPath());
                            String albumID = "";
                            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    new String[]{MediaStore.Audio.Media.ALBUM_ID},
                                    MediaStore.Audio.Media.TITLE +"=?",
                                    new String[]{musicSpot.getName()},null);
                            if (cursor.moveToNext()) {
                                albumID = cursor.getString(0);
                                nameMap.put("picture",albumID);
                            }
                            songArray.add(nameMap);
                        }
                    }
                    sadapter.notifyDataSetChanged();
                    txtSongName.setText("共有 " + songArray.size() + " 首歌曲");
                }//歌曲有新增 才更新
            }
        }
    }
    //按兩次返回退出程式
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果两次按键时间间隔大于2000毫秒，则不退出
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程式", Toast.LENGTH_SHORT).show();
                // 更新mExitTime
                mExitTime = System.currentTimeMillis();
            } else {
                // 否则退出程序
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //播放進度條
    class ProcessBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            if (fromUser==true && !isPause) {
                mediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            if(!isPause)
                mediaPlayer.seekTo(seekBar.getProgress());

        }

    }

    //顯示時間,從毫秒轉換成00:00的格式
    public String ShowTime(int time){
        time/=1000;
        int minute=time/60;
        int minuteTen = minute/10;
        minute%=10;
        int hour=minute/60;
        int second=time-minute*60;
        int secondTen = second/10;
        second%=10;
        return String.format("%d%d:%d%d", minuteTen,minute, secondTen,second);
    }
    Runnable updatesb =new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            ProceseekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(updatesb, 1000);
            nowPlayTime.setText(ShowTime(mediaPlayer.getCurrentPosition()));
            //每秒更新一次
        }
    };//更新seekbar

    //自動建立捷徑
    private void addShortcut() {
        Intent shortcutIntent = new Intent(getApplicationContext(),
                MediaActivity.class); // 啟動捷徑入口，一般用MainActivity，有使用其他入口則填入相對名稱，ex:有使用SplashScreen
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent); // shortcutIntent送入
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                "AImozart"); // 捷徑app名稱
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(
                        getApplicationContext(),// 捷徑app圖
                        R.drawable.aim_logo));
        addIntent.putExtra("duplicate", false); // 只創建一次
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT"); // 安裝
        //getApplicationContext().sendBroadcast(addIntent); // 送出廣播
    }

    /*public void Notify(){
        final int notifyID = 1; // 通知的識別號碼
        final int priority = Notification.PRIORITY_MAX; // 通知的優先權，可用PRIORITY_MAX、PRIORITY_HIGHT、PRIORITY_LOW、PRIORITY_MIN、PRIORITY_DEFAULT

        final Intent intent = getIntent(); // 目前Activity的Intent
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, flags); // 取得PendingIntent

        final Intent cancelIntent = new Intent(getApplicationContext(), CancelNotificationReceiver.class); // 取消通知的的Intent
        cancelIntent.putExtra("cancel_notify_id", notifyID); // 傳入通知的識別號碼
        flags = PendingIntent.FLAG_ONE_SHOT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, cancelIntent, flags); // 取得PendingIntent

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.aim_logo).setContentTitle("內容標題").setContentText("內容文字").addAction(R.drawable.aim_logo, "開啟App", pendingIntent).addAction(android.R.drawable.ic_menu_close_clear_cancel, "關閉通知", pendingCancelIntent).build(); // 建立通知
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 將ongoing(持續)的flag添加到通知中
        notificationManager.notify(notifyID, notification); // 發送通知
    }*/

   /* @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Media Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.user.myapplicationlayouttest/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Media Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.user.myapplicationlayouttest/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }*/
   @Override
   public void onPause() {
       super.onPause();
       System.gc();
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

