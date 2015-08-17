package com.jph.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.jph.takephoto.uitl.TakePhoto;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 继承这个类来让Activity获取拍照的能力<br>
 * @author JPH
 * @Date:2015.08.05
 */
public class TakePhotoActivity extends Activity implements TakePhoto.TakeResultListener {
    private TakePhoto takePhoto;
    /**
     *  获取TakePhoto实例
     * @return
     */
    protected TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto=new TakePhoto(this,this);
        }
        return takePhoto;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        takePhoto.onResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void takeSuccess(Uri uri) {
        Log.i("info", "takeSuccess：" + uri);
    }
    @Override
    public void takeFail(String msg) {
        Log.w("info", "takeFail:" + msg);
    }
    @Override
    public void takeCancel() {
        Log.w("info", "用户取消");
    }
}
