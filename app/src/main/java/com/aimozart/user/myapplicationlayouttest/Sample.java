package com.aimozart.user.myapplicationlayouttest;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.oc.ocvolume.dsp.featureExtraction;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/4/26.
 */
public class Sample{
    private int audioFrequency;
    private int audioChannel; // 8k or 16k
    private int audioFormat; // Mono or STEREO
    private int audioTrackSize;// 288000/16000=18(s) 每一個TrackSize容納18秒
    private int blockSize;
    private boolean started = true;
    public short samples[];
    private double db;
    private double maxFeature[] = new double[13];
    private double minFeature[] = new double[13];
    public double sumOfnormalFeatrue[] = new double[13];
    private double Entropy[] = new double[13];
    public double featureWeight[] = new double[13];
    private double sumOfFeatureWeight = 0;
    public List<Double> featureArray1 = new ArrayList<Double>();
    private List<Double> featureArray2 = new ArrayList<Double>();
    private List<Double> featureArray3 = new ArrayList<Double>();
    private List<Double> featureArray4 = new ArrayList<Double>();
    private List<Double> featureArray5 = new ArrayList<Double>();
    private List<Double> featureArray6 = new ArrayList<Double>();
    private List<Double> featureArray7 = new ArrayList<Double>();
    private List<Double> featureArray8 = new ArrayList<Double>();
    private List<Double> featureArray9 = new ArrayList<Double>();
    private List<Double> featureArray10 = new ArrayList<Double>();
    private List<Double> featureArray11 = new ArrayList<Double>();
    private List<Double> featureArray12 = new ArrayList<Double>();
    private List<Double> featureArray13 = new ArrayList<Double>();
    double feature[][] = new double[13][13];
    double avgFeature[] = new double[13];
    int framenum = 2;//總frame數
    int startframe = 0;//db>20的frame
    AudioTrack mAudioTrack; // 8bit or 16bit
    InputStream file;
    Handler handler;
    public Sample(InputStream file,Handler handler)
    {
        this.file = file;
        this.handler = handler;
        audioFrequency = 16000;
        audioChannel = AudioFormat.CHANNEL_OUT_MONO;
        audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        audioTrackSize=288000;
        blockSize = 512;
        RecordAudioTask ra = new RecordAudioTask();
        ra.execute();
        for(int i=0; i<13; i++)
        {
            maxFeature[i] =-10000;
            minFeature[i] = 10000;
        }//最大最小initialize
    }

    private class RecordAudioTask extends AsyncTask<Void, double[], Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                int bufferSize = AudioTrack.getMinBufferSize(audioFrequency,
                        audioChannel, audioFormat);
                Log.v("bufSize", String.valueOf(bufferSize));
                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, audioFrequency,
                        audioChannel, audioFormat, audioTrackSize, AudioTrack.MODE_STATIC);
                byte[] audioBuffer = new byte[blockSize*2];
                samples = new short[blockSize];
                DataInputStream din = null;
                try{
                    din = new DataInputStream(file);
                }
                catch(Exception e){Log.d("isuese",e.toString());}


                while (started) {
                    int result = din.read(audioBuffer, 0, blockSize*2);//結尾返回-1，其餘返回array大小
                    if(result==-1)
                        started = false;
                    for (int i = 0; i < blockSize && result>i; i++) {
                        samples[i] = getShort(audioBuffer[i*2],audioBuffer[i*2+1]);
                    }
                    db = computeDb(samples);
                    showFeature();//算mfcc
                }
            } catch (Throwable t) {
                Log.e("AudioRecord", t.toString());}

                for(int j=0; j<featureArray1.size(); j++)
                {
                    featureArray1.set(j, normalizeFeature(featureArray1.get(j), 0));
                    featureArray2.set(j,normalizeFeature(featureArray2.get(j),1));
                    featureArray3.set(j,normalizeFeature(featureArray3.get(j),2));
                    featureArray4.set(j,normalizeFeature(featureArray4.get(j),3));
                    featureArray5.set(j,normalizeFeature(featureArray5.get(j),4));
                    featureArray6.set(j,normalizeFeature(featureArray6.get(j),5));
                    featureArray7.set(j,normalizeFeature(featureArray7.get(j),6));
                    featureArray8.set(j,normalizeFeature(featureArray8.get(j),7));
                    featureArray9.set(j,normalizeFeature(featureArray9.get(j),8));
                    featureArray10.set(j,normalizeFeature(featureArray10.get(j),9));
                    featureArray11.set(j,normalizeFeature(featureArray11.get(j),10));
                    featureArray12.set(j,normalizeFeature(featureArray12.get(j),11));
                    featureArray13.set(j,normalizeFeature(featureArray13.get(j),12));
                }//對所有feature正規化  並寫入 featrue中
                /*for(int j=0; j<featureArray1.size(); j++)
                {
                    computeEntropy(featureArray1.get(j), 0);
                    computeEntropy(featureArray2.get(j), 1);
                    computeEntropy(featureArray3.get(j), 2);
                    computeEntropy(featureArray4.get(j), 3);
                    computeEntropy(featureArray5.get(j), 4);
                    computeEntropy(featureArray6.get(j), 5);
                    computeEntropy(featureArray7.get(j), 6);
                    computeEntropy(featureArray8.get(j), 7);
                    computeEntropy(featureArray9.get(j), 8);
                    computeEntropy(featureArray10.get(j), 9);
                    computeEntropy(featureArray11.get(j), 10);
                    computeEntropy(featureArray12.get(j), 11);
                    computeEntropy(featureArray13.get(j), 12);
                }//所有維度取熵值
                for(int i=0; i<13; i++)
                {
                   sumOfFeatureWeight+=1+(i+1)*featureWeight[i];
                   sumOfnormalFeatrue[i]/=(double)framenum;
                }
                for(int i=0; i<13; i++)
                {
                   featureWeight[i] = computeWeight(i);
                }//計算所有維度權重*/
                Message msg= handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            for(int i=0; i<13; i++)
            {
                //Log.d("isuse"+(i+1),""+avgFeature[i]);
                Log.d("isuse"+(i+1),""+sumOfnormalFeatrue[i]/featureArray1.size());
            }
            Log.d("isuse"," ");
            return null;
        }

        @Override
        protected void onProgressUpdate(double[]... values) {


        }
    }
    private short getShort(byte argB1, byte argB2) {
        return (short) (argB1 | (argB2 << 8));}

    public void showFeature()
    {
        featureExtraction ft = new featureExtraction();
        try{
            feature = ft.process(samples,44100);
            if(feature[0][0]!=-1150.0 && db>=20 && framenum>2)
            {
                for(int i=0; i<13; i++)
                {
                    double temp = 0.0d;
                    for(int j=0; j<feature.length; j++)
                    {
                        temp+=feature[j][i];
                        if(feature[j][i]>maxFeature[i])
                            maxFeature[i] = feature[j][i];//紀錄特徵i最大值
                        if(feature[j][i]<minFeature[i])
                            minFeature[i] = feature[j][i];//紀錄特徵i最小值
                        //作正規化用
                        pushFeature(i,j);
                    }
                    if(avgFeature[i]==0)
                    {
                        avgFeature[i] += temp/ feature.length;
                        startframe = 2;
                    }
                    else
                        avgFeature[i] += (temp/ feature.length-avgFeature[i])/startframe;//取該特徵的平均
                    //feature[1][i]= 0;
                }
                startframe+=2;
            }
            //Log.d("isuse"+framenum, ""+avgFeature[0] + " "+feature[0][0]+" "+feature[1][0]+" "+db);
            framenum+=2;
        }
        catch(Exception e){  Log.d("isuse", ""+e.toString()+" "+feature[0]); }
    }
    public double computeDb(short buffer[])
    {
        long v = 0;
        int r = buffer.length;
        for (int i = 0; i < buffer.length; i++) {
            v += buffer[i] * buffer[i];
        }
        // 平方和除以数据总长度，得到音量大小。
        double mean = v / (double) r;
        double volume = 10 * Math.log10(mean);

        return volume;
    }
    public void pushFeature(int i, int j)
    {
        switch (i)
        {
            case 0:
                featureArray1.add(feature[j][i]);
                break;
            case 1:
                featureArray2.add(feature[j][i]);
                break;
            case 2:
                featureArray3.add(feature[j][i]);
                break;
            case 3:
                featureArray4.add(feature[j][i]);
                break;
            case 4:
                featureArray5.add(feature[j][i]);
                break;
            case 5:
                featureArray6.add(feature[j][i]);
                break;
            case 6:
                featureArray7.add(feature[j][i]);
                break;
            case 7:
                featureArray8.add(feature[j][i]);
                break;
            case 8:
                featureArray9.add(feature[j][i]);
                break;
            case 9:
                featureArray10.add(feature[j][i]);
                break;
            case 10:
                featureArray11.add(feature[j][i]);
                break;
            case 11:
                featureArray12.add(feature[j][i]);
                break;
            case 12:
                featureArray13.add(feature[j][i]);
                break;
        }
    }
    public double normalizeFeature(double f, int i)
    {
        double noramlFeature = (maxFeature[i]-f)/(maxFeature[i]-minFeature[i]);
        sumOfnormalFeatrue[i]+=noramlFeature;//計算noramlizeFeature總和
        return noramlFeature;
    }//Feature正規化 f為feature i為特徵值維度
    public void computeEntropy(double f,int i)
    {
       double Entropy = f/sumOfnormalFeatrue[i];
        if(Entropy!=0)//防log(0)==NaN
           featureWeight[i]+=Entropy*Math.log(Entropy);//計算noramlizeFeature總和
        //Log.d("isuse"+(i+1),""+featureWeight[i]);
    }//計算熵值 f為feature i為特徵值維度
    public double computeWeight(int i)
    {
        double weight;
        weight = (1+(i+1)*featureWeight[i])/sumOfFeatureWeight;
        return  weight;
    }//計算所有維度feature 權重(貢獻度)
}
