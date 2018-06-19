package com.ctyon.watch.ui.activity.gallery;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ctyon.watch.R;

import java.util.List;

/**
 * Description：ViewPager适配器
 * Author：lxl
 * Date： 2017/7/3 17:43
 */
public class ViewPagerAdapter extends PagerAdapter {
    private List<String>                 mPhotoList;
    private Context                      mContext;
    private OnViewPagerLongClickListener mListener;

    public void setOnViewPagerClickListener(OnViewPagerLongClickListener listener) {
        mListener = listener;
    }

    public ViewPagerAdapter(List<String> urls, Context context) {
        mPhotoList = urls;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        String urlid = mPhotoList.get(position);
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_main_item, container, false);
        final ImageView photoView = (ImageView) view.findViewById(R.id.img);
        Glide.with(view.getContext())
                .load(urlid)

                .into(photoView);

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.onLongClickListener(v, position);
                }
                return true;
            }
        });

        container.addView(view);
        return view;
    }


    public interface OnViewPagerLongClickListener {
        void onLongClickListener(View view, int position);

    }


}

