package com.aimozart.user.myapplicationlayouttest;

/**
 * Created by user on 2016/5/9.
 */
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewAdapter extends PagerAdapter{

    private List<View> viewList;
    private List<String> titles;//標題

    public ViewAdapter(List<View> viewList,List<String> titles){

        this.viewList = viewList;
        this.titles = titles;
    }

    public int getCount() {

        return viewList.size();
    }

    //view是否由目標產生，官方写arg0==arg1即可
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0==arg1;

    }

    //銷毀一個頁面(即ewPager的一個item)
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView(viewList.get(position));
    }

    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    //為對應的頁面設置標題
    public CharSequence getPageTitle(int position) {

        return titles.get(position);
    }

}