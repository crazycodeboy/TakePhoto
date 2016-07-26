package com.jph.takephoto.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.uitl.IntentUtils;
import com.jph.takephoto.uitl.TConstant;
import com.jph.takephoto.uitl.TException;
import com.jph.takephoto.uitl.TImageFiles;
import com.jph.takephoto.uitl.TUriParse;
import com.jph.takephoto.uitl.TUtils;

import java.io.FileNotFoundException;

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
    private int cropHeight;
    private int cropWidth;
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
            cropHeight=savedInstanceState.getInt("cropHeight");
            cropWidth=savedInstanceState.getInt("cropWidth");
            showCompressDialog=savedInstanceState.getBoolean("showCompressDialog");
            outPutUri=savedInstanceState.getParcelable("outPutUri");
            compressConfig=(CompressConfig)savedInstanceState.getSerializable("compressConfig");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("cropHeight",cropHeight);
        outState.putInt("cropWidth",cropWidth);
        outState.putBoolean("showCompressDialog",showCompressDialog);
        outState.putParcelable("outPutUri",outPutUri);
        outState.putSerializable("compressConfig",compressConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TConstant.PIC_SELECT_CROP:
                if (resultCode == Activity.RESULT_OK && data != null) {//从相册选择照片并裁切
                    cropImageUri(data.getData());
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.PIC_SELECT_ORIGINAL://从相册选择照片不裁切
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
            case TConstant.PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL://从文件选择照片不裁切
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
            case TConstant.PIC_TAKE_CROP://拍取照片,并裁切
                if (resultCode == Activity.RESULT_OK) {
                    cropImageUri(outPutUri);
                }
                break;
            case TConstant.PIC_TAKE_ORIGINAL://拍取照片
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
            case TConstant.PIC_CROP://裁剪照片
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
    public void onCropImageUri(Uri imageUri, Uri outPutUri, int cropWidth, int cropHeight) {
        activity.startActivityForResult(IntentUtils.getPhotoCropIntent(imageUri, outPutUri, cropWidth, cropHeight), TConstant.PIC_CROP);
    }

    @Override
    public void onPicFromDocuments() {
        activity.startActivityForResult(IntentUtils.getPickIntentWithDocuments(),TConstant.PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL);
    }

    @Override
    public void onPicSelectOriginal() {
        activity.startActivityForResult(IntentUtils.getPhotoPickIntent(), TConstant.PIC_SELECT_ORIGINAL);
    }

    @Override
    public void onPicSelectCrop(Uri outPutUri) {
        onPicSelectCrop(outPutUri,TConstant.outputX,TConstant.outputY);
    }

    @Override
    public void onPicSelectCrop(Uri outPutUri, int cropWidth, int cropHeight) {
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.outPutUri = outPutUri;
        activity.startActivityForResult(IntentUtils.getPhotoPickIntent(), TConstant.PIC_SELECT_CROP);
    }

    @Override
    public void onPicTakeOriginal(Uri outPutUri) {
        this.outPutUri = outPutUri;
        activity.startActivityForResult(IntentUtils.getPhotoCaptureIntent(this.outPutUri), TConstant.PIC_TAKE_ORIGINAL);
    }

    @Override
    public void onPicTakeCrop(Uri outPutUri) {
        onPicTakeCrop(outPutUri, TConstant.outputX, TConstant.outputY);
    }

    @Override
    public void onPicTakeCrop(Uri outPutUri, int cropWidth, int cropHeight) {
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.outPutUri = outPutUri;
        activity.startActivityForResult(IntentUtils.getPhotoCaptureIntent(this.outPutUri), TConstant.PIC_TAKE_CROP);
    }

    @Override
    public TakePhoto onEnableCompress(CompressConfig config,boolean showCompressDialog) {
        this.compressConfig=config;
        this.showCompressDialog=showCompressDialog;
        return this;
    }

    private void cropImageUri(Uri imageUri) {
        onCropImageUri(imageUri, outPutUri, cropWidth, cropHeight);
    }
    private void takeSuccess(final String picturePath){
        if (null==compressConfig){
            listener.takeSuccess(picturePath);
        }else {
            if (showCompressDialog)wailLoadDialog = TUtils.showProgressDialog(activity,"正在压缩照片...");
            new CompressImageImpl(compressConfig).compress(picturePath, new CompressImage.CompressListener() {
                @Override
                public void onCompressSuccessed(String imgPath) {
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