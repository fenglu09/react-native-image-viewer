package com.mogu.picturepreview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.react.bridge.ReactContext;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Map;


public class BigImgBrowseAdapter extends PagerAdapter {
    private Activity context;
    private String[] imgPathList;
    public Map<String, View> mListViews;
    private DisplayImageOptions options;

    public BigImgBrowseAdapter(Activity context, String[] imgPathList, Map<String, View> mListViews) {
        this.context = context;
        this.imgPathList = imgPathList;
        this.mListViews = mListViews;

        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imgPathList.length;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0.equals(arg1);
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
//        ((ViewPager) arg0).removeView(mListViews.get(arg1));
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        View view = LayoutInflater.from(context).inflate(R.layout.bigimgbrowse_item, null);
        final ZoomImageView hy = (ZoomImageView) view.findViewById(R.id.zoomImageView);
        final ProgressBar iv_loading = (ProgressBar) view.findViewById(R.id.iv_loading);
        final TextView tv_tip = (TextView) view.findViewById(R.id.tv_tip);

        String newImgUrl = imgPathList[position];

        /* add by david  ImageLoader未初始化处理 start*/
        boolean isInit = ImageLoader.getInstance().isInited();
        if (!isInit) {
            if(context!=null)
                ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        }
        /* add by david  ImageLoader未初始化处理 end*/
        ImageLoader.getInstance().displayImage(newImgUrl, hy, options,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                        //加载失败
                        iv_loading.setVisibility(View.GONE);
                        tv_tip.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View arg1,
                                                  Bitmap arg2) {
                        if (arg2 != null) {
                            //加载成功
                            iv_loading.setVisibility(View.GONE);
                            tv_tip.setVisibility(View.GONE);
//                            hy.setScreenSize(context, AppConst.getScreenWidth(context), AppConst.getScreenHeight(context), arg2);
                        } else {
                            //加载失败
                            iv_loading.setVisibility(View.GONE);
                            tv_tip.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                    }
                });


        mListViews.put("" + position, hy);
//        if (PicLayout.hy1
//                == null && position == PicLayout.
//                currentItem) {
//            PicLayout.hy1
//                    = hy;
//        }

        ((ViewPager) container).addView(view);
        return view;
    }

}

