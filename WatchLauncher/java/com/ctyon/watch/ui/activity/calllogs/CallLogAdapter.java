package com.ctyon.watch.ui.activity.calllogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ctyon.watch.R;
import com.ctyon.watch.model.CountCallLog;

import java.util.List;

/**
 * Created by zx
 * On 2017/9/16
 */

public class CallLogAdapter extends BaseAdapter {
    private Context mContext;
    private List<CountCallLog> callLogs;

    public CallLogAdapter(Context mContext, List<CountCallLog> callLogs) {
        this.mContext = mContext;
        this.callLogs = callLogs;
    }

    public void updateListView(List<CountCallLog> callLogs){
        this.callLogs = callLogs;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return callLogs.size();
    }

    @Override
    public Object getItem(int i) {
        return callLogs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_call_log, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String number = callLogs.get(i).getNumber();
        String date = callLogs.get(i).getDate();
        String name = callLogs.get(i).getName();
        int count = callLogs.get(i).getCount();
        int type = callLogs.get(i).getType();
        if (name != null){
            viewHolder.tvNumber.setText(name);
        }else {
            viewHolder.tvNumber.setText(number);
        }
        viewHolder.tvDate.setText(date);
        if (count>1){
            viewHolder.tvCount.setText("("+count+")");
        }else {
            viewHolder.tvCount.setText("");
        }
        switch (type){
            case 1:
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.incall);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                viewHolder.tvCount.setCompoundDrawables(drawable,null,null,null);
                break;
            case 2:
                Drawable drawable2 = mContext.getResources().getDrawable(R.mipmap.outcall);
                drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
                viewHolder.tvCount.setCompoundDrawables(drawable2,null,null,null);
                break;
            default:
            Drawable drawable3 = mContext.getResources().getDrawable(R.mipmap.unanswer);
            drawable3.setBounds(0, 0, drawable3.getIntrinsicWidth(), drawable3.getIntrinsicHeight());
            viewHolder.tvCount.setCompoundDrawables(drawable3,null,null,null);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tvNumber;
        TextView tvCount;
        TextView tvDate;

        ViewHolder(View view) {
            tvNumber = (TextView)view.findViewById(R.id.tvNumber);
            tvCount= (TextView)view.findViewById(R.id.tvCount);
            tvDate = (TextView)view.findViewById(R.id.tvDate);
        }
    }
}
