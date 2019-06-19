package com.mogu.picturepreview;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class BigImgBrowse extends Activity implements ZoomImageView.ZoomImageViewInterface {
    private ViewPager mPager;
    private Map<String, View> ViewsMap;
    private String[] imgUrlArr;
    private LinearLayout.LayoutParams lParams = null;

    private BigImgBrowseAdapter adapter;
    public int currentItem = 0;


    float mStartX = 0;
    public ZoomImageView hy1;
    boolean drag = true;

    TextView tv_saveimg, tv_num;
    ImageView tv_close;

    private static Context mContext;
    private static Bitmap mBitmap;
    private static String saveDir = "saveImage";
    private static int compressQuality = 100;
    private static String saveMessage;
    private static String filePath;
    private static String suffix;
    private static String fileName;
    private static ProgressDialog mSaveDialog = null;

    boolean isShowSave;
    boolean isfinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigimg_browse);

        currentItem = getIntent().getIntExtra("currentIndex", 0);
        imgUrlArr = getIntent().getStringArrayExtra("imgUrlArr");
        isShowSave = getIntent().getBooleanExtra("isShowSave", false);

        mPager = (ViewPager) findViewById(R.id.viewpager);
        ViewsMap = new HashMap<String, View>();
        adapter = new BigImgBrowseAdapter(this, imgUrlArr, ViewsMap);
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(currentItem);
        mPager.setOnPageChangeListener(pageChange);

        tv_saveimg = (TextView) findViewById(R.id.tv_saveimg);
        tv_saveimg.setOnClickListener(MyListener);
        tv_close = (ImageView) findViewById(R.id.tv_close);
        tv_close.setOnClickListener(MyListener);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_num.setText(currentItem + 1 + "/" + imgUrlArr.length);

        if (isShowSave) {
            tv_saveimg.setVisibility(View.VISIBLE);
        } else {
            tv_saveimg.setVisibility(View.GONE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isfinish = true;
            }
        }, 1000);

    }

    private View.OnClickListener MyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            if (v == tv_close) {
                close_view();
                return;
            }
            if (v == tv_saveimg) {
                /** add by david 2019-6-13 start  */
                //添加权限判断
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                int permission_result = PermissionChecker.checkPermission(BigImgBrowse.this, perms[0], android.os.Process.myPid(), android.os.Process.myUid(), BigImgBrowse.this.getPackageName());
                if (permission_result != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) BigImgBrowse.this, perms, 100);
                    return;
                }
                /** add by david 2019-6-13 end  */

                String imgurl = imgUrlArr[currentItem];
//                saveImg(imgurl, "jpg");
                if (imgurl.startsWith("http") || imgurl.startsWith("https")) {
                    saveImg(imgurl, "jpg");
                } else {
                    Toast.makeText(com.mogu.picturepreview.BigImgBrowse.this, "该图片为本地图片,无需保存", 0).show();
                }
                return;
            }
        }
    };


    public void saveImg(String url, String imageSuffix) {

        mContext = BigImgBrowse.this;
        filePath = url;
        suffix = imageSuffix;
        // suffix = filePath.substring( filePath.lastIndexOf(".") + 1 );
        // if( suffix.indexOf("?") != -1 ){
        //     suffix = suffix.substring(0, suffix.indexOf("?"));
        // }
        mSaveDialog = ProgressDialog.show(mContext, "保存图片", "图片正在保存中...", true);
        new Thread(saveFileRunnable).start();
    }


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


    @Override
    public void close_view() {
        // finish();
        // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        /** modify by David at 2019-06-10 start   */
        // android 6.0 关闭后返回MainActivity 闪退问题

        if (isfinish) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        /** modify by David at 2019-06-10 end   */
    }

    /**
     * modify by David at 2019-06-10 start
     */
    // android 6.0 关闭后返回MainActivity 闪退问题
    @Override
    public void onBackPressed() {
//
        if (isfinish) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /**
     * modify by David at 2019-06-10 end
     */

    OnPageChangeListener pageChange = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            hy1 = (ZoomImageView) ViewsMap.get("" + arg0);

            currentItem = arg0;
//            AppUtils.noneselsect(arg0, imageViews);
            tv_num.setText(currentItem + 1 + "/" + imgUrlArr.length);

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

//    private float calculate(float x1, float x2) {// �ڵ�(x1,y1)�͵�(x2,y2)֮���õ�ǰ��ɫ���߶�
//
//        float pz = x1 - x2;// ���������ľ���
//        return pz;
//    }
}
