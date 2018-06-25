package com.ctyon.socketclient.app.server;

import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SocketServiceB extends Service {
    public SocketServiceB() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 批量添加通讯录
     */
    public void batchAddContact(Context context, String[] splitOldContactStr) {
        android.util.Log.i("shipeixian", "批量导入 begin");
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

        }
        android.util.Log.i("shipeixian", "批量导入 end");
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

    /**
     * 从sd卡获取图片资源
     * @return
     */
    private List<String> getImagePathFromSD() {
        // 图片列表
        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator +"Pictures";
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsImageFile(file.getPath())) {
                imagePathList.add(file.getPath());
            }
        }
        // 返回得到的图片列表
        return imagePathList;
    }

    /**
     * 检查扩展名，得到图片格式的文件
     * @param fName  文件名
     * @return
     */
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }


    /**
     * 切换模式 4G与2G切换，传入10则为4G，传入5则为2G
     * 4g通用Global：10   2G CDMA w/o EVDO：5
     * @param context
     * @param nwMode
     */
    private void setPreferredNetworkMode(Context context, int nwMode) {
        final int phoneSubId = 2;
        final int phoneId = 0;
        android.provider.Settings.Global.putInt(context.getContentResolver(),"preferred_network_mode" + phoneSubId, nwMode);
        if(putIntAtIndex(context.getContentResolver(), "preferred_network_mode", phoneId, nwMode)) {
            android.util.Log.i("shipeixian", "网络模式切换成功,模式为"+(nwMode == 5 ? "2G" : "4G"));
        }
    }

    public boolean putIntAtIndex(android.content.ContentResolver cr, String name, int index, int value) {
        String data = "";
        String valArray[] = null;
        String v = android.provider.Settings.Global.getString(cr, name);

        if (index == Integer.MAX_VALUE) {
            throw new RuntimeException("putIntAtIndex index == MAX_VALUE index=" + index);
        }
        if (index < 0) {
            throw new RuntimeException("putIntAtIndex index < 0 index=" + index);
        }
        if (v != null) {
            valArray = v.split(",");
        }

        // Copy the elements from valArray till index
        for (int i = 0; i < index; i++) {
            String str = "";
            if ((valArray != null) && (i < valArray.length)) {
                str = valArray[i];
            }
            data = data + str + ",";
        }

        data = data + value;

        // Copy the remaining elements from valArray if any.
        if (valArray != null) {
            for (int i = index+1; i < valArray.length; i++) {
                data = data + "," + valArray[i];
            }
        }
        return android.provider.Settings.Global.putString(cr, name, data);
    }

    public boolean getRecordState() {
        int minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 100));
        short[] point = new short[minBuffer];
        int readSize = 0;
        try {
            audioRecord.startRecording();
        } catch (Exception e) {
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
                Log.i("shipeixian","startRecording():" + e);
            }
            return false;
        }
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            Log.d("shipeixian","audio input has been occupied");
            return false;
        } else {
            readSize = audioRecord.read(point, 0, point.length);
            if (readSize <= 0) {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
                Log.d("shipeixian","result of audiorecord is empty");
                return false;
            } else {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
                Log.d("shipeixian","result of audiorecord is not empty");
                return true;
            }
        }
    }

}
