package com.jph.takephoto.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TException;

/**
 * 拍照及从图库选择图片框架
 * 从相册选择图片进行裁剪，从相机拍取图片进行裁剪<br>
 * 从相册选择图片（不裁切），并获取图片的路径<br>
 * 拍取图片（不裁切），并获取图片路径
 * Author: JPH
 * Date 2016/7/27 13:56
 * Version:1.0.3
 */
public interface TakePhoto {
    /**
     * 从文件中获取图片（不裁切）
     */
    void onPickFromDocuments();
    /**
     * 从文件中获取图片并裁切
     */
    void onPickFromDocumentsWithCrop(Uri outPutUri, CropOptions options);
    /**
     * 从相册中获取图片（不裁切）
     */
    void onPickFromGallery();
    /**
     * 从相册中获取图片并裁切
     */
    void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options);

    /**
     * 从相机获取图片(不裁切)
     * @param outPutUri 图片保存的路径
     */
    void onPickFromCapture(Uri outPutUri);
    /**
     * 从相机获取图片并裁切
     * @param outPutUri 图片保存的路径
     */
    void onPickFromCaptureWithCrop(Uri outPutUri, CropOptions options);

    /**
     * 裁切图片
     * @param imageUri 要裁切的图片
     * @param outPutUri 裁切好输出的图片
     * @param options 裁切配置
     */
    void onCrop(Uri imageUri, Uri outPutUri, CropOptions options)throws TException;
    /**
     * 启用图片压缩
     * @param config 压缩图片配置
     * @param showCompressDialog 压缩时是否显示进度对话框
     * @return
     */
    TakePhoto onEnableCompress(CompressConfig config,boolean showCompressDialog);
    void onCreate(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);
    /**
     * 处理拍照或从相册选择的图片或裁剪的结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);
    /**
     * 拍照结果监听接口
     */
    interface TakeResultListener {
        void takeSuccess(String imagePath);

        void takeFail(String msg);

        void takeCancel();
    }
}