package com.wx.common.support.utils.db;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

/**
 * Created by Administrator on 2018/3/15.
 */

public class SnappyUtils {
    /**
     * 保存String对象到/data/data/com.snappydb/files/mydatabse下
     *
     * @param ctx
     * @param name
     * @param result
     */
    public static void putString(Context ctx, String name, String result) {
        try {
            DB snappydb = DBFactory.open(ctx);
            snappydb.put(name, result);
            snappydb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ctx
     * @param name
     * @return
     */
    public static String getString(Context ctx, String name) {
        String str = "";
        try {
            DB snappydb = DBFactory.open(ctx);
            str = snappydb.get(name);
            snappydb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return str;
    }
}
