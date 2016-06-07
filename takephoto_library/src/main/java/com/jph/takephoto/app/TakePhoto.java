package com.jph.takephoto.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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
 *
 * @author JPH
 *         Date 2015.08.04
 */
public class TakePhoto {
    private int cropHeight;
    private int cropWidth;
    private Activity activity;
    private TakeResultListener l;
    private Uri imageUri;

    public TakePhoto(Activity activity, TakeResultListener l) {
        this.activity = activity;
        this.l = l;
    }

    /**
     * 处理拍照或裁剪结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onResult(int requestCode, int resultCode, Intent data) {
        StringBuffer sb = new StringBuffer();
        sb.append("requestCode:").append(requestCode).append("--resultCode:").append(resultCode).append("--data:").append(data).append("--imageUri:").append(imageUri);
        Log.w("info", sb.toString());

        switch (requestCode) {
            case TConstant.PIC_SELECT_CROP:
                if (resultCode == Activity.RESULT_OK && data != null) {//从相册选择照片并裁切
                    cropImageUri(data.getData(), TConstant.PIC_CROP);
                } else {
                    l.takeCancel();
                }
                break;
            case TConstant.PIC_SELECT_ORIGINAL://从相册选择照片不裁切
                if (resultCode == Activity.RESULT_OK) {
                    String picturePath = TUtils.getFilePathWithUri(data.getData(), activity);
                    if (!TextUtils.isEmpty(picturePath)) {
                        l.takeSuccess(Uri.parse(picturePath));
                    } else {
                        l.takeFail("文件没找到");
                    }
                } else {
                    l.takeCancel();
                }
                break;
            case TConstant.PIC_TAKE_CROP://拍取照片,并裁切
                if (resultCode == Activity.RESULT_OK) {
                    cropImageUri(imageUri, TConstant.PIC_CROP);
                }
                break;
            case TConstant.PIC_TAKE_ORIGINAL://拍取照片
                if (resultCode == Activity.RESULT_OK) {
                    l.takeSuccess(imageUri);
                } else {
                    l.takeCancel();
                }
                break;
            case TConstant.PIC_CROP://裁剪照片
                if (resultCode == Activity.RESULT_OK) {
                    l.takeSuccess(imageUri);
                } else if (resultCode == Activity.RESULT_CANCELED) {//裁切的照片没有保存
                    if (data != null) {
                        Bitmap bitmap = data.getParcelableExtra("data");//获取裁切的结果数据
                        TUtils.writeToFile(bitmap, imageUri);//将裁切的结果写入到文件
                        l.takeSuccess(imageUri);
                        Log.w("info", bitmap == null ? "null" : "not null");
                    } else {
                        l.takeFail("没有获取到裁剪结果");
                    }
                } else {
                    l.takeCancel();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 从相册选择原生的照片（不裁切）
     */
    public void picSelectOriginal(Uri uri) {
        imageUri = uri;
        activity.startActivityForResult(IntentUtils.getPhotoPickIntent(), TConstant.PIC_SELECT_ORIGINAL);
    }

    /**
     * 从相册选择照片进行裁剪
     *
     * @param uri 图片保存的路径
     */
    public void picSelectCrop(Uri uri) {
        picSelectCrop(uri, TConstant.outputX, TConstant.outputY);
    }

    /**
     * 从相册选择照片进行裁剪
     *
     * @param uri        图片保存的路径
     * @param cropWidth  裁切宽度
     * @param cropHeight 裁切高度
     */
    public void picSelectCrop(Uri uri, int cropWidth, int cropHeight) {
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        imageUri = uri;
        activity.startActivityForResult(IntentUtils.getPhotoPickIntent(), TConstant.PIC_SELECT_CROP);
    }

    /**
     * 拍取照片不裁切
     *
     * @param uri 图片保存的路径
     */
    public void picTakeOriginal(Uri uri) {
        imageUri = uri;
        activity.startActivityForResult(IntentUtils.getPhotoCaptureIntent(imageUri), TConstant.PIC_TAKE_ORIGINAL);
    }

    /**
     * 从相机拍取照片进行裁剪
     *
     * @param uri 图片保存的路径
     */
    public void picTakeCrop(Uri uri) {
        picTakeCrop(uri, TConstant.outputX, TConstant.outputY);
    }

    /**
     * 从相机拍取照片进行裁剪
     *
     * @param uri        图片保存的路径
     * @param cropWidth  裁切宽度
     * @param cropHeight 裁切高度
     */
    public void picTakeCrop(Uri uri, int cropWidth, int cropHeight) {
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        imageUri = uri;
        activity.startActivityForResult(IntentUtils.getPhotoCaptureIntent(imageUri), TConstant.PIC_TAKE_CROP);
    }


    /**
     * 裁剪指定uri对应的照片
     *
     * @param imageUri：uri对应的照片
     * @param requestCode：请求码
     */
    private void cropImageUri(Uri imageUri, int requestCode) {
        activity.startActivityForResult(IntentUtils.getPhotoCropIntent(imageUri,this.imageUri,cropWidth,cropHeight), requestCode);
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    /**
     * 拍照结果监听接口
     */
    public interface TakeResultListener {
        void takeSuccess(Uri uri);

        void takeFail(String msg);

        void takeCancel();
    }
}