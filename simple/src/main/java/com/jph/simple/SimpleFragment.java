package com.jph.simple;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import java.io.File;

/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 * Author JPH
 * Date 2016/6/7 0007 16:01
 */
public class SimpleFragment extends TakePhotoFragment {
    private ImageView imgShow;
    private ToggleButton toggleButton;
    private boolean withOwnCrop;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.simple_layout,null);
        imgShow= (ImageView) view.findViewById(R.id.imgShow);
        toggleButton= (ToggleButton) view.findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                withOwnCrop=isChecked;
            }
        });
        return view;
    }

    public void cropPic(View view) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create();
        CropOptions cropOptions=new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(withOwnCrop).create();
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
    public void takeFail(TResult result,String msg) {
        super.takeFail(result,msg);
    }
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImage().getPath());
    }
    private void showImg(String imagePath){
        BitmapFactory.Options option=new BitmapFactory.Options();
        option.inSampleSize=2;
        Bitmap bitmap=BitmapFactory.decodeFile(imagePath,option);
        imgShow.setImageBitmap(bitmap);
    }
}
