package com.jph.simple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.app.TakePhotoFragmentActivity;
import com.jph.takephoto.compress.CompressConfig;

import java.io.File;

/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 * Author JPH
 * Date 2016/6/7 0007 16:01
 */
public class MainActivity extends TakePhotoFragmentActivity {
    private ImageView imgShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgShow= (ImageView) findViewById(R.id.imgShow);
    }
    public void cropPic(View view) {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        switch (view.getId()) {
            case R.id.btnCropFromGallery://从相册选择照片进行裁剪
                getTakePhoto().onPicSelectCrop(imageUri);
                break;
            case R.id.btnCropFromTake://从相机拍取照片进行裁剪
                getTakePhoto().onPicTakeCrop(imageUri);
                break;
            case R.id.btnOriginal://从相册选择照片不裁切
                getTakePhoto().onPicSelectOriginal();
                break;
            case R.id.btnTakeOriginal://从相机拍取照片不裁剪
                getTakePhoto().onEnableCompress(CompressConfig.getDefaultConfig().setShowCompressDialog(true)).onPicTakeOriginal(imageUri);
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
    public void takeSuccess(String imagePath) {
        super.takeSuccess(imagePath);
        showImg(imagePath);
    }
    private void showImg(String imagePath){
        BitmapFactory.Options option=new BitmapFactory.Options();
        option.inSampleSize=2;
        Bitmap bitmap=BitmapFactory.decodeFile(imagePath,option);
        imgShow.setImageBitmap(bitmap);
    }
}
