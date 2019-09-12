package com.mogu.picturepreview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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
    public void openPreview(ReadableArray array, int index, boolean isShowSave)

    {
        /** add by david 2019-6-13 start  */
        //添加权限判断
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int permission_result = PermissionChecker.checkPermission(getCurrentActivity(), perms[0], android.os.Process.myPid(), android.os.Process.myUid(), getCurrentActivity().getPackageName());
        if (permission_result != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getCurrentActivity(), perms, 100);
            return;
        }
        /** add by david 2019-6-13 end  */

        Context context = getCurrentActivity();
        initImageLoader(context);


        String imgs[] = new String[array.size()];
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = array.getString(i);

        }

        /*add by david model处理 start*/
        activityhide((Activity) context);
        /*add by david model处理 end*/

        Intent intent = new Intent(context, BigImgBrowse.class);
        intent.putExtra("imgUrlArr", imgs);
        intent.putExtra("currentIndex", index);
        intent.putExtra("isShowSave", isShowSave);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }


    public void activityhide(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //此时已在主线程中，可以更新UI了
                activity.setVisible(false);
            }
        });

        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此时已在主线程中，可以更新UI了
                        activity.setVisible(true);
                    }
                });

            }
        }, 1000);

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
