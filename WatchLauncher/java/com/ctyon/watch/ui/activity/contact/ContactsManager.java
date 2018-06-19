package com.ctyon.watch.ui.activity.contact;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import android.provider.CallLog.Calls;
import com.ctyon.watch.R;
import com.ctyon.watch.model.CallLog;
import com.ctyon.watch.model.ContactEntity;
import com.ctyon.watch.utils.ContactsUtil;
import com.ctyon.watch.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by zx
 * On 2017/9/12
 */

public class ContactsManager {
    private Context mContext;
    private static ContactsManager instance;
    private final ContentResolver resolver;
    private static final Uri simUri = Uri.parse("content://icc/adn");
    //所有联系人
    private static final String ALL_CONTACTS_SELECTION = RawContacts.DELETED + "=0";
    //手机联系人
    private static final String PHONE_CONTACTS_SELECTION = RawContacts.DELETED +"=?"+" and "+ RawContacts.ACCOUNT_TYPE + "=?";
    private static final String[] PHONE_CONTACTS_ARGS = new String[]{"0","com.android.localphone"};
    //SIM卡联系人
    private static final String SIM_CONTACTS_SELECTION = RawContacts.DELETED +"=?"+" AND "+ RawContacts.ACCOUNT_TYPE+"=?";
    private static final String[] SIM_CONTACTS_ARGS = new String[]{"0","com.android.sim"};

    private ContactsManager(Context context) {
        mContext = context.getApplicationContext();
        resolver = mContext.getContentResolver();
    }

    public synchronized static ContactsManager getInstance(Context context) {
        if (instance == null) {
            instance = new ContactsManager(context);
        }
        return instance;
    }

    /***
     * 查询所有联系人
     * 手机联系人  RawContacts.ACCOUNT_NAME = PHONE; RawContacts.ACCOUNT_TYPE = com.android.localphone;
     * sim卡联系人(单卡) RawContacts.ACCOUNT_NAME = SIM; RawContacts.ACCOUNT_TYPE = com.android.sim
     * sim卡联系人(双卡中的sim卡1) RawContacts.ACCOUNT_NAME = SIM1; RawContacts.ACCOUNT_TYPE = com.android.sim
     * RawContacts.DELETED + " = 0 " 过滤已删除联系人
     * raw_contacts表会保存手机联系人和SIM卡联系人，
     * 其中SIM卡联系人通过Uri.parse("content://icc/adn")(单卡)或Uri.parse("content://icc/adn/subId/0")(双卡中的sim卡1)获得
     * @return 所有联系人
     */
    /*public List<ContactEntity> queryAllContacts() {
        List<ContactEntity> contacts = new ArrayList<>();
        //查询raw_contacts表中所有联系人的_id  根据sort_key排序
        String selection;
        String [] args;
        if (ContactsUtil.hasSimCard(mContext)){
            //手机联系人和SIM卡联系人
             selection = ALL_CONTACTS_SELECTION;
             args = null;
        }else {
            //手机联系人
             selection = PHONE_CONTACTS_SELECTION;
             args = PHONE_CONTACTS_ARGS;
        }
        Cursor cursor = resolver.query(RawContacts.CONTENT_URI,
                new String[]{RawContacts._ID,RawContacts.DISPLAY_NAME_PRIMARY,RawContacts.ACCOUNT_TYPE},
                selection,args,RawContacts.SORT_KEY_PRIMARY);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactEntity contact = new ContactEntity();
                //raw_contacts id
                String id = cursor.getString(cursor.getColumnIndex(RawContacts._ID));
                String number = "";
                String name = cursor.getString(cursor.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY));
                String account_type = cursor.getString(cursor.getColumnIndex(RawContacts.ACCOUNT_TYPE));
                contact.setId(Long.parseLong(id));
                contact.setName(name);
                if (account_type.equals("com.android.sim")){
                    contact.setSimContact(true);
                }else if (account_type.equals("com.android.localphone")){
                    contact.setSimContact(false);
                }
                //根据raw_contacts id(外键)查询data表的联系人信息
                Cursor cursor_number = resolver.query(Phone.CONTENT_URI, new String[]{Phone.NUMBER},
                        Phone.RAW_CONTACT_ID  + " = " + id, null, null);
                if (cursor_number != null) {
                    while (cursor_number.moveToNext()) {
                        number = cursor_number.getString(cursor_number.getColumnIndex(Phone.NUMBER));
                        contact.setNumber(number);
                    }
                    if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(number)){
                        contacts.add(contact);
                    }
                    cursor_number.close();
                }
            }
            cursor.close();
        }
        return contacts;
    }*/

    public List<ContactEntity> queryAllContacts(){
        List<ContactEntity> contacts = new ArrayList<>();
        String[] projection = new String[] {
                Phone.RAW_CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.DISPLAY_NAME};

        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, null, null, Phone.SORT_KEY_PRIMARY);
        if (cursor != null){
                while (cursor.moveToNext())
                {
                    ContactEntity contact = new ContactEntity();
                    int contactId = cursor.getInt(0);
                    String number = cursor.getString(1);
                    String name = cursor.getString(2);
                    //add by shipeixian for judge if contact repeat begin
                    if (!isRepeatContact(contacts, number, name) && !name.contains("sos")) {
                        contact.setId(contactId);
                        contact.setNumber(number);
                        contact.setName(name);
                        contacts.add(contact);
                    }
                    //add by shipeixian for judge if contact repeat end
                }
            cursor.close();
            }

        return contacts;
    }

    //add by shipeixian for judge if contact repeat begin
    public boolean isRepeatContact(List<ContactEntity> contacts, String number, String name) {
        for (ContactEntity itemContact : contacts) {
            if (number.equals(itemContact.getNumber()) && name.equals(itemContact.getName())) {
                return true;
            }
        }
        return false;
    }
    //add by shipeixian for judge if contact repeat end

    /***
     * 根据号码判断是否为联系人
     * @param number
     * @return
     */
    public boolean isContact(String number) {
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",new String[] {number}, null);  
        if(cursor == null) {
              return false;
        }
        if (cursor.moveToFirst()) {  
              cursor.close();  
              return true;
        }  
        return false;
    }

    public boolean isSimContact(long id){
        Cursor cursor = resolver.query(RawContacts.CONTENT_URI,
                new String[]{RawContacts._ID, RawContacts.ACCOUNT_TYPE},
                RawContacts._ID +"=?" ,new String[]{String.valueOf(id)},null);
        if (cursor != null) {
            while (cursor.moveToNext()){
                String account_type = cursor.getString(cursor.getColumnIndex(RawContacts.ACCOUNT_TYPE));
                if (account_type.equals("com.android.sim")){
                    return true;
                }else if (account_type.equals("com.android.localphone")){
                    return false;
                }
            }
            cursor.close();
        }
        return false;
    }

    /***
     * 查询Sim卡联系人
     * @return
     */
    private List<ContactEntity> querySimContacts(){
        List<ContactEntity> contacts = new ArrayList<>();
        Cursor cursor = resolver.query(simUri, new String[]{RawContacts._ID, RawContacts.DISPLAY_NAME_PRIMARY, RawContacts.ACCOUNT_NAME},
                RawContacts.DELETED + " = 0",null, RawContacts.SORT_KEY_PRIMARY);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                /*for(int i=0; i<cursor.getColumnCount(); i++){
                    String columnName = cursor.getColumnName(i);
                    String value = cursor.getString(i);
                    Log.e("SIM","columnName:"+columnName+"  value:" + value);
                }
                Log.e("SIM","---------------");*/
                ContactEntity contact = new ContactEntity();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String number = cursor.getString(cursor.getColumnIndex("number"));
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(number)){
                    contact.setName(name);
                    contact.setNumber(number);
                    contact.setId(id);
                    contacts.add(contact);
                }

            }
            cursor.close();
        }
        return contacts;
    }

    /***
     * 根据名字和号码获取联系人实体
     * @param name
     * @param number
     * @return
     */
    public ContactEntity getContact(String name, String number){
        List<ContactEntity> contacts = queryAllContacts();
        for (ContactEntity contact : contacts) {
            if (contact.getName().equals(name) && contact.getNumber().equals(number)){
                return contact;
            }
        }
        return null;
    }

    /***
     * 插入联系人
     * @param name 联系人姓名
     * @param number 号码
     */
    public boolean insertContact(String name, String number) {
        //add by shipeixian for avoid repetition begin
        if (getContact(name, number) != null) {
            return false;
        }
        //add by shipeixian for avoid repetition end
        ContentValues values = new ContentValues();
        //首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
        long contact_id = ContentUris.parseId(resolver.insert(RawContacts.CONTENT_URI, values));
        //insert Name
        values.clear();
        //Data.RAW_CONTACT_ID 表示raw_contacts表中的列名"raw_contact_id"
        values.put(Data.RAW_CONTACT_ID, contact_id);
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        values.put(StructuredName.DISPLAY_NAME, name);
        resolver.insert(Data.CONTENT_URI, values);
        //insert number
        values.clear();
        values.put(Data.RAW_CONTACT_ID, contact_id);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER, number);
        resolver.insert(Data.CONTENT_URI, values);
        return true;
    }

    /***
     * 将SIM卡联系人插入数据库
     * @param name
     * @param number
     */
    private void insertSimContactToDb(String name, String number){
        ContentValues values = new ContentValues();
        values.put(RawContacts.ACCOUNT_TYPE, "com.android.sim");
        values.put(RawContacts.ACCOUNT_NAME, "SIM");
        long contact_id = ContentUris.parseId(resolver.insert(RawContacts.CONTENT_URI, values));
        //insert Name
        values.clear();
        //Data.RAW_CONTACT_ID 表示raw_contacts表中的列名"raw_contact_id"
        values.put(Data.RAW_CONTACT_ID, contact_id);
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        values.put(StructuredName.DISPLAY_NAME, name);
        resolver.insert(Data.CONTENT_URI, values);
        //insert number
        values.clear();
        values.put(Data.RAW_CONTACT_ID, contact_id);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER, number);
        resolver.insert(Data.CONTENT_URI, values);
    }

    public void insertSimContact(String name, String number){
        ContentValues values = new ContentValues();
        values.put("tag", name);
        values.put("number", number);
        resolver.insert(simUri, values);
        insertSimContactToDb(name,number);
    }

    /***
     * 复制SIM卡联系人到数据库，检测到SIM卡时调用
     */
    public void copySimContactsToDb(){
        //先删除数据库里之前储存的SIM卡联系人，防止同一联系人出现多次
        deleteSimContactsFromDb();
        List<ContactEntity> contacts = querySimContacts();
        for (ContactEntity contact : contacts) {
            insertSimContactToDb(contact.getName(),contact.getNumber());
        }
    }
    /***
     * 删除数据库中的SIM卡联系人，当取出SIM卡时调用
     */
    public void deleteSimContactsFromDb(){
        Cursor cursor = resolver.query(RawContacts.CONTENT_URI,
                new String[]{RawContacts._ID},
                SIM_CONTACTS_SELECTION,SIM_CONTACTS_ARGS,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(RawContacts._ID));
                //删除联系人
                resolver.delete(ContentUris.withAppendedId(RawContacts.CONTENT_URI, id), null, null);
            }
            cursor.close();
        }
    }

    /***
     * 删除指定联系人(手机联系人和SIM卡联系人)
     * @param rawContactId
     */
    public void deleteContact(long rawContactId, String name, String number) {
        if (isSimContact(rawContactId)){
            deleteSimContact(name,number);
        }
        resolver.delete(ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId), null, null);
    }

    /***
     * 删除sim卡联系人
     * @param name
     * @param number
     */
    public void deleteSimContact(String name, String number){
        resolver.delete(simUri, "tag="+name+" AND number="+number,null);
    }

    /***
     * 删除多个联系人
     * @param contactEntityList
     */
    public void deleteMultipleContacts(List<ContactEntity> contactEntityList) {
        for (ContactEntity contactEntity : contactEntityList) {
            if (isSimContact(contactEntity.getId())){
                //删除SIM卡联系人
                deleteSimContact(contactEntity.getName(),contactEntity.getNumber());
            }
            //删除手机联系人
            resolver.delete(ContentUris.withAppendedId(RawContacts.CONTENT_URI, contactEntity.getId()), null, null);
        }
    }

    /***
     * 删除所有联系人
     */
    public void deleteAllContacts() {
        List<ContactEntity> contacts = querySimContacts();
        if (contacts.size()>0){
            for (ContactEntity contact : contacts) {
                //删除SIM卡联系人
                deleteSimContact(contact.getName(),contact.getNumber());
            }
        }
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                //删除手机联系人
                resolver.delete(uri, null, null);
            }
            cursor.close();
        }
    }

    /***
     * 修改联系人信息
     * @param rawContactId
     * @param name
     * @param number
     */
    public void updateContact(long rawContactId, String oldName, String oldNumber, String name, String number) {
        if (isSimContact(rawContactId)){
            updateSIMContact(oldName,oldNumber,name, number);
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        //Update name
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
        builder.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "=?",
                new String[]{String.valueOf(rawContactId), StructuredName.CONTENT_ITEM_TYPE});
        // 把与这个display_name关联的data2，data3，data5清空，主要是保证(raw_contacts表中的display_name的正确性)
        builder.withValue(
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, // 对应data表中的data2字段
                null);
        builder.withValue(
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, // 对应data表中的data3字段
                        null);
        builder.withValue(
                        ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,// 对应data表中的data5字段
                        null);
        builder.withValue(StructuredName.DISPLAY_NAME, name);
        ops.add(builder.build());
        //Update number
        builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
        builder.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "=?",
                new String[]{String.valueOf(rawContactId), Phone.CONTENT_ITEM_TYPE});
        builder.withValue(Phone.NUMBER, number);
        ops.add(builder.build());
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    /***
     * 修改SIM卡联系人信息
     */
    public void updateSIMContact(String oldName, String oldNumber, String newName, String newNumber)
    {
        ContentValues values = new ContentValues();
        values.put("tag", oldName);
        values.put("number", oldNumber);
        values.put("newTag", newName);
        values.put("newNumber", newNumber);
        resolver.update(simUri, values, null, null);
    }

    public List<CallLog> queryCallLogsByNumber(String number) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "权限被拒绝", Toast.LENGTH_SHORT).show();

            return null;
        }
        List<CallLog> callLogs = new ArrayList<>();
        Cursor cursor = resolver.query(Calls.CONTENT_URI, null, "number=?", new String[]{number}, Calls.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CallLog callLog = new CallLog();
                int id = cursor.getInt(cursor.getColumnIndex(Calls._ID));
                long date = cursor.getLong(cursor.getColumnIndex(Calls.DATE));
                int duration = cursor.getInt(cursor.getColumnIndex(Calls.DURATION));
                callLog.setId(id);
                callLog.setDate(parseDate(date));
                callLog.setType(cursor.getInt(cursor.getColumnIndex(Calls.TYPE)));
                callLog.setDuration(parseDuration(duration));
                callLogs.add(callLog);
            }
            cursor.close();
        }
        return callLogs;
    }

    private String parseDate(long date) {
        //格式化通话时间
        if (ContactsUtil.is24Format(mContext)){
            SimpleDateFormat format;
            if (ContactsUtil.getLanguageEnv().equals("zh")){
                 format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
            }else {
                 format = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.CHINA);
            }
            return format.format(date);
        }else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 ", Locale.CHINA);
            int hour = Integer.parseInt(new SimpleDateFormat("H", Locale.CHINA).format(date));
            if(hour < 13){
                //AM
                SimpleDateFormat format3 = new SimpleDateFormat("h:mm", Locale.CHINA);
                if (ContactsUtil.getLanguageEnv().equals("zh")){
                    return format.format(date) + mContext.getString(R.string.am)+format3.format(date);
                }else {
                    return format.format(date) + format3.format(date)+ mContext.getString(R.string.am);
                }
            }else {
                //PM
                SimpleDateFormat format3 = new SimpleDateFormat("h:mm", Locale.CHINA);
                if (ContactsUtil.getLanguageEnv().equals("zh")){
                    return format.format(date) + mContext.getString(R.string.pm)+format3.format(date);
                }else {
                    return format.format(date) + format3.format(date)+ mContext.getString(R.string.pm);
                }
            }
        }
    }

    private String parseDuration(int duration) {
        String timeStr;
        int hour;
        int minute;
        int second;
        if (duration <= 0)
            return mContext.getString(R.string.MS);
        else {
            minute = duration / 60;
            if (minute < 60) {
                second = duration % 60;
                timeStr = unitFormat(minute) + mContext.getString(R.string.M) +
                        unitFormat(second) + mContext.getString(R.string.S);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = duration - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + mContext.getString(R.string.H) +
                        unitFormat(minute) + mContext.getString(R.string.M) +
                        unitFormat(second) + mContext.getString(R.string.S);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public void deleteCallLogById(int id) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        resolver.delete(Calls.CONTENT_URI, "_id=?", new String[]{String.valueOf(id)});
    }

    /***
     * 删除指定号码所有通话记录
     * @param number 号码
     */
    public void deleteCallLogByNumber(String number) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        resolver.delete(Calls.CONTENT_URI, "number=?", new String[]{number});

    }
}
