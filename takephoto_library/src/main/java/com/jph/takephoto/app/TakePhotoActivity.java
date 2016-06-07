package com.jph.takephoto.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.jph.takephoto.uitl.CompressImageUtil;
import com.jph.takephoto.uitl.CompressImageUtil.CompressListener;
import com.jph.takephoto.uitl.TUtils;

/**
 * 继承这个类来让Activity获取拍照的能力<br>
 *
 * @author JPH
 * Date:2015.08.05
 */
public class TakePhotoActivity extends Activity implements TakePhoto.TakeResultListener,CompressListener {
    private TakePhoto takePhoto;
    protected ProgressDialog wailLoadDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     *  获取TakePhoto实例
     * @return
     */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto=new TakePhotoImpl(this,this);
        }
        return takePhoto;
    }
    @Override
    public void takeSuccess(String imagePath) {
        Log.i("info", "takeSuccess：" + imagePath);
    }
    @Override
    public void takeFail(String msg) {
        Log.w("info", "takeFail:" + msg);
    }
    @Override
    public void takeCancel() {
        Log.w("info", "用户取消");
    }
    /**
     * 压缩照片
     * @param path 照片路径
     */
    protected void compressPic(String path) {
        wailLoadDialog = TUtils.showProgressDialog(TakePhotoActivity.this,"正在压缩照片...");// 提交数据
        new CompressImageUtil().compressImageByPixel(path,this);
    }
    @Override
    public void onCompressSuccessed(String imgPath) {
        if (wailLoadDialog!=null&&wailLoadDialog.isShowing()&&!this.isFinishing())wailLoadDialog.dismiss();
    }
    @Override
    public void onCompressFailed(String msg) {
        if (wailLoadDialog!=null)wailLoadDialog.dismiss();
    }
}
