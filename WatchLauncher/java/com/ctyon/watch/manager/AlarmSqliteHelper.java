package com.ctyon.watch.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/10/26.
 */

public class AlarmSqliteHelper extends SQLiteOpenHelper {

    public static final String ALARM_TABLE_NAME = "tb_watch_alarm";
    public static final String ALARM_ID_NAME = "_id";
    public static final String ALARM_TIME_VALUE = "time";
    public static final String ALARM_DESC_VALUE = "description";
    public static final String ALARM_TYPE_VALUE = "type";
    public static final String ALARM_TITLE_VALUE = "title";
    public static final String ALARM_IS_OPEN_VALUE = "isOpen";
    public static final String ALARM_IS_ONETIME_VALUE = "isOneTime";
    public static final String ALARM_CYCLE_WEEK = "cycleWeek";

    private static AlarmSqliteHelper helper = null;

    private AlarmSqliteHelper(Context context) {
        super(context, "alarm_db", null, 1);
    }

    public static AlarmSqliteHelper getInstance(Context context) {
        if (helper == null) {
            synchronized (AlarmSqliteHelper.class) {
                if(helper == null){
                    return new AlarmSqliteHelper(context);
                }
            }
        }
        return helper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ALARM_TABLE_NAME + "(" + ALARM_ID_NAME + " INTEGER primary key autoincrement," + ALARM_TIME_VALUE +
                " varchar, " + ALARM_DESC_VALUE + " varchar," + ALARM_TYPE_VALUE + " integer," + ALARM_TITLE_VALUE + " varchar," +
                ALARM_IS_OPEN_VALUE + " boolean," + ALARM_IS_ONETIME_VALUE + " boolean,"+ALARM_CYCLE_WEEK+" varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        deleteTable(db,ALARM_TABLE_NAME);
    }

    public void deleteTable(SQLiteDatabase db, String name) {

        String sql_delteTable = "DROP TABLE if exists " + name;

        db.execSQL(sql_delteTable);
    }
}
