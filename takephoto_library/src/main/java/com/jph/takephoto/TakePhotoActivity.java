package com.jph.takephoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.jph.takephoto.uitl.CompressImageUtil;
import com.jph.takephoto.uitl.CompressImageUtil.CompressListener;
import com.jph.takephoto.uitl.TakePhoto;
import com.jph.takephoto.uitl.Utils;

/**
 * 继承这个类来让Activity获取拍照的能力<br>
 *
 * @author JPH
 * @Date:2015.08.05
 */
public class TakePhotoActivity extends Activity implements TakePhoto.TakeResultListener,CompressListener {
    private TakePhoto takePhoto;
    protected ProgressDialog wailLoadDialog;
    private CompressListener compressListener;
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
        getTakePhoto().onResult(requestCode, resultCode, data);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (takePhoto!=null)outState.putParcelable("imageUri", takePhoto.getImageUri());
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        getTakePhoto().setImageUri((Uri)savedInstanceState.getParcelable("imageUri"));
        super.onRestoreInstanceState(savedInstanceState);
    }
    /**
     * 压缩照片
     * @param path 照片路径
     * @param compressListener 压缩照片的监听器
     */
    protected void compressPic(String path,CompressListener compressListener) {
        this.compressListener=compressListener;
        wailLoadDialog = Utils.showProgressDialog(TakePhotoActivity.this,"正在压缩照片...");// 提交数据
        new CompressImageUtil().compressImageByPixel(path,this);
    }
    @Override
    public void onCompressSuccessed(String imgPath) {
        this.compressListener.onCompressSuccessed(imgPath);
        if (wailLoadDialog!=null)wailLoadDialog.dismiss();
    }
    @Override
    public void onCompressFailed(String msg) {
        this.compressListener.onCompressFailed(msg);
        if (wailLoadDialog!=null)wailLoadDialog.dismiss();
    }
}
