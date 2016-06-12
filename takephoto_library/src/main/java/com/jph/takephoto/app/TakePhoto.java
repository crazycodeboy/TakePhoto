package com.jph.takephoto.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.jph.takephoto.compress.CompressConfig;

/**
 * 拍照及从图库选择照片框架
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 * Author: JPH
 * Date: 2016/6/7 0007 15:10
 */
public interface TakePhoto {
    /**
     * 处理拍照或裁剪结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 从相册选择原生的照片（不裁切）
     */
    void onPicSelectOriginal();

    /**
     * 从相册选择照片进行裁剪
     *
     * @param outPutUri 图片保存的路径
     */
    void  onPicSelectCrop(Uri outPutUri);

    /**
     * 从相册选择照片进行裁剪
     *
     * @param outPutUri  图片保存的路径
     * @param cropWidth  裁切宽度
     * @param cropHeight 裁切高度
     */
    void onPicSelectCrop(Uri outPutUri, int cropWidth, int cropHeight) ;

    /**
     * 拍取照片不裁切
     *
     * @param outPutUri 图片保存的路径
     */
    void onPicTakeOriginal(Uri outPutUri);
    /**
     * 从相机拍取照片进行裁剪
     *
     * @param outPutUri 图片保存的路径
     */
    void onPicTakeCrop(Uri outPutUri);

    /**
     * 从相机拍取照片进行裁剪
     *
     * @param outPutUri  图片保存的路径
     * @param cropWidth  裁切宽度
     * @param cropHeight 裁切高度
     */
    void onPicTakeCrop(Uri outPutUri, int cropWidth, int cropHeight);

    /**
     * 启用照片压缩
     * @return 压缩照片配置类
     */
    TakePhoto onEnableCompress(CompressConfig config,boolean showCompressDialog);
    /**
     * 裁剪指定uri对应的照片
     *
     * @param imageUri   uri对应的照片
     * @param outPutUri  裁切完成的照片
     * @param cropWidth  裁剪宽度
     * @param cropHeight 裁剪高度
     */
    void onCropImageUri(Uri imageUri, Uri outPutUri, int cropWidth, int cropHeight);
    void onCreate(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);
    /**
     * 拍照结果监听接口
     */
    interface TakeResultListener {
        void takeSuccess(String imagePath);

        void takeFail(String msg);

        void takeCancel();
    }
}