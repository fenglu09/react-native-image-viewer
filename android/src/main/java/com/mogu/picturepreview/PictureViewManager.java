package com.mogu.picturepreview;

import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.image.ReactImageView;

import java.util.Map;

public class PictureViewManager extends ViewGroupManager<PicLayout>  {


    private final ReactApplicationContext _reactContext;
    private PicLayout mAgoraVideoLayout;

    public void close_view() {

    }


    public PictureViewManager(ReactApplicationContext reactContext) {
        _reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "AndroidPicPreview";
    }

    @Override
    protected PicLayout createViewInstance(ThemedReactContext reactContext) {
        mAgoraVideoLayout = new PicLayout(reactContext);
        return mAgoraVideoLayout;
    }

    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                "close", MapBuilder.of("registrationName", "close"));
    }

    @Override
    protected void addEventEmitters(ThemedReactContext reactContext, PicLayout view) {
        super.addEventEmitters(reactContext, view);

    }

    @ReactProp(name = "imageData")
    public void setimageData(PicLayout view, ReadableArray imageData) {
        String imgs[] = new String[imageData.size()];
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = imageData.getString(i);
        }
        view.setimageData(imgs);
    }

    @ReactProp(name = "index")
    public void setindex(PicLayout view, int index) {
        view.setindex(index);
    }


}
