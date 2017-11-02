package com.javano1.gallery.service;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadImageService {

    private static LoadImageService loadImageService = new LoadImageService();
    private LruCache<String, Bitmap> memoryCache;
    private ExecutorService loadImageThreadPool = Executors.newFixedThreadPool(4);

    private LoadImageService() {
        long availableMemory = Runtime.getRuntime().maxMemory() / 4096;
        memoryCache = new LruCache<String, Bitmap>((int) availableMemory) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    public static LoadImageService getInstance() {
        return loadImageService;
    }

    public Bitmap loadImageByPath(final String path, final int size, final LoadImageCallBack mCallBack){
        @SuppressLint("HandlerLeak")
        final Handler mHander = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mCallBack.onLoadImage((Bitmap) msg.obj);
            }
        };

        Bitmap bitmap = getBitmapFromMemCache(path);
        if(bitmap == null){
            loadImageThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap mBitmap = getThumbBitmap(path, size);
                    Message msg = mHander.obtainMessage();
                    msg.obj = mBitmap;
                    mHander.sendMessage(msg);
                    addBitmapToMemoryCache(path, mBitmap);
                }
            });
        }
        return bitmap;
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            memoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    private Bitmap getThumbBitmap(String path, int size){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = getInSampleSize(options, size);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private int getInSampleSize(BitmapFactory.Options options, int size){
        int inSampleSize = 1;
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        if (size <= 0)
            return 12;

        if(bitmapWidth > size || bitmapHeight > size){
            int widthScale = Math.round((float) bitmapWidth / (float) size);
            int heightScale = Math.round((float) bitmapHeight / (float) size);

            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    public interface LoadImageCallBack{
        void onLoadImage(Bitmap bitmap);
    }
}
