package com.ctyon.watch.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/9/12.
 */

public class WarnUtils {
    public static void toast(Context context, String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
    public static void toast(Context context, int str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
}
