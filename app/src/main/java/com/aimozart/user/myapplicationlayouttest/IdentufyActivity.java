package com.aimozart.user.myapplicationlayouttest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jAudioFeatureExtractor.jAudioTools.AudioSamples;
/**
 * Created by user on 2016/7/17.
 */
public class IdentufyActivity extends Activity implements OnGestureListener, ViewPager.OnPageChangeListener {

    double feature[] = new double[13];
    double tempfeature[] = new double[13];
    double s3[][] = new double[12][12];
    private AudioSamples as;
    private Sample sa;
    private Sample sa1;
    private FileInputStream file;
    private TextView mf;
    private int num = 0;
    private double featureDistance = 0;
    //private String path;
    private String filename;
    //view for start
    public static ImageButton detect;
    private ImageView addSong;
    private ListView Songlist;
    private ButtonAdapterID adapter;
    public static ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();//list detail
    private boolean isDetected = false;//判斷偵測中不能判斷
    //view for result
    private ImageButton continueDetect;
    private ImageButton share;
    private ImageButton addtoList;
    private ImageView detectImage;
    //private ListView detectSonglist;
    private ViewPager viewPager;
    private TextView MusicName;
    private ImageButton MusicState;
    private ImageButton Share;
    private ImageButton AddToList;
    private ImageView[] resultpic;//存結果image
    private LinearLayout DialogLayout;//加入清單listview 容器
    private int[] picid;//存結果image id
    public static boolean[] selectTolist;
    private int[] detectedID;//儲存辨識完歌曲的ID
    private int currentPosition;//紀錄當前選取歌曲編號
    ArrayList<HashMap<String,Object>> resultlist = new ArrayList<HashMap<String,Object>>();
    List<Member> nameList = new ArrayList<Member>();//儲存清單
    //svm variable
    String sd_card = Environment.getExternalStorageDirectory().toString();
    String path = sd_card;

    String train_path = path + "/Model/train.txt";
    String test_path = path + "/Model/test.txt";
    String output_path = path + "/Model/result.txt";
    String model_name = path + "/Model/my_model.model";
    private int musicType[];

    Decode mp3Decoder;

    private GestureDetector detector;
    private MySQLiteOpenHelper listDataBase;//list database
    private MySQLitePlayListOpenHelper songDataBase;

    private LinearLayout layer2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay2);
        layer2 = (LinearLayout)findViewById(R.id.layer2);
        layer2.setBackground(getLocalBitmap(this,R.drawable.idbg));
        if(listDataBase == null){
            listDataBase = new MySQLiteOpenHelper(this);
        }//開啟資料庫
        if(songDataBase ==null)
            songDataBase = new MySQLitePlayListOpenHelper(this);
        detector = new GestureDetector(this, this);

        //mp3Decoder= new Decode();
        //String tempname[] = new String[]{"alpha1"};
        File file = new File(path+"/Model");
        Log.d("isexist",""+file.exists());
        if(!file.exists())
            file.mkdir();
        //新建test.txt
        file = new File(test_path);
        if(!file.exists())
            try{
                file.createNewFile();}
            catch (Exception e){}
        //新建model
        file = new File(model_name);
        if(!file.exists())
            try{
                file.createNewFile();
                WriteToSDcard(model_name,getResources().openRawResource(R.raw.my_model));
            }
            catch (Exception e){}
        /*for(int i=0; i<tempname.length; i++)
        {
            HashMap<String,Object> song = new HashMap<String, Object>();
            song.put("name", tempname[i]);
            list.add(song);
        }*/
        init();//初始化view
        /*filename = "beta.wav";
        path = Environment.getExternalStorageDirectory().getPath();
        //file = getResources().openRawResource(R.raw.beta1);
        File input  = new File(path+"/Music/"+filename);
        try{
        file = new FileInputStream(input);
        }
        catch (Exception e){
            //Log.d("failed",e.toString());
        }
        mf = (TextView)findViewById(R.id.mfccfeature);
        if(file!=null)
        {
            //Log.d("fileexist",file.toString());
            sa = new Sample(file,handler);*/
            /*try{
            as = new AudioSamples(file,"1",false);}
            catch(Exception e)
            {Log.d("isuse",e.toString());}*/
        //}
        /*MFCC mfcc = new MFCC();
        try{
        //feature = mfcc.extractFeature(as.getSamplesMixedDown(),as.getSamplingRate(),s3);
            feature = mfcc.extractFeature(sa.samples,44100,s3);
            for(int i=0; i<12; i++)
            {
                Log.d("isuse"+i, ""+feature[i]);
            }
        }
        catch(Exception e){  Log.d("isuse", ""+e.toString()+" "+feature[0]); }*/
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String s = "1";
            if(msg.what==2)
            {

                featureDistance = 0;
                if(num>-1)
                {
                    for(int i=0; i<sa.avgFeature.length; i++)
                    {
                        //tempfeature[i] = sa.sumOfnormalFeatrue[i];
                        //featureDistance+=(tempfeature[i]-feature[i])*(tempfeature[i]-feature[i]);
                        s = s+" "+String.valueOf(i+1)+":"+String.valueOf(sa.sumOfnormalFeatrue[i]/sa.featureArray1.size());
                    }
                    s+="\r\n";
                    try{
                        FileWriter fw = new FileWriter(path+"/Model/test.txt", num>0);
                        // true 代表接在尾端  一開始為false 蓋掉前一次辨識data
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(s);
                        bw.close();
                    }
                    catch(Exception e){}
                    //Log.d("isused"+(num),""+featureDistance);
                    //s = s+"特徵"+String.valueOf(num)+": "+String.valueOf(featureDistance)+'\n';
                }
                else
                {
                    for(int i=0; i<sa.avgFeature.length; i++)
                        feature[i] = sa.sumOfnormalFeatrue[i];
                }
                //s = s+"特徵"+String.valueOf(i)+": "+String.valueOf(sa.avgFeature[i])+'\n';
                //s = s+"特徵權重"+String.valueOf(i)+": "+String.valueOf(sa.sumOfnormalFeatrue[i])+'\n';
//                adapter.buttonArray.get(num).setImageResource(R.drawable.isdetected);
                adapter.bgArray.get(num).setBackgroundResource(R.mipmap.tv_finish_bg);
                num++;
                if(num<list.size()) {
                    HashMap<String, Object> song = new HashMap<String, Object>();
                    song = list.get(num);
                    newSample((String) song.get("path"));
                }
                else
                {
                    svmPredict();
                    setContentView(R.layout.detectfinish);
                    Notify();//0803新增通知欄
                    initResult();
                }

            }
            //mf.setText(s);
        }
    };
    public void newSample(String music)
    {
        Log.d("numstatebt", "" + adapter.buttonArray.size());
        //if(adapter.buttonArray.get(num)!=null)
//        adapter.buttonArray.get(num).setImageResource(R.drawable.classifing);
        adapter.bgArray.get(num).setBackgroundResource(R.mipmap.tv_detecting_bg);
        //filename = music+".mp3";
        //File input  = new File(path+"/Music/"+filename);
        File input = new File(music);
        /*if(!input.exists())
        {
            filename = music+".wav";
            Log.d("filenotexist",""+filename);
            input = new File(path+"/Music/"+filename);
        }
        Log.d("filenotexist",""+input.exists());*/
        //InputStream is = new ByteArrayInputStream(mp3Decoder.decode(path+"/Music/"+filename));
        if(!input.exists())
            Toast.makeText(this,"格式不支援",Toast.LENGTH_SHORT);
        else
        {
            Log.d("fileexist",""+input.exists());
            try{
                file = new FileInputStream(input);}
            catch (Exception e){}
            sa = new Sample(file,handler);
        }
    }
    public void init()
    {
       /* String[] trainArgs = {train_path, model_name};
        final String[] testArgs = {test_path, model_name, output_path};
        svm_train train = new svm_train();
        final svm_predict predict = new svm_predict();*/
        isDetected = false;
        detect = (ImageButton)findViewById(R.id.detect);
        detect.setVisibility(View.VISIBLE);
        addSong = (ImageView)findViewById(R.id.detectimage);
        Songlist = (ListView)findViewById(R.id.detectlist);
        detectImage = (ImageView)findViewById(R.id.detectimage);
        detectImage.setImageResource(R.drawable.detectimage);
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > 0) {
                    isDetected = true;
                    HashMap<String, Object> song = new HashMap<String, Object>();
                    song = list.get(num);
                    newSample((String) song.get("path"));
                    //隱藏 detect button 並 增加listview 高度
                    detect.setVisibility(View.GONE);
                    for(int i=0; i<list.size(); i++)
                    {
                        adapter.buttonArray.get(i).setVisibility(View.GONE);
                        adapter.bgArray.get(i).setBackgroundResource(R.mipmap.tv_detect_bg);
                    }
                    //更換正在辨識背景
                    detectImage.setImageResource(R.mipmap.detecting);
                    /*long start_test_time = System.nanoTime();
                try{
                predict.main(testArgs);
                long end_test_time = System.nanoTime();
                long test_time = (end_test_time - start_test_time) / 1000000;//get milliseconds
                Log.d("usetime", String.valueOf(test_time) + "ms");}
                catch (IOException e) {
                    Log.d("exception",e.toString());
                }*/
                }
            }
        });
        addSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent destIntent = Intent.createChooser(intent, "選取歌曲");
                startActivityForResult(destIntent, 1);
                //開啟歌曲選取頁面  目前只能單選
            }
        });
        //adapter = new SimpleAdapter(this,list,R.layout.liststyle,new String[]{"name"},new int[]{R.id.SongName});
        adapter = new ButtonAdapterID(this,list,R.layout.liststyle,new String[]{"name","image"},new int[]{R.id.SongName,R.id.cancel,R.id.SongBg});
        Songlist.setAdapter(adapter);
        //更新list
    }
    public void initResult()
    {
        ReadFile();
        addToDefaultList();
        continueDetect = (ImageButton)findViewById(R.id.con_detect);
        share= (ImageButton)findViewById(R.id.share);
        addtoList= (ImageButton)findViewById(R.id.addtolist);
        //detectSonglist = (ListView)findViewById(R.id.detectlist);
        MusicName = (TextView)findViewById(R.id.MusicName);
        MusicState = (ImageButton)findViewById(R.id.MusicState);
        Share = (ImageButton)findViewById(R.id.share);
        AddToList = (ImageButton)findViewById(R.id.addtolist);
        ViewGroup group = (ViewGroup)findViewById(R.id.viewGroup);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        picid = new int[musicType.length];
        Log.d("musicTypesize",""+musicType.length);
        resultpic = new ImageView[picid.length];
        selectTolist = new boolean[picid.length];
        for(int i=0; i<resultpic.length; i++){
            if(musicType[i]==1)
                picid[i]=R.mipmap.alpha;
            else if(musicType[i]==2)
                picid[i]=R.mipmap.beta;
            else if(musicType[i]==3)
                picid[i] =R.mipmap.theta;
            ImageView imageView = new ImageView(this);
            resultpic[i] = imageView;
            imageView.setBackgroundResource(picid[i]);
        }
        //配置viewpager 滑動圖片
        viewPager.setAdapter(new MyAdapter());
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);//實現一開始左右滑動
        continueDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.lay2);
                layer2 = (LinearLayout)findViewById(R.id.layer2);
                layer2.setBackground(getLocalBitmap(IdentufyActivity.this, R.drawable.idbg));
                list.clear();
                adapter.notifyDataSetChanged();
                num = 0;
                init();
            }
        });
        MusicState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectTolist[currentPosition]) {
                    selectTolist[currentPosition] = false;
                    MusicState.setImageResource(R.mipmap.unckeck);
                }
                else{
                    selectTolist[currentPosition] = true;
                    MusicState.setImageResource(R.mipmap.check);
                }
            }
        });
        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "「分享」");
                startActivity(Intent.createChooser(intent, getTitle()));
            }
        });
        DialogLayout = new LinearLayout(this);
        DialogLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ListView playerlist = new ListView(this);
        //設定清單title
        nameList = listDataBase.getAllSpots();
        final String []nameArray = new String [nameList.size()];
        for(int i=0; i<nameArray.length; i++)
        {
            Member m = nameList.get(i);
            nameArray[i]  = m.getName();
        }
        ArrayAdapter<String> sadapter = new ArrayAdapter<String>(this,R.layout.playerlist,R.id.playerListSongName,nameArray);
        playerlist.setAdapter(sadapter);
        playerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i=0; i<selectTolist.length; i++)
                    if(selectTolist[i]==true)
                        addSongToList(nameArray[position],i);
                Toast.makeText(IdentufyActivity.this, "已將歌曲加入" + nameArray[position] + "清單", Toast.LENGTH_SHORT).show();
            }
        });
        DialogLayout.addView(playerlist);
        AddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addListDialog();
            }
        });
        //ButtonAdapterID badapter = new ButtonAdapterID(this,resultlist,R.layout.liststyle,new String[]{"name","image"},new int[]{R.id.SongName,R.id.cancel});
        //adapter = new SimpleAdapter(this,list,R.layout.liststyle,new String[]{"name"},new int[]{R.id.SongName});
        //detectSonglist.setAdapter(badapter);
    }
    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data)
    {
        super.onActivityResult(requestcode, resultcode, data);
        if(requestcode == 1 && resultcode == RESULT_OK && null != data)
        {

            Uri uri = data.getData();
            //get 選取歌曲路徑
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            cursor.moveToFirst();//需先將cursor 歸位
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(pathColumn);
            String name = cursor.getString(titleColumn);
            cursor.close();
            //Log.d("titlecolumn", "" + name);
            //get song name
            HashMap<String,Object> song = new HashMap<String, Object>();
            song.put("name",name);
            song.put("path",path);
            list.add(song);
            resultlist.add(song);
            //song name 加入list
            adapter.notifyDataSetChanged();
            //adapter = new SimpleAdapter(this,list,R.layout.liststyle,new String[]{"name"},new int[]{R.id.SongName});
            //Songlist.setAdapter(adapter);
            //更新list
        }
    }//ActivityForResult動作

    public void svmPredict()
    {
        String[] trainArgs = {train_path, model_name};
        final String[] testArgs = {test_path, model_name, output_path};
        svm_train train = new svm_train();
        final svm_predict predict = new svm_predict();
        long start_test_time = System.nanoTime();
        try{
            predict.main(testArgs);
            long end_test_time = System.nanoTime();
            long test_time = (end_test_time - start_test_time) / 1000000;//get milliseconds
            Log.d("usetime", String.valueOf(test_time) + "ms");}
        catch (IOException e) {
            Log.d("exception", e.toString());
        }
    }
    public void ReadFile()
    {
        musicType = new int[list.size()];
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(output_path), "UTF-8")); // 指定讀取文件的編碼格式，以免出現中文亂碼

            String str = null;
            int i = 0;
            while ((str = reader.readLine()) != null) {
                musicType[i] = Integer.parseInt(""+str.charAt(0));
                i++;
            }
        } catch (IOException e) {

            e.printStackTrace();

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //viewpager 配置
    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return resultpic.length;//設定邊界
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object)   {
            container.removeView((View) object);
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            try{
                container.addView(resultpic[position]);}
            catch(Exception e){
            }
            return resultpic[position % resultpic.length];
        }
    }
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int position, float arg1, int arg2) {
        currentPosition = position;
        HashMap<String, Object> song = list.get(position);
        MusicName.setText((String) song.get("name"));
        //Log.d("position", "" + (String)song.get("name")+ " "+position);
        if(selectTolist[position])
            MusicState.setImageResource(R.mipmap.check);
        else
            MusicState.setImageResource(R.mipmap.unckeck);
    }

    @Override
    public void onPageSelected(int arg0) {

    }
    public File WriteToSDcard(String path,InputStream inputStream){
        //写入数据到SDCard中
        File file=null;;
        OutputStream outputStream=null;
        try{
            file= new File(path);
            outputStream = new FileOutputStream(file);
            byte[] buffer=new byte[10*1024];//设置一个大小为4K的数组作为缓存
            while(inputStream.read(buffer)!=-1){
                outputStream.write(buffer);
            }
            outputStream.flush();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    public void addListDialog()
    {
        AlertDialog dialog = new AlertDialog.Builder(this,R.style.Dialog_Fullscreen)
                .setTitle(Html.fromHtml("<font color='#FFFFFF'>加入清單</font>"))
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
        //dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.show();
        //set dialog title style
        // need to use after dialog.shoe()
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        int titleId = getResources().getIdentifier("alertTitle", "id", "android");
        TextView dialogTitle = (TextView) dialog.findViewById(titleId);
        dialogTitle.setTextSize(30);
    }
    public void addToDefaultList()
    {
        detectedID = new int[list.size()];
        for(int i=0; i<list.size(); i++) {
            HashMap<String, Object> song = list.get(i);
            Music spot1 = new Music((String) song.get("name"), "file://" + (String) song.get("path"));
            spot1.setType("" + musicType[i]);
            long id;//
            Music tempspot;
            if (songDataBase.getSpots(spot1.getName()).size() > 0) {
                tempspot = songDataBase.getSpots(spot1.getName()).get(0);
                id = tempspot.getId();
            } else {
                id = songDataBase.insert(spot1);
            }
            detectedID[i] = (int)id;
            if(musicType[i]==1)
                addSongToList("α-alpha",i);
            else if(musicType[i]==2)
                addSongToList("β-beta",i);
            else if(musicType[i]==3)
                addSongToList("θ-theta",i);
        }
        //若歌曲庫內無重複歌名 則新增
    }
    public void addSongToList(String listName, int i)
    {
        Member tempmember = listDataBase.getSpots(listName).get(0);
        String musicContent = tempmember.getMusic();
        if(musicContent.equals(""))
            musicContent = ""+detectedID[i];
        else
        {
            String checkid[] = musicContent.split(",");
            boolean checkflag = true;
            for(int j=0; j<checkid.length; j++)
            {
                if(Integer.parseInt(checkid[j])==detectedID[i])
                {
                    checkflag = false;
                    break;
                }
            }//檢查是否有重複id
            if(checkflag)
                musicContent+=","+detectedID[i];
        }
        //檢查list content 是否有重複歌曲
        tempmember.setMusic(musicContent);
        listDataBase.update(tempmember);
        //取得清單歌曲內容  並加上新增歌曲id(用,分隔)
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
        if(!isDetected) {
            final int distance = 100;//滑动距离
            final int speed = 200;//滑动速度
            if (e1.getX() - e2.getX() > distance && Math.abs(velocityX) > speed) {//左滑
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                // 設置切換動畫，從右邊進入，左邊退出
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                return true;
            } else if (e2.getX() - e1.getX() > distance && Math.abs(velocityX) > speed) {//右滑
                Intent intent = new Intent(this, MediaActivity.class);
                startActivity(intent);
                // 設置切換動畫，從左邊進入，右邊退出
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                return true;
            } else
                return false;
        }
        return false;
    }

    public void Page1btn(View view) {
        Intent intent = new Intent(this, MediaActivity.class);
        startActivity(intent);
        // 設置切換動畫，從左邊進入，右邊退出
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
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

    //0803新增通知欄
    public void Notify(){
        final int notifyID = 1; // 通知的識別號碼
        final boolean autoCancel = true; // 點擊通知後是否要自動移除掉通知

        final int requestCode = notifyID; // PendingIntent的Request Code
        final Intent intent = getIntent(); // 目前Activity的Intent
        final int flags = PendingIntent.FLAG_CANCEL_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, flags); // 取得PendingIntent
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.aim_logo).setContentTitle("辨識完成").setContentText("辨識完成").setContentIntent(pendingIntent).setAutoCancel(autoCancel).build(); // 建立通知
        notificationManager.notify(notifyID, notification); // 發送通知

    }
    @Override
    public void onPause() {
        super.onPause();
        //System.gc();
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
    //壓縮drawable 圖片  避免OOM

}

