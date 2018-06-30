package com.ctyon.watch.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ctyon.watch.manager.AlarmManager;
import com.ctyon.watch.model.AlarmModel;
import com.ctyon.watch.service.StealCallService;
import com.ctyon.watch.utils.AlarmManagerUtil;
import com.ctyon.watch.utils.DateUtil;
import com.ctyon.watch.utils.LogUtils;

import java.util.List;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

//add by shipeixian on 2018/05/07 begin
import com.ctyon.watch.ui.activity.contact.ContactsManager;
import android.content.SharedPreferences;
import android.net.Uri;
//add by shipeixian on 2018/05/07 end

/**
 * Created by Administrator on 2018/1/29.
 */

public class ReBootReceiver extends BroadcastReceiver {
   
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() == ACTION_BOOT_COMPLETED){
            context.startService(new Intent(context, StealCallService.class));
        } /*else if (intent.getAction().equals("com.ctyon.shawn.ADD_CONTACT")) {
            //add by shipeixian on 2018/05/07 begin
            ContactsManager contactsManager = ContactsManager.getInstance(context);
            //String contactName = intent.getStringExtra("contact_name");
            //String contactNumber = intent.getStringExtra("contact_number");
            //if (contactsManager.insertContact(contactName, contactNumber)) {
            	//android.util.Log.d("shipeixian", "添加号码成功：" + contactNumber);
            //}

            String contactStr = intent.getStringExtra("contacts");
            //旧的联系人,一顿删
            contactsManager.deleteAllContacts();
            //新的联系人,一顿加
            if (!android.text.TextUtils.isEmpty(contactStr)) {
                String[] splitOldContactStr = contactStr.split("/");
                if (splitOldContactStr.length > 0) {
                    for (String item : splitOldContactStr) {
                        String[] contacts = item.split(",");
                        if (contactsManager.insertContact(contacts[0], contacts[1])) {
            	            android.util.Log.d("shipeixian", "添加号码成功：" + contacts[1]);
                        }
                    }
                }
            }
           
            //add by shipeixian on 2018/05/07 end
        }*/ else if("com.ctyon.shawn.SMS.SENT".equals(intent.getAction())){
                android.util.Log.d("---SHIPEIXIAN---", "短信发送成功"+System.currentTimeMillis());
        } else if("com.ctyon.shawn.SMS.DELIVERY".equals(intent.getAction())){
                android.util.Log.i("---SHIPEIXIAN---", "对方已经收到短信"+System.currentTimeMillis());
        }
    }
}
