package com.jph.takephoto.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TExceptionType;
import com.jph.takephoto.model.TIntentWap;
import com.jph.takephoto.uitl.IntentUtils;
import com.jph.takephoto.uitl.TConstant;
import com.jph.takephoto.model.TException;
import com.jph.takephoto.uitl.TImageFiles;
import com.jph.takephoto.uitl.TUriParse;
import com.jph.takephoto.uitl.TUtils;
import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;

/**
 * 拍照及从图库选择照片框架
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 * Author: JPH
 * Date: 2016/6/7 0007 15:10
 */
public class TakePhotoImpl implements TakePhoto{
    private static final String TAG = IntentUtils.class.getName();
    private Activity activity;
    private TakeResultListener listener;
    private Uri outPutUri;
    private CropOptions cropOptions;
    private CompressConfig compressConfig;
    /**
     * 是否显示压缩对话框
     */
    private boolean showCompressDialog;
    private ProgressDialog wailLoadDialog;
    public TakePhotoImpl(Activity activity, TakeResultListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState!=null){
            cropOptions= (CropOptions) savedInstanceState.getSerializable("cropOptions");
            showCompressDialog=savedInstanceState.getBoolean("showCompressDialog");
            outPutUri=savedInstanceState.getParcelable("outPutUri");
            compressConfig=(CompressConfig)savedInstanceState.getSerializable("compressConfig");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("cropOptions",cropOptions);
        outState.putBoolean("showCompressDialog",showCompressDialog);
        outState.putParcelable("outPutUri",outPutUri);
        outState.putSerializable("compressConfig",compressConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TConstant.RC_PICK_PICTURE_FROM_GALLERY_CROP:
                if (resultCode == Activity.RESULT_OK && data != null) {//从相册选择照片并裁切
                    try {
                        onCrop(data.getData(),outPutUri,cropOptions);
                    } catch (TException e) {
                        takeFail(e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL://从相册选择照片不裁切
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        takeSuccess(TUriParse.getFilePathWithUri(data.getData(), activity));
                    } catch (TException e) {
                        takeFail(e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL://从文件选择照片不裁切
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        takeSuccess(TUriParse.getFilePathWithDocumentsUri(data.getData(), activity));
                    } catch (TException e) {
                        takeFail(e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_CROP://从文件选择照片，并裁切
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        onCrop(data.getData(),outPutUri,cropOptions);
                    } catch (TException e) {
                        takeFail(e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP://拍取照片,并裁切
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        onCrop(outPutUri,outPutUri,cropOptions);
                    } catch (TException e) {
                        takeFail(e.getDetailMessage());
                        e.printStackTrace();
                    }
                }else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_CAPTURE://拍取照片
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        takeSuccess(TUriParse.getFilePathWithUri(outPutUri, activity));
                    } catch (TException e) {
                        takeFail(e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_CROP://裁剪照片返回结果
            case Crop.REQUEST_CROP://裁剪照片返回结果
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        takeSuccess(TUriParse.getFilePathWithUri(outPutUri, activity));
                    } catch (TException e) {
                        takeFail(e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {//裁切的照片没有保存
                    if (data != null) {
                        Bitmap bitmap = data.getParcelableExtra("data");//获取裁切的结果数据
                        TImageFiles.writeToFile(bitmap, outPutUri);//将裁切的结果写入到文件
                        try {
                            takeSuccess(TUriParse.getFilePathWithUri(outPutUri, activity));
                        } catch (TException e) {
                            takeFail(e.getDetailMessage());
                            e.printStackTrace();
                        }
                    } else {
                        takeFail("没有获取到裁剪结果");
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCrop(Uri imageUri, Uri outPutUri, CropOptions options)throws TException {
        if (!TImageFiles.checkMimeType(activity,TImageFiles.getMimeType(activity,imageUri))){
            Toast.makeText(activity,"选择的不是图片",Toast.LENGTH_SHORT).show();
            throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        }
        if (options.isWithOwnCrop()){
            TUtils.cropWithOwnApp(activity,imageUri,outPutUri,options);
        }else {
            TUtils.cropWithOtherAppBySafely(activity,imageUri,outPutUri,options);
        }
    }
    @Override
    public void onPickFromDocuments() {
        selectPicture(0,false);
    }
    @Override
    public void onPickFromGallery() {
        selectPicture(1,false);
    }
    private void selectPicture(int defaultIndex,boolean isCrop){
        ArrayList<TIntentWap>intentWapList=new ArrayList<>();
        intentWapList.add(new TIntentWap(IntentUtils.getPickIntentWithDocuments(),isCrop?TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_CROP:TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL));
        intentWapList.add(new TIntentWap(IntentUtils.getPickIntentWithGallery(),isCrop?TConstant.RC_PICK_PICTURE_FROM_GALLERY_CROP:TConstant.RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL));
        try {
            TUtils.sendIntentBySafely(activity,intentWapList,defaultIndex,isCrop);
        } catch (TException e) {
            takeFail(e.getDetailMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options) {
        this.cropOptions = options;
        this.outPutUri = outPutUri;
        selectPicture(1,true);
    }
    @Override
    public void onPickFromDocumentsWithCrop(Uri outPutUri, CropOptions options) {
        this.cropOptions = options;
        this.outPutUri = outPutUri;
        selectPicture(0,true);
    }

    @Override
    public void onPickFromCapture(Uri outPutUri) {
        this.outPutUri = outPutUri;
        activity.startActivityForResult(IntentUtils.getCaptureIntent(this.outPutUri), TConstant.RC_PICK_PICTURE_FROM_CAPTURE);
    }

    @Override
    public void onPickFromCaptureWithCrop(Uri outPutUri, CropOptions options) {
        this.cropOptions = options;
        this.outPutUri = outPutUri;
        activity.startActivityForResult(IntentUtils.getCaptureIntent(outPutUri), TConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP);
    }
    @Override
    public TakePhoto onEnableCompress(CompressConfig config,boolean showCompressDialog) {
        this.compressConfig=config;
        this.showCompressDialog=showCompressDialog;
        return this;
    }
    private void takeSuccess(final String picturePath){
        if (null==compressConfig){
            listener.takeSuccess(picturePath);
        }else {
            if (showCompressDialog)wailLoadDialog = TUtils.showProgressDialog(activity,"正在压缩照片...");
            new CompressImageImpl(compressConfig).compress(picturePath, new CompressImage.CompressListener() {
                @Override
                public void onCompressSuccess(String imgPath) {
                    listener.takeSuccess(imgPath);
                    if (wailLoadDialog!=null&&!activity.isFinishing())wailLoadDialog.dismiss();
                }
                @Override
                public void onCompressFailed(String imagePath,String msg) {
                    listener.takeFail(String.format("图片压缩失败:%s,picturePath:%s",msg,picturePath));
                    if (wailLoadDialog!=null&&!activity.isFinishing())wailLoadDialog.dismiss();
                }
            });
        }
    }
    private void takeFail(String message){
        listener.takeFail(message);
    }
}