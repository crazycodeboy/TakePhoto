package com.jph.takephoto;

import android.app.Activity;
import android.content.Intent;
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
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 *
 * @author JPH
 *         Date:2014.10.09
 *         last modified:2014.11.04
 */
public class MainActivity extends Activity {
    private Uri imageUri;
    private ImageView imgShow;
    private TakePhoto takePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgShow = (ImageView) findViewById(R.id.imgShow);
        takePhoto=new TakePhoto(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap=takePhoto.onResult(requestCode,resultCode,data);
        imgShow.setImageBitmap(bitmap);
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void cropPic(View view) {
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), +System.currentTimeMillis() + ".jpg"));
        switch (view.getId()) {
            case R.id.btnCropFromGallery://从相册选择照片进行裁剪
                takePhoto.picSelectCrop(imageUri);
                break;
            case R.id.btnCropFromTake://从相机拍取照片进行裁剪
                takePhoto.picTakeCrop(imageUri);
                break;
            case R.id.btnOriginal://从相册选择照片不裁切
                takePhoto.picSelectOriginal(imageUri);
                break;
            case R.id.btnTakeOriginal://从相机拍取照片不裁剪
                takePhoto.picTakeOriginal(imageUri);
                break;

            default:
                break;
        }
    }
}
