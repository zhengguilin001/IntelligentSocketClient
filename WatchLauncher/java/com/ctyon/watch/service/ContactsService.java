package com.ctyon.watch.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.ctyon.watch.ui.activity.contact.ContactsManager;
import android.os.Message;

import android.provider.ContactsContract;
import java.util.ArrayList;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.database.Cursor;

public class ContactsService extends Service {

    private boolean isOnStartCall = false;
    
    @Override
    public IBinder onBind(Intent intent) {
       
        return null;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 4444) {
                stopSelf();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d("shipeixian", "ContactsService onCreate");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        android.util.Log.d("shipeixian", "ContactsService onStartCommand");
        if(isOnStartCall) {
              android.util.Log.d("shipeixian", "ContactsService onStartCommand again return");
              return START_NOT_STICKY;
        }
        isOnStartCall = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    android.util.Log.d("shipeixian", "service 处理添加联系人逻辑");
                    ContactsManager contactsManager = ContactsManager.getInstance(getApplicationContext());
                    String contactStr = intent.getStringExtra("contacts");
                    //旧的联系人,一顿删
                    contactsManager.deleteAllContacts();
                    Thread.sleep(3000);
                    //新的联系人,一顿加
                    if (!android.text.TextUtils.isEmpty(contactStr)) {
                        String[] splitOldContactStr = contactStr.split("/");
                        if (splitOldContactStr.length > 0) {
                            batchAddContact(getApplicationContext(), splitOldContactStr);
                            /*for (String item : splitOldContactStr) {
                                String[] contacts = item.split(",");
                                if (contactsManager.insertContact(contacts[0], contacts[1])) {
                                    android.util.Log.d("shipeixian", "添加号码成功：" + contacts[1]);
                                }
                            }*/
                        }
                    } else {
                        android.util.Log.d("shipeixian", "添加联系人字符串为空");
                    }
                } catch(Exception e) {
                       android.util.Log.d("shipeixian", "添加联系人失败");
                } finally {
                    handler.sendEmptyMessage(4444);
                }
            }
        }).start();
        
        return START_NOT_STICKY;
    }

    @Override
    public void onStart(final Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.util.Log.d("shipeixian", "ContactsService onDestroy");
    }

    /**
     * 批量添加通讯录
     *
     */
    public void batchAddContact(Context context, String[] splitOldContactStr) {
        android.util.Log.i("shipeixian","批量导入 begin");
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            int rawContactInsertIndex = 0;
            for (String item : splitOldContactStr) {
                String[] contacts = item.split(",");

                rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .withYieldAllowed(true).build());

                // 添加姓名
                ops.add(ContentProviderOperation
                        .newInsert(
                                android.provider.ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contacts[0])
                        .withYieldAllowed(true).build());
                // 添加号码
                ops.add(ContentProviderOperation
                        .newInsert(
                                android.provider.ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contacts[1])
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "").withYieldAllowed(true).build());
            }
            if (ops != null) {
                // 真正添加
                ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            }

        } catch (Exception e) {
            android.util.Log.i("shipeixian","批量导入联系人："+e);
        }
        android.util.Log.i("shipeixian","批量导入 end");
    }

    public boolean isContactExist(String name, String num, Context context) {
        Cursor cursorOriginal =
                context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                        ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + num + "'" + " and " + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "='" + name + "'", null, null);
        if (null != cursorOriginal) {
            if (cursorOriginal.getCount() > 1) {
                return false;
            } else {
                if (cursorOriginal.moveToFirst()) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
