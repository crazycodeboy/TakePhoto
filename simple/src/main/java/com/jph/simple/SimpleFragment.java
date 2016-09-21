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
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;


/**
 - 支持通过相机拍照获取图片
 - 支持从相册选择图片
 - 支持从文件选择图片
 - 支持多图选择
 - 支持批量图片裁切
 - 支持批量图片压缩
 - 支持对图片进行压缩
 - 支持对图片进行裁剪
 - 支持对裁剪及压缩参数自定义
 - 提供自带裁剪工具(可选)
 - 支持智能选取及裁剪异常处理
 - 支持因拍照Activity被回收后的自动恢复
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:3.0.0
 * 技术博文：http://www.cboy.me
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class SimpleFragment extends TakePhotoFragment {
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
            case R.id.btnPickMultiple://图片多选
                getTakePhoto().onPickMultiple(5);
                break;
            case R.id.btnPickMultipleCompress://图片多选并压缩
                getTakePhoto().onEnableCompress(compressConfig,true).onPickMultiple(5);
                break;
            case R.id.btnPickMultipleCrop://图片多选并裁切
                getTakePhoto().onPickMultipleWithCrop(5,cropOptions);
                break;
            case R.id.btnPickMultipleCropCompress://图片多选裁切并压缩
                getTakePhoto().onEnableCompress(compressConfig,true).onPickMultipleWithCrop(5,cropOptions);
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
        showImg(result.getImages());
    }
    private void showImg(ArrayList<TImage> images){
        LinearLayout linearLayout= (LinearLayout) getActivity().findViewById(R.id.llImages);
        for(int i=0,j=images.size();i<j-1;i+=2){
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.image_show,null);
            ImageView imageView1= (ImageView) view.findViewById(R.id.imgShow1);
            ImageView imageView2= (ImageView) view.findViewById(R.id.imgShow2);
            Glide.with(this).load(new File(images.get(i).getPath())).into(imageView1);
            Glide.with(this).load(new File(images.get(i+1).getPath())).into(imageView2);
            linearLayout.addView(view);
        }
        if(images.size()%2==1){
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.image_show,null);
            ImageView imageView1= (ImageView) view.findViewById(R.id.imgShow1);
            Glide.with(this).load(new File(images.get(images.size()-1).getPath())).into(imageView1);
            linearLayout.addView(view);
        }

    }
}
