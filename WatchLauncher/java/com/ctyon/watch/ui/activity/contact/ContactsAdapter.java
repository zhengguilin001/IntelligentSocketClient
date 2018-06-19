package com.ctyon.watch.ui.activity.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ctyon.watch.R;
import com.ctyon.watch.model.ContactEntity;

import java.util.List;

/**
 * Created by zx
 * On 2017/9/8
 */

public class ContactsAdapter extends BaseAdapter {

    private Context mContext;
    private List<ContactEntity> contacts;

    public ContactsAdapter(Context mContext, List<ContactEntity> contacts) {
        this.mContext = mContext;
        this.contacts = contacts;
    }

    public void updateListView(List<ContactEntity> contacts){
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String name = contacts.get(i).getName();
        String lastName;
        viewHolder.tvName.setText(name);
        lastName = name.charAt(name.length()-1)+"";
        viewHolder.tvLastName.setText(lastName);
        int type = (i+1)%3;
        switch (type){
            case 1:
                viewHolder.tvLastName.setBackgroundResource(R.drawable.shape_yellow_round);
                break;
            case 2:
                viewHolder.tvLastName.setBackgroundResource(R.drawable.shape_green_round);
                break;
            case 0:
                viewHolder.tvLastName.setBackgroundResource(R.drawable.shape_blue_round);
                break;
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tvLastName;
        TextView tvName;

        ViewHolder(View view) {
            tvName = (TextView)view.findViewById(R.id.tv_name);
            tvLastName = (TextView)view.findViewById(R.id.tv_last_name);
        }
    }
}
