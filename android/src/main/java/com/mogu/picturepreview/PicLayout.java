package com.mogu.picturepreview;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import android.support.v4.view.ViewPager.OnPageChangeListener;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import android.os.Handler;

/**
 * <p>com.ram.rctagora
 * <p>Created by GaoXuanJi on 2017/8/31
 * <p>Des          :
 * <p>Modify Author:
 * <p>Modify Date  :
 */
public class PicLayout extends FrameLayout  {


    private ViewPager mPager;
    private Map<String, View> ViewsMap;
    private String[] imgUrlArr;
    private LinearLayout.LayoutParams lParams = null;

    private BigImgBrowseAdapter adapter=null;
    public static int currentItem = 0;
    public  Context _context;

    private static Context mContext;
    private static Bitmap mBitmap;
    private static String saveDir = "saveImage";
    private static int compressQuality = 100;
    private static String saveMessage;
    private static String filePath;
    private static String suffix;
    private static String fileName;
    private static ProgressDialog mSaveDialog = null;




    float mStartX = 0;
//    public  static  ZoomImageView hy1;
    boolean drag = true;

    TextView tv_saveimg, tv_num;
    ImageView tv_close;



    public PicLayout(Context context) {
        this(context, null);
    }

    public PicLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PicLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setimageData(@NonNull String[] imgs) {
        imgUrlArr = imgs;
    }


    public void setindex(@NonNull int index) {

        currentItem = index;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(adapter==null){
            setView();
        }
        super.onDraw(canvas);
    }

    private void setView() {


//        ViewsMap = new HashMap<String, View>();
////        adapter = new BigImgBrowseAdapter(_context, imgUrlArr, ViewsMap);
//        mPager.setAdapter(adapter);
//        mPager.setCurrentItem(currentItem);
//        mPager.setOnPageChangeListener(pageChange);
////
//        tv_saveimg.setOnClickListener(MyListener);
//        tv_close.setOnClickListener(MyListener);
//        tv_num.setText(currentItem + 1 + "/" + imgUrlArr.length);

    }

    @Override
    protected void detachAllViewsFromParent() {
        super.detachAllViewsFromParent();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private View.OnClickListener MyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            if (v == tv_close) {
                removeAllViews();
                return;
            }
            if (v == tv_saveimg) {
                String imgurl = imgUrlArr[currentItem];
                saveImg(imgurl, "jpg");
                return;
            }
        }
    };


        private InputStream getImageStream(String filePath) throws Exception {
        URL url = new URL(filePath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    private void saveFile(Bitmap bm) throws IOException {
        String sdcardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
        File dirPath = new File(sdcardPath, saveDir);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }

        String fileName = System.currentTimeMillis() + "." + suffix;

        File imageFile = new File(dirPath, fileName);

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile));
        Bitmap.CompressFormat type;
        switch (suffix.toLowerCase()) {
            case "jpg":
                type = Bitmap.CompressFormat.JPEG;
                break;
            case "jpeg":
                type = Bitmap.CompressFormat.JPEG;
                break;
            case "png":
                type = Bitmap.CompressFormat.PNG;
                break;
            case "webp":
                type = Bitmap.CompressFormat.WEBP;
                break;
            default:
                type = null;
        }

        if (type != null) {
            bm.compress(type, compressQuality, bos);
        }

        bos.flush();
        bos.close();
        MediaScannerConnection.scanFile(mContext, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                if (uri == null) {
                    saveMessage = "添加图片错误";
                }
            }
        });

    }

    private Runnable saveFileRunnable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            Handler messageHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    mSaveDialog.dismiss();
                    Toast toast = Toast.makeText(mContext, saveMessage, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    ;
                    toast.show();
                }
            };
            try {
                mBitmap = BitmapFactory.decodeStream(getImageStream(filePath));
                if (mBitmap == null) {
                    saveMessage = "网络传输错误";
                } else {
                    saveFile(mBitmap);
                    saveMessage = "图片保存成功";
                }
            } catch (IOException e) {
                saveMessage = "图片保存失败！";
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageHandler.sendMessage(messageHandler.obtainMessage());
            Looper.loop();
        }

    };

        public void saveImg(String url, String imageSuffix) {
        mContext = getContext();
        filePath = url;
        suffix = imageSuffix;
        // suffix = filePath.substring( filePath.lastIndexOf(".") + 1 );
        // if( suffix.indexOf("?") != -1 ){
        //     suffix = suffix.substring(0, suffix.indexOf("?"));
        // }
        mSaveDialog = ProgressDialog.show(mContext, "保存图片", "图片正在保存中...", true);
        new Thread(saveFileRunnable).start();
    }

    OnPageChangeListener pageChange = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
//            hy1 = (ZoomImageView) ViewsMap.get("" + arg0);

            currentItem = arg0;
//            AppUtils.noneselsect(arg0, imageViews);
            tv_num.setText(currentItem+1+"/"+imgUrlArr.length);

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }
    };


    private class ClickBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("pic_close")) {
                removeAllViews();
            }

        }

    }
    private ClickBroadcastReceiver mLoginBroadcastReceiver;

    private void SetBroadcastReceiver(Context context) {
        mLoginBroadcastReceiver = new ClickBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("pic_close");
        context.registerReceiver(mLoginBroadcastReceiver, intentFilter);
    }
        /**
         * 初始化View
         *
         * @param context
         */
    private void initView(Context context) {
        _context=context;
        initImageLoader(context);
        SetBroadcastReceiver(context);


        View rootView = View.inflate(context, R.layout.bigimg_browse, null);

        mPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        tv_saveimg = (TextView) rootView.findViewById(R.id.tv_saveimg);
        tv_close = (ImageView) rootView.findViewById(R.id.tv_close);
        tv_num = (TextView) rootView.findViewById(R.id.tv_num);

        addView(rootView);
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
                .diskCache(new UnlimitedDiscCache(cacheDir)) // Ӳ�̻���Ŀ¼
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb Ӳ�̻���Ŀ¼��С
                .tasksProcessingOrder(QueueProcessingType.FIFO)// default
                .build();

        ImageLoader.getInstance().init(config);
    }
    /**
     * 动态添加View
     * @param str
     */
    public void autoAddView(String str){
        Button button = new Button(getContext());
        button.setText(str);
        addView(button);
    }

//以下代码修复通过动态 addView 后看不到的问题

    @Override
    public void requestLayout() {

//        Toast.makeText(_context,"requestLayout",0).show();
        super.requestLayout();
        post(measureAndLayout);
    }

    private final Runnable measureAndLayout = new Runnable() {
        @Override
        public void run() {
            measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

}
