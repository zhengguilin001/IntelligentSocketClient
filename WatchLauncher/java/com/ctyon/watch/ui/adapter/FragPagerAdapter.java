package com.ctyon.watch.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.ctyon.watch.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/25.
 */

public class FragPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List<Fragment> mList = new ArrayList<>();
    private FragmentManager manager;

    public FragPagerAdapter(FragmentManager manager){
        super(manager);
    }
    public FragPagerAdapter(FragmentManager manager, List<Fragment> list, Context context){
        super(manager);
        mList = list;
        this.manager = manager;
        mContext = context;
    }

    @Override
    public int getCount() {
        if(mList.isEmpty())
            return 0;
        return Short.MAX_VALUE;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemPosition(Object object)
    {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        //处理position。让数组下标落在[0,fragmentList.size)中，防止越界
        position = position % mList.size();
        return super.instantiateItem(container, position);
    }
}
