package com.aimozart.user.myapplicationlayouttest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by user on 2016/7/26.
 */
public class UseGuide extends Activity implements GestureDetector.OnGestureListener {
    String musicClassify =
            "點選匯入音樂之後，按下辨識按鈕開始音樂辨識\n" +
            "辨識完之後會顯示此首歌曲屬於哪類型的音樂\n" +
            "並同時將音樂放人此類型的預設清單\n" +
            "也可同時加入欲加入的自訂清單\n" +
            "如辨識的不只一首歌\n" +
            "可用滑動功能看所有辨識完的歌曲屬於何種類型\n" +
            "再加以加入哪些自訂清單。";
    String musicPlay =
            "點選某首歌曲開始播放音樂，可在下方的按鈕處調整\n" +
            "模式為:隨機循環播放、隨機不循環播放、循環不重複播放、\n" +
            "循環重複播放、單一歌曲重複播放\n" +
            "另外其餘按鈕可調整歌曲下一首、上一首或者暫停、開始播放\n" +
            "在音樂播放的播放清單中，可滑除此清單不想聽的歌曲\n" +
            "不會改變原音樂清單的歌曲，只改變播放當下的清單列表。";
    String musicList =
            "除原有的三種音樂類型的播放清單\n" +
            "可額外設定自定清單並加入歌曲\n" +
            "按列表中右邊的三點，顯示要新增、刪除或重新命名音樂清單\n" +
            "點入某清單後，一樣可點選歌曲右方的三點\n" +
            "選擇新增、刪除、移動歌曲至其他清單，或者點選歌曲進入音樂播放。";
//    String []helpArray = new String []{musicPlay,musicClassify,musicList};
    int []knowledge = new int[]{R.mipmap.kalpha,R.mipmap.kbeta,R.mipmap.ktheta};
    int []guideArray = new int[]{R.mipmap.guide_play,R.mipmap.guide_id,R.mipmap.guide_list};
    String []helpTitle = new String []{"「音樂播放」","「音樂辨識」","「音樂清單」"};
//    private TextView ht;
//    private TextView gt;
    private ImageButton cancelGuide;
    private ImageView img_guide;
    private GestureDetector detector;
    private int currentPosition = 0;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guidelayout);
        detector = new GestureDetector(this, this);
        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));

        Toast.makeText(this,"左右滑動閱讀不同指南",Toast.LENGTH_LONG).show();
        //提示左右滑動


//        ht = (TextView)findViewById(R.id.guideText);
//        gt = (TextView)findViewById(R.id.guideTitle);
        cancelGuide = (ImageButton)findViewById(R.id.returnHelp);
        img_guide = (ImageView)findViewById(R.id.waveknowledge);
        if(position==1) {
//            ht.setText(helpArray[currentPosition]);
//            gt.setText(helpTitle[currentPosition]);
           img_guide.setImageResource(guideArray[currentPosition]);
        }
        else if(position==2) {
//            ht.setVisibility(View.GONE);
//            gt.setVisibility(View.GONE);
            img_guide.setImageResource(knowledge[currentPosition]);
        }
        cancelGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        final int distance = 100;//滑动距离
        final int speed = 200;//滑动速度
        if(e1.getX() - e2.getX()>distance&&Math.abs(velocityX)>speed){
            currentPosition--;
            if(currentPosition<0)
                currentPosition = guideArray.length-1;

            if(position==1) {
//                ht.setText(helpArray[currentPosition]);
//                gt.setText(helpTitle[currentPosition]);
                img_guide.setImageResource(guideArray[currentPosition]);
            }
            else if(position==2)
                img_guide.setImageResource(knowledge[currentPosition]);
        }else if(e2.getX() - e1.getX()>distance&&Math.abs(velocityX)>speed){
            currentPosition++;
            if(currentPosition>=guideArray.length)
                currentPosition = 0;
            if(position==1) {
//                ht.setText(helpArray[currentPosition]);
//                gt.setText(helpTitle[currentPosition]);
                img_guide.setImageResource(guideArray[currentPosition]);
            }
            else if(position==2)
                img_guide.setImageResource(knowledge[currentPosition]);
        }
        return true;
    }
}
