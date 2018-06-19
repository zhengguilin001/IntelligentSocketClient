package com.ctyon.watch.ui.activity.calllogs;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog.Calls;

import com.ctyon.watch.model.CountCallLog;
import com.ctyon.watch.utils.ContactsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zx
 * On 2017/9/16
 */

public class CallLogManager {
    private static final String TAG = "CallLogManager";
    private Uri callLogUri = Calls.CONTENT_URI;
    private static CallLogManager instance;
    private Context mContext;
    private ContentResolver resolver;
    private static String[] CALLS_PROJECTION = new String[]{Calls.NUMBER, Calls.DATE, Calls.TYPE};
    private CallLogManager(Context context){
        mContext = context.getApplicationContext();
        resolver = mContext.getContentResolver();
    }

    public synchronized static CallLogManager getInstance(Context context){
        if (instance == null) {
            instance = new CallLogManager(context);
        }
        return instance;
    }

    /***
     * 查询所有通话记录
     * @return
     */
    public List<CountCallLog> queryCountCallLogs(){
        List<CountCallLog> countCallLogs = new ArrayList<>();
        Map<String,Integer> callLogCount = new HashMap<>();
        Cursor cursor = resolver.query(callLogUri, CALLS_PROJECTION,null,null, Calls.DEFAULT_SORT_ORDER);
        if (cursor != null){
            while (cursor.moveToNext()){
                /*for(int i=0; i<cursor.getColumnCount(); i++){
                    String columnName = cursor.getColumnName(i);
                    String value = cursor.getString(i);
                    Log.e("CallLogManager ",columnName+" : "+value);
                }
                Log.e("CallLogManager ","---------------------------------");*/
                String number = cursor.getString(cursor.getColumnIndex("number"));
                if (callLogCount.containsKey(number)){
                    callLogCount.put(number,callLogCount.get(number)+1);
                }else {
                    callLogCount.put(number,1);
                    CountCallLog countCallLog = new CountCallLog();
                    int type = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));
                    long date = cursor.getLong(cursor.getColumnIndex(Calls.DATE));
                    countCallLog.setNumber(number);
                    //String name = getName(number);
                    //countCallLog.setName(name);
                    countCallLog.setType(type);
                    countCallLog.setDate(ContactsUtil.parserDate(date,mContext));
                    countCallLogs.add(countCallLog);
                }
            }
            cursor.close();
        }
        for (CountCallLog countCallLog : countCallLogs) {
            countCallLog.setCount(callLogCount.get(countCallLog.getNumber()));
        }
        return countCallLogs;
    }

    /***
     * 删除指定联系人的通话记录
     * @param selectedLogs 选中的记录
     */
    public void deleteCallLogs(List<CountCallLog> selectedLogs){
        for (CountCallLog selectedLog : selectedLogs) {
            resolver.delete(callLogUri,"number=?",new String[]{selectedLog.getNumber()});
        }

    }
}
