package com.mogu.picturepreview;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;


import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.facebook.react.bridge.ReactApplicationContext;

import java.io.File;

public class PicturePreview extends ReactContextBaseJavaModule {
    private final ReactApplicationContext _reactContext;


    public PicturePreview(ReactApplicationContext reactContext) {
        super(reactContext);

        _reactContext = reactContext;

    }

    @Override
    public String getName() {
        return "PicturePreview";
    }


    /**
     * 图片预览
     *
     * @param {String[]} imgs ---图片路径
     * @param {int}      index   ---当前图片位置
     */


    @ReactMethod
    public void openPreview(ReadableArray array,int index,boolean isShowSave)

    {
        Context context = getCurrentActivity();
        initImageLoader(context);


        String imgs[] = new String[array.size()];
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = array.getString(i);

        }


        Intent intent = new Intent(context, BigImgBrowse.class);
        intent.putExtra("imgUrlArr", imgs);
        intent.putExtra("currentIndex", index);
        intent.putExtra("isShowSave", isShowSave);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }




    public void initImageLoader(Context context) {
        final String SDCARD = Environment
                .getExternalStorageDirectory().toString();
        final String appDirName = "mogu";
        final String cacheDir_path = SDCARD + "/" + appDirName + "/"
                + "universal_cache_dir";


        File cacheDir = new File(cacheDir_path);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .build();

        ImageLoader.getInstance().init(config);
    }


}
