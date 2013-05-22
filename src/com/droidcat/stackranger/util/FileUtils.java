package com.droidcat.stackranger.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtk54039 on 13-5-16.
 * cache Json/Image to sdcard/cachedir
 */
public class FileUtils {
    public static final String TAG = FileUtils.class.getSimpleName();

    public synchronized static void deleteSitesCache(Context pContext, String type) {
        DeleteFileOrFolder(getStoreFileDir(pContext, type));
    }

    //write String cache to file.
    public synchronized static boolean writeStringCache(Context pContext, String uid, String data, String type) {
        File cache = new File(getStoreFileDir(pContext, type), uid);
        BufferedWriter lBufferedWriter = null;
        try {
            lBufferedWriter = new BufferedWriter(new FileWriter(cache));
            lBufferedWriter.write(data);
            lBufferedWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (lBufferedWriter != null) {
                try {
                    lBufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static File getStoreFileDir(Context pContext, String dirName) {
        File dir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = new File(pContext.getExternalCacheDir(), dirName);
        } else {
            dir = new File(pContext.getCacheDir(), dirName);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public synchronized static List<String> readStringCaches(Context pContext, String type) {
        File cacheDir = getStoreFileDir(pContext, type);
        Log.i(TAG, "cacheDir---=: " + cacheDir);
        List<String> result = new ArrayList<String>();
        if (cacheDir.exists() && cacheDir.isDirectory()) {
            File files[] = cacheDir.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    result.add(readStringCache(files[i]));
                }
                return result;
            }
        }
        return null;
    }

    // read cached string
    public synchronized static String readStringCache(File pFile) {
        BufferedReader lBufferedReader = null;
        try {
            lBufferedReader = new BufferedReader(new FileReader(pFile));
            String line;
            StringBuilder lStringBuilder = new StringBuilder();
            while ((line = lBufferedReader.readLine()) != null) {
                lStringBuilder.append(line);
            }
            String result = lStringBuilder.toString();
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (lBufferedReader != null) {
                try {
                    lBufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param folder 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean DeleteFileOrFolder(File folder) {
        boolean flag = false;
        // 判断目录或文件是否存在
        if (!folder.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (folder.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(folder);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(folder);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param file 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(File file) {
        boolean flag = false;
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param folder 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(File folder) {
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i]);
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i]);
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (folder.delete()) {
            return true;
        } else {
            return false;
        }
    }
}
