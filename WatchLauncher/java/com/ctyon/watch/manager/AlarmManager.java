package com.ctyon.watch.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ctyon.watch.model.AlarmModel;

import java.util.ArrayList;
import java.util.List;

import static com.ctyon.watch.manager.AlarmSqliteHelper.ALARM_CYCLE_WEEK;
import static com.ctyon.watch.manager.AlarmSqliteHelper.ALARM_DESC_VALUE;
import static com.ctyon.watch.manager.AlarmSqliteHelper.ALARM_ID_NAME;
import static com.ctyon.watch.manager.AlarmSqliteHelper.ALARM_IS_ONETIME_VALUE;
import static com.ctyon.watch.manager.AlarmSqliteHelper.ALARM_IS_OPEN_VALUE;
import static com.ctyon.watch.manager.AlarmSqliteHelper.ALARM_TABLE_NAME;
import static com.ctyon.watch.manager.AlarmSqliteHelper.ALARM_TIME_VALUE;
import static com.ctyon.watch.manager.AlarmSqliteHelper.ALARM_TITLE_VALUE;
import static com.ctyon.watch.manager.AlarmSqliteHelper.ALARM_TYPE_VALUE;

/**
 * Created by Administrator on 2017/10/26.
 */

public class AlarmManager {

    private final AlarmSqliteHelper helper;
    private SQLiteDatabase db;

    public AlarmManager(Context context) {
        helper = AlarmSqliteHelper.getInstance(context);
    }

    public void addAlarm(AlarmModel entity) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_TIME_VALUE, entity.getTime());
        values.put(ALARM_DESC_VALUE, entity.getDescription());
        values.put(ALARM_TYPE_VALUE, entity.getType());
        values.put(ALARM_IS_OPEN_VALUE, entity.isOpen());
        values.put(ALARM_IS_ONETIME_VALUE, entity.isOneTime());
        values.put(ALARM_CYCLE_WEEK, entity.getAlarm_week());
        long insert = db.insert(ALARM_TABLE_NAME, null, values);
        db.close();
    }

    public void deleteAlarmById(long id) {
        db = helper.getWritableDatabase();
        db.delete(ALARM_TABLE_NAME, "_id=?", new String[]{"" + id});
        db.close();
    }

    public void closeDB() {
        if (db != null) {
            db.close();
        }
        if (helper != null) {
            helper.close();
        }
    }

    public long countOfAlarm() {
        db = helper.getWritableDatabase();
        String sql = "select count(*) from " + ALARM_TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        db.close();
        return count;
    }

    public AlarmModel queryIsRepeatById(long id) {
        db = helper.getWritableDatabase();
        AlarmModel entity = new AlarmModel();
        Cursor cursor = db.query(ALARM_TABLE_NAME, null, ALARM_ID_NAME + "=?", new String[]{"" + id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long mid = cursor.getLong(cursor.getColumnIndex(ALARM_ID_NAME));
                String time = cursor.getString(cursor.getColumnIndex(ALARM_TIME_VALUE));
                String desc = cursor.getString(cursor.getColumnIndex(ALARM_DESC_VALUE));
                int type = cursor.getInt(cursor.getColumnIndex(ALARM_TYPE_VALUE));
                String title = cursor.getString(cursor.getColumnIndex(ALARM_TITLE_VALUE));
                boolean isOpen = cursor.getInt(cursor.getColumnIndex(ALARM_IS_OPEN_VALUE)) > 0;
                boolean isOneTime = cursor.getInt(cursor.getColumnIndex(ALARM_IS_ONETIME_VALUE)) > 0;
                String alarm_week = cursor.getString(cursor.getColumnIndex(ALARM_CYCLE_WEEK));
                if (id == mid) {
                    entity.setAlramId(id);
                    entity.setTime(time);
                    entity.setDescription(desc);
                    entity.setType(type);
                    entity.setOpen(isOpen);
                    entity.setOneTime(isOneTime);
                    entity.setAlarm_week(alarm_week);
                    return entity;
                }
            }
        }
        cursor.close();
        db.close();
        return null;
    }

    public void deleteAll() {
        db = helper.getWritableDatabase();
        db.delete(ALARM_TABLE_NAME, null, null);
        db.close();
    }

    public long queryIdByTime(AlarmModel entity) {
        db = helper.getWritableDatabase();
        Cursor cur = db.query(ALARM_TABLE_NAME, new String[]{ALARM_ID_NAME}, ALARM_TIME_VALUE + "=?", new String[]{entity.getTime()}, null, null, null);
        cur.moveToFirst();
        long aLong = cur.getLong(0);
        cur.close();
        db.close();
        return aLong;
    }

    //add by shipeixian begin
    public long queryIdByTimeAndType(String time, String type) {
        db = helper.getWritableDatabase();
        Cursor cur = db.query(ALARM_TABLE_NAME, new String[]{ALARM_ID_NAME}, ALARM_TIME_VALUE + "=? and type=?", new String[]{time, type}, null, null, null);
        if (cur.moveToFirst()) {
            long aLong = cur.getLong(0);
            cur.close();
            db.close();
            return aLong;
        }
        cur.close();
        db.close();
        return -1;

    }
    //add by shipeixian end

    public List<AlarmModel> queryAll() {
        db = helper.getWritableDatabase();
        List<AlarmModel> list = new ArrayList<>();
        Cursor cursor = db.query(ALARM_TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                for(int i=0; i<cursor.getColumnCount(); i++){
                    String columnName = cursor.getColumnName(i);
                    String value = cursor.getString(i);
                    Log.e("Alarm","columnName:"+columnName+"  value:" + value);
                }
                Log.e("Alarm","---------------");
                AlarmModel entity = new AlarmModel();
                long id = cursor.getLong(cursor.getColumnIndex(ALARM_ID_NAME));
                String time = cursor.getString(cursor.getColumnIndex(ALARM_TIME_VALUE));
                String desc = cursor.getString(cursor.getColumnIndex(ALARM_DESC_VALUE));
                int type = cursor.getInt(cursor.getColumnIndex(ALARM_TYPE_VALUE));
                String title = cursor.getString(cursor.getColumnIndex(ALARM_TITLE_VALUE));
                boolean isOpen = cursor.getInt(cursor.getColumnIndex(ALARM_IS_OPEN_VALUE)) > 0;
                boolean isOneTime = cursor.getInt(cursor.getColumnIndex(ALARM_IS_ONETIME_VALUE)) > 0;
                String alarm_week = cursor.getString(cursor.getColumnIndex(ALARM_CYCLE_WEEK));
                entity.setAlramId(id);
                entity.setTime(time);
                entity.setDescription(desc);
                entity.setType(type);
                entity.setOpen(isOpen);
                entity.setOneTime(isOneTime);
                entity.setAlarm_week(alarm_week);
                list.add(entity);
            }
            cursor.close();
        }
        db.close();
        return list;
    }
}
