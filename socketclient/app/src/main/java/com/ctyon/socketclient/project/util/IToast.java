package com.ctyon.socketclient.project.util;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctyon.socketclient.App;
import com.ctyon.socketclient.BuildConfig;
import com.ctyon.socketclient.R;

/**
 * Created by Administrator on 2018/3/12.
 * 1-自定义样式
 * 2-内部自动获取上下文
 */

public class IToast {
    /**
     * 展示toast==LENGTH_SHORT
     *
     * @param msg
     */
    public static void show(String msg) {
        if(BuildConfig.DEBUG) {
            show(msg, Toast.LENGTH_SHORT);
        }else{
            Log.d("521", msg);
        }
    }

    /**
     * 展示toast==LENGTH_LONG
     *
     * @param msg
     */
    public static void showLong(String msg) {
        show(msg, Toast.LENGTH_LONG);
    }


    private static void show(String massage, int show_length) {
        Toast toast = null;
        Context context = App.getsContext();
        //使用布局加载器，将编写的toast_layout布局加载进来
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        //获取ImageView
        ImageView image = view.findViewById(R.id.toast_iv);
        //设置图片
        image.setImageResource(R.drawable.ic_report_problem);
        //获取TextView
        TextView title = view.findViewById(R.id.toast_tv);
        //设置显示的内容
        title.setText(massage);
        if (toast == null) {
            toast = new Toast(context);
        }
        //设置Toast要显示的位置，水平居中并在底部，X轴偏移0个单位，Y轴偏移70个单位，
//        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 70);
        toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 70);
        //设置显示时间
        toast.setDuration(show_length);
        toast.setView(view);
        toast.show();
    }
}
