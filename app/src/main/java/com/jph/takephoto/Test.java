package com.jph.takephoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 *
 * @author JPH
 * @Date:2014.10.09
 */
public class Test extends TakePhotoActivity {
    private ImageView imgShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgShow= (ImageView) findViewById(R.id.imgShow);
    }
    public void cropPic(View view) {
        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), +System.currentTimeMillis() + ".jpg"));
        switch (view.getId()) {
            case R.id.btnCropFromGallery://从相册选择照片进行裁剪
                getTakePhoto().picSelectCrop(imageUri);
                break;
            case R.id.btnCropFromTake://从相机拍取照片进行裁剪
                getTakePhoto().picTakeCrop(imageUri);
                break;
            case R.id.btnOriginal://从相册选择照片不裁切
                getTakePhoto().picSelectOriginal(imageUri);
                break;
            case R.id.btnTakeOriginal://从相机拍取照片不裁剪
                getTakePhoto().picTakeOriginal(imageUri);
                break;
            default:
                break;
        }
    }
    @Override
    public void takeCancel() {
        super.takeCancel();
    }
    @Override
    public void takeFail(String msg) {
        super.takeFail(msg);
    }
    @Override
    public void takeSuccess(Uri uri) {
        super.takeSuccess(uri);
        showImg(uri);
    }
    private void showImg(Uri uri){
        BitmapFactory.Options option=new BitmapFactory.Options();
        option.inSampleSize=2;
        Bitmap bitmap=BitmapFactory.decodeFile(uri.getPath(),option);
        imgShow.setImageBitmap(bitmap);
    }
}
