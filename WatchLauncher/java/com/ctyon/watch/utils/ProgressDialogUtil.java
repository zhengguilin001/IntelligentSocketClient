package com.ctyon.watch.utils;

import android.app.ProgressDialog;
import android.content.Context;


/**
 * Created by zx
 * On 2017/11/23
 */

public class ProgressDialogUtil {

    private static ProgressDialog progressDialog;

    /***
     * uncancelable ProgressDialog
     * @param context
     * @param message
     */
    public static void showProgressDialog(Context context, String message){
        if(progressDialog != null)
            progressDialog.dismiss();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
