package com.jph.takephoto.uitl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Intent工具类用于生成拍照、
 * 从相册选择照片，裁切照片所需的Intent
 * Author: JPH
 * Date: 2016/6/7 0007 13:41
 */
public class IntentUtils {
    private static final String TAG = IntentUtils.class.getName();

    /**
     * 获取裁切照片的Intent
     * @param targetUri 要裁切的照片
     * @param outPutUri 裁切完成的照片
     * @param cropWidth 裁切之后的宽度
     * @param cropHeight 裁切之后的高度
     * @return
     */
    public static Intent getPhotoCropIntent(Uri targetUri,Uri outPutUri, int cropWidth,int cropHeight) {
        boolean isReturnData = TUtils.isReturnData();
        Log.w(TAG, "getPhotoCropIntent:isReturnData:" + (isReturnData ? "true" : "false"));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(targetUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", cropWidth);
        intent.putExtra("aspectY", cropHeight);
        intent.putExtra("outputX", cropWidth);
        intent.putExtra("outputY", cropHeight);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        intent.putExtra("return-data", isReturnData);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        return intent;
    }

    /**
     * 获取拍照的Intent
     * @return
     */
    public static Intent getPhotoCaptureIntent(Uri imageUri) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        return intent;
    }
    /**
     * 获取选择照片的Intent
     * @return
     */
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
        intent.setType("image/*");//从所有图片中进行选择
        return intent;
    }
}
