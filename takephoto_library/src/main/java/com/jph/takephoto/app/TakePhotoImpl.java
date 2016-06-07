package com.jph.takephoto.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.jph.takephoto.uitl.IntentUtils;
import com.jph.takephoto.uitl.TConstant;
import com.jph.takephoto.uitl.TUtils;

/**
 * 拍照及从图库选择照片框架
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 * Author: JPH
 * Date: 2016/6/7 0007 15:10
 */
public class TakePhotoImpl implements TakePhoto{
    private Activity activity;
    private TakeResultListener listener;
    private Uri outPutUri;
    private int cropHeight;
    private int cropWidth;

    public TakePhotoImpl(Activity activity, TakeResultListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState!=null){
            cropHeight=savedInstanceState.getInt("cropHeight");
            cropWidth=savedInstanceState.getInt("cropWidth");
            outPutUri=savedInstanceState.getParcelable("outPutUri");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("cropHeight",cropHeight);
        outState.putInt("cropWidth",cropWidth);
        outState.putParcelable("outPutUri",outPutUri);
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
                    String picturePath = TUtils.getFilePathWithUri(data.getData(), activity);
                    if (!TextUtils.isEmpty(picturePath)) {
                        listener.takeSuccess(picturePath);
                    } else {
                        listener.takeFail("文件没找到");
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
                    listener.takeSuccess(TUtils.getFilePathWithUri(outPutUri, activity));
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.PIC_CROP://裁剪照片
                if (resultCode == Activity.RESULT_OK) {
                    listener.takeSuccess(TUtils.getFilePathWithUri(outPutUri, activity));
                } else if (resultCode == Activity.RESULT_CANCELED) {//裁切的照片没有保存
                    if (data != null) {
                        Bitmap bitmap = data.getParcelableExtra("data");//获取裁切的结果数据
                        TUtils.writeToFile(bitmap, outPutUri);//将裁切的结果写入到文件
                        listener.takeSuccess(TUtils.getFilePathWithUri(outPutUri, activity));
                        Log.w("info", bitmap == null ? "null" : "not null");
                    } else {
                        listener.takeFail("没有获取到裁剪结果");
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

    private void cropImageUri(Uri imageUri) {
        onCropImageUri(imageUri, outPutUri, cropWidth, cropHeight);
    }
}