package org.devio.takephoto.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.devio.takephoto.model.MultipleCrop;
import org.devio.takephoto.model.TException;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.model.TakePhotoOptions;


/**
 * - 支持通过相机拍照获取图片
 * - 支持从相册选择图片
 * - 支持从文件选择图片
 * - 支持多图选择
 * - 支持批量图片裁切
 * - 支持批量图片压缩
 * - 支持对图片进行压缩
 * - 支持对图片进行裁剪
 * - 支持对裁剪及压缩参数自定义
 * - 提供自带裁剪工具(可选)
 * - 支持智能选取及裁剪异常处理
 * - 支持因拍照Activity被回收后的自动恢复
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:4.0.0
 * 技术博文：http://www.devio.org
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */
public interface TakePhoto {
    /**
     * 图片多选
     *
     * @param limit 最多选择图片张数的限制
     */
    void onPickMultiple(int limit);

    /**
     * 图片多选，并裁切
     *
     * @param limit   最多选择图片张数的限制
     * @param options 裁剪配置
     */
    void onPickMultipleWithCrop(int limit, CropOptions options);

    /**
     * 从文件中获取图片（不裁剪）
     */
    void onPickFromDocuments();

    /**
     * 从文件中获取图片并裁剪
     *
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    void onPickFromDocumentsWithCrop(Uri outPutUri, CropOptions options);

    /**
     * 从相册中获取图片（不裁剪）
     */
    void onPickFromGallery();

    /**
     * 从相册中获取图片并裁剪
     *
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options);

    /**
     * 从相机获取图片(不裁剪)
     *
     * @param outPutUri 图片保存的路径
     */
    void onPickFromCapture(Uri outPutUri);

    /**
     * 从相机获取图片并裁剪
     *
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    void onPickFromCaptureWithCrop(Uri outPutUri, CropOptions options);

    /**
     * 裁剪图片
     *
     * @param imageUri  要裁剪的图片
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    void onCrop(Uri imageUri, Uri outPutUri, CropOptions options) throws TException;

    /**
     * 裁剪多张图片
     *
     * @param multipleCrop 要裁切的图片的路径以及输出路径
     * @param options      裁剪配置
     */
    void onCrop(MultipleCrop multipleCrop, CropOptions options) throws TException;

    void permissionNotify(PermissionManager.TPermissionType type);

    /**
     * 启用图片压缩
     *
     * @param config             压缩图片配置
     * @param showCompressDialog 压缩时是否显示进度对话框
     */
    void onEnableCompress(CompressConfig config, boolean showCompressDialog);

    /**
     * 设置TakePhoto相关配置
     *
     * @param options
     */
    void setTakePhotoOptions(TakePhotoOptions options);

    void onCreate(Bundle savedInstanceState);

    void onSaveInstanceState(Bundle outState);

    /**
     * 处理拍照或从相册选择的图片或裁剪的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 拍照结果监听接口
     */
    interface TakeResultListener {
        void takeSuccess(TResult result);

        void takeFail(TResult result, String msg);

        void takeCancel();
    }
}