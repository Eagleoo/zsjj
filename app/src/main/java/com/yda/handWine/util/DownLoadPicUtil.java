package com.yda.handWine.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class DownLoadPicUtil {

    public interface DownLoadCallBack {
        void onSuccess(Bitmap bitmap, String downloadpath);

        void onFailure(String message);
    }

    public static void download(final String imgUrl, final Activity activity, final DownLoadCallBack downLoadCallBack) {
        //获得图片的地址
//        final String imgUrl = "http://pic59.nipic.com/file/20150113/14563866_191535842000_2.jpg";

        String image = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
        final String imagename = image.split("\\.")[0];
        final String imageName = imagename + ".png";

        Log.e("图片", "imageName" + imageName + "url" + imgUrl);


        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getPic(imgUrl);//下载
                Looper.prepare();
                if (BitmapUtil.saveImageToGallery(activity,bitmap)){//保存到相册
                    Util.show(activity,"保存成功");
                }
                else {
                    Util.show(activity,"保存失败");
                }
                Looper.loop();
                //Util.save2Album(bitmap,activity,imgUrl);
                //onSaveBitmap(bitmap, imageName, downLoadCallBack);//保存到本地


            }
        }).start();


    }

    public static Bitmap getPic(String url) {
        //获取okHttp对象get请求
        try {
            OkHttpClient client = new OkHttpClient();
            //获取请求对象
            Request request = new Request.Builder().url(url).build();
            //获取响应体
            ResponseBody body = client.newCall(request).execute().body();
            //获取流
            InputStream in = body.byteStream();
            //转化为bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("CheckResult")
    private static void onSaveBitmap(final Bitmap mBitmap, String filename, final DownLoadCallBack downLoadCallBack) {
        // 第一步：首先保存图片
        //将Bitmap保存图片到指定的路径/sdcard/Boohee/下，文件名以当前系统时间命名,但是这种方法保存的图片没有加入到系统图库中

        final File dcimFile = FileUtil
                .getDCIMFile(FileUtil.FILE_IMAGE, filename);


        try {
            FileOutputStream fos = new FileOutputStream(dcimFile);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Observable.just(mBitmap).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) throws Exception {
                            downLoadCallBack.onSuccess(mBitmap, dcimFile.getAbsolutePath());
                            Log.e("路径保存位置", "位置：" + dcimFile.getAbsolutePath());
                        }
                    });
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Result handleQRCodeFormBitmap(Bitmap bitmap) {
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        com.yda.handWine.util.RGBLuminanceSource source = new com.yda.handWine.util.RGBLuminanceSource(bitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader2 = new QRCodeReader();
        Result result = null;
        try {
            try {
                result = reader2.decode(bitmap1, hints);
            } catch (ChecksumException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }




}
