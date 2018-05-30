package com.wx.common.support.utils;

import java.io.File;

/**
 * Created by xmai on 18-3-26.
 * DataCleanManager.DeleteFile(newFile("data/data/"+getPackageName()));
 */

public class DataCleanManager {
    public static void DeleteFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
            }
        }
    }
}
