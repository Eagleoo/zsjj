package com.yda.handWine.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.yda.handWine.SampleApplicationLike;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    /**
     * 拍照路径
     */

    private static String FILE_NAME = "userIcon.jpg";
    public static String PATH_PHOTOGRAPH = "/LXT/";

    public static String Appname = "CSF";

    public static String getFileName() {
        String fileName = FILE_NAME;
        return fileName;
    }

    public static final String APPS_ROOT_DIR = getExternalStorePath() + "/" + Appname + "/";
    public static final String FILE_IMAGE = getExternalStorePath() + "/" + Appname + "/image/";
    public static final String FILE_VOICE = getExternalStorePath() + "/" + Appname + "/voice/";
    public static final String FILE_FILE = getExternalStorePath() + "/" + Appname + "/file/";
    public static final String FILE_DB = getExternalStorePath() + "/" + Appname + "/db/";
    public static final String FILE_AVATAR = getExternalStorePath() + "/" + Appname + "/avatar/";
    public static final String FILE_RICH_TEXT = getExternalStorePath() + "/" + Appname + "/richtext/";


    /**
     * 外置存储卡的路径
     *
     * @return
     */
    public static String getExternalStorePath() {
        if (isExternalStorageWritable()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 是否有外存卡
     *
     * @return
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 初始化应用文件夹目录
     */
    public static void initFileAccess() {
        File rootDir = new File(APPS_ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }

        File imessageDir = new File(FILE_VOICE);
        if (!imessageDir.exists()) {
            imessageDir.mkdir();
        }

        File imageDir = new File(FILE_IMAGE);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }

        File fileDir = new File(FILE_FILE);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        File avatarDir = new File(FILE_AVATAR);
        if (!avatarDir.exists()) {
            avatarDir.mkdir();
        }
        File richTextDir = new File(FILE_RICH_TEXT);
        if (!richTextDir.exists()) {
            richTextDir.mkdir();
        }

    }

    private static String getFilePath() {

        String filepath = "";
        try {
            filepath = Environment.getExternalStorageDirectory().getCanonicalPath() + PATH_PHOTOGRAPH;
        } catch (IOException e) {
            Toast.makeText(SampleApplicationLike.getContext(), "文件存储出现问题" + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return filepath;
    }

    public static File getAvailableCacheDir() {
        if (isExternalStorageWritable()) {
            return SampleApplicationLike.getContext().getExternalCacheDir();
        } else {
            return SampleApplicationLike.getContext().getCacheDir();
        }
    }

    public static String getAvatarCropPath() {
        return new File(getAvailableCacheDir(), FILE_NAME).getAbsolutePath();
    }

    public static String getPublishPath(String fileName) {
        return new File(getAvailableCacheDir(), fileName).getAbsolutePath();
    }


    // 保存图片到手机指定目录
    public static void savaBitmap(String imgName, byte[] bytes) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileOutputStream fos = null;
            try {

                initFileAccess();
                imgName = FILE_IMAGE + imgName;
                fos = new FileOutputStream(imgName);
                fos.write(bytes);
                Toast.makeText(SampleApplicationLike.getContext(), "图片已保存到" + imgName, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(SampleApplicationLike.getContext(), "请检查SD卡是否可用", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 保存图片
     *
     * @param bitmap
     * @param filePath
     */
    public static void saveBitmap(Bitmap bitmap, String filePath) {
        FileOutputStream bos = null;
        File file = new File(filePath);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            bos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void deleteDir(File directory) {
        if (directory != null) {
            if (directory.isFile()) {
                directory.delete();
                return;
            }

            if (directory.isDirectory()) {
                File[] childFiles = directory.listFiles();
                if (childFiles == null || childFiles.length == 0) {
                    directory.delete();
                    return;
                }

                for (int i = 0; i < childFiles.length; i++) {
                    deleteDir(childFiles[i]);
                }
                directory.delete();
            }
        }
    }


    public static File getDCIMFile(String filePath, String imageName) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 文件可用
            File dirs = new File(Environment.getExternalStorageDirectory(),
                    "DCIM" + filePath);
            if (!dirs.exists())
                dirs.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),
                    "DCIM" + filePath + imageName);
            if (!file.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    file.createNewFile();
                } catch (Exception e) {
                }
            }
            return file;
        } else {
            return null;
        }

    }


    public static File saveBitmap2(Bitmap bitmap, String fileName, File baseFile) {
        FileOutputStream bos = null;
        File imgFile = new File(baseFile, "/" + fileName);
        try {
            bos = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgFile;
    }

    public static File getBaseFile(String filePath) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 文件可用
            File f = new File(Environment.getExternalStorageDirectory(),
                    filePath);
            if (!f.exists())
                f.mkdirs();
            return f;
        } else {
            return null;
        }
    }


    /**
     * 由指定的路径和文件名创建文件
     */
    public static File createFile(String path, String name) throws IOException {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(path + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    // 自定义接口
    public interface NetEvevt {
        public void onNetChange(int netMobile);
    }

    public static boolean getUninatllApkInfo(Context context, String filePath) {
        boolean result = false;
        try {
            PackageManager pm = context.getPackageManager();
            Log.e("archiveFilePath", filePath);
            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                result = true;//完整
            }
        } catch (Exception e) {
            result = false;//不完整
        }
        return result;
    }


}

