package com.jph.simple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.jph.takephoto.app.TakePhotoFragmentActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;

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
        CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create();
        CropOptions cropOptions=new CropOptions.Builder().setAspectX(1).setAspectY(1).create();
        switch (view.getId()) {
            case R.id.btnPickFromGallery://从相册选择照片不裁切
                getTakePhoto().onEnableCompress(compressConfig,true).onPickFromGallery();
                break;
            case R.id.btnPickFromGalleryWithCrop://从相册选择照片进行裁剪
                getTakePhoto().onEnableCompress(compressConfig,true).onPickFromGalleryWithCrop(imageUri,cropOptions);
                break;
            case R.id.btnPickFromCapture://从相机拍取照片不裁剪
                getTakePhoto().onEnableCompress(compressConfig,true).onPickFromCapture(imageUri);
                break;
            case R.id.btnPickFromCaptureWithCrop://从相机拍取照片进行裁剪
            getTakePhoto().onEnableCompress(compressConfig,true).onPickFromCaptureWithCrop(imageUri,cropOptions);
            break;
            case R.id.btnPickFromDocuments://从文件选择照片不裁剪
                getTakePhoto().onEnableCompress(compressConfig,true).onPickFromDocuments();
                break;
            case R.id.btnDocumentsCrop://从文件选择照片并裁剪
                getTakePhoto().onEnableCompress(compressConfig,true).onPickFromDocumentsWithCrop(imageUri,cropOptions);
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
