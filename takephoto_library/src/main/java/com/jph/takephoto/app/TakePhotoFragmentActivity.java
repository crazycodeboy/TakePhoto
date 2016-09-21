package com.jph.takephoto.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.jph.takephoto.model.TResult;

/**
 * 继承这个类来让Activity获取拍照的能力<br>
 * @author JPH
 * Date:2015.08.05
 */
public class TakePhotoFragmentActivity extends FragmentActivity implements TakePhoto.TakeResultListener{
    private TakePhoto takePhoto;
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
    public void takeSuccess(TResult result) {
        Log.i("info", "takeSuccess：" + result.getImage().getPath());
    }
    @Override
    public void takeFail(TResult result,String msg) {
        Log.w("info", "takeFail:" + msg);
    }
    @Override
    public void takeCancel() {
        Log.w("info", "用户取消");
    }
}
