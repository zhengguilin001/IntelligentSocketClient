package com.ctyon.watch.ui.activity.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ctyon.watch.R;
import java.util.List;

/**
 * Created by shipeixian on 18-4-27.
 */

public class PhotoAdapter extends BaseAdapter {

    private List<String> photoNameList;
    private Context context;

    public PhotoAdapter(Context context, List<String> photoNameList) {
        this.context = context;
        this.photoNameList = photoNameList;
    }

    @Override
    public int getCount() {
        return photoNameList.size();
    }

    @Override
    public Object getItem(int i) {
        return photoNameList.size() > 0 ? photoNameList.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_item_photo, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String[] strArrays = photoNameList.get(i).split("/");
        String name = strArrays[strArrays.length - 1];
        String realName = name.substring(name.length() - 8,name.length());
        viewHolder.photoName.setText(realName);
        return view;
    }

    static class ViewHolder {
        TextView photoName;

        public ViewHolder(View view) {
            photoName = (TextView) view.findViewById(R.id.photoName);
        }
    }
}
