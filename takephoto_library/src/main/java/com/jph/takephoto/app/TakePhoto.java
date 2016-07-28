package com.jph.takephoto.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TException;


/**
 - 支持通过相机拍照获取图片
 - 支持从相册选择图片
 - 支持从文件选择图片
 - 支持对图片进行压缩
 - 支持对图片进行裁剪
 - 支持对裁剪及压缩参数自定义
 - 提供自带裁剪工具(可选)
 - 支持智能选取及裁剪异常处理
 - 支持因拍照Activity被回收后的自动恢复
 * Author: JPH
 * Date: 2016/6/7 0007 15:10
 * Version:2.0.0
 */
public interface TakePhoto {
    /**
     * 从文件中获取图片（不裁剪）
     */
    void onPickFromDocuments();
    /**
     * 从文件中获取图片并裁剪
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options 裁剪配置
     */
    void onPickFromDocumentsWithCrop(Uri outPutUri, CropOptions options);
    /**
     * 从相册中获取图片（不裁剪）
     */
    void onPickFromGallery();
    /**
     * 从相册中获取图片并裁剪
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options 裁剪配置
     */
    void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options);

    /**
     * 从相机获取图片(不裁剪)
     * @param outPutUri 图片保存的路径
     */
    void onPickFromCapture(Uri outPutUri);
    /**
     * 从相机获取图片并裁剪
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options 裁剪配置             
     */
    void onPickFromCaptureWithCrop(Uri outPutUri, CropOptions options);

    /**
     * 裁剪图片
     * @param imageUri 要裁剪的图片
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options 裁剪配置
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