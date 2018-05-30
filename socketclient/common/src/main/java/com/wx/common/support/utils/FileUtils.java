package com.wx.common.support.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xmai on 18-3-29.
 *
 */

public class FileUtils {
    public static void saveData(Context ctx,String str,String filename) throws IOException {
        OutputStream out = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
        /** 参数一: 文件名。MODE_WORLD_WRITEABLE  MODE_PRIVATE
        Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，
         如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
        Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
        Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
        MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入
         * 如果文件不存在，Android会自动创建它。创建的文件保存在/data/data/<package name>/files目录下
		 * 参数二: 文件操作模式参数。代表该文件是私有数据，只能被应用本身访问。
		 * */
        Writer writer = new OutputStreamWriter(out);
        try {
            writer.write(str);
        } finally {
            writer.close();
        }
    }

    public static String loadData(Context ctx,String filename) throws FileNotFoundException, IOException {
        String result = "";
        BufferedReader reader = null;
        StringBuilder data = new StringBuilder();
        try {
            InputStream in = ctx.openFileInput(filename);
            Log.i("521", in.toString());
            reader = new BufferedReader(new InputStreamReader(in));
            String line = new String();
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (FileNotFoundException e) {
            Log.d("521", "没有发现保存的数据");
        } finally {
            reader.close();
        }
        result = data.toString();
        return result;

    }

    public static boolean inSameDay(Date date1, Date Date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(Date2);
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        if ((year1 == year2) && (day1 == day2)) {
            return true;
        }
        return false;
    }

    /**
     * 读取文件的最后修改时间的方法
     */
    public static String getFileLastModifiedTime1(String filePath) {
        String path = filePath.toString();
        File f = new File(path);
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);

        // 输出：修改时间[2] 2009-08-17 10:32:38
        return formatter.format(cal.getTime());
    }

    public static Date getFileLastModifiedTime(String filePath) {
        String path = filePath.toString();
        File f = new File(path);
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);
        String s = formatter.format(cal.getTime());
        // 输出：修改时间[2] 2009-08-17 10:32:38
        try {
            return formatter.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }


    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     *  @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir().getAbsoluteFile());
    }

    /**删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     * @param directory
     */
    public static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
}
