package com.jph.takephoto.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import com.jph.takephoto.R;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.MultipleCrop;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TException;
import com.jph.takephoto.model.TExceptionType;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TIntentWap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.uitl.ImageRotateUtil;
import com.jph.takephoto.uitl.IntentUtils;
import com.jph.takephoto.uitl.TConstant;
import com.jph.takephoto.uitl.TImageFiles;
import com.jph.takephoto.uitl.TUriParse;
import com.jph.takephoto.uitl.TUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import multipleimageselect.helpers.Constants;
import multipleimageselect.models.Image;

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
 * Date: 2016/9/21 0007 20:10
 * Version:3.0.0
 * 技术博文：http://www.cboy.me
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class TakePhotoImpl implements TakePhoto {
    private static final String TAG = IntentUtils.class.getName();
    private TContextWrap contextWrap;
    private TakeResultListener listener;
    private Uri outPutUri;
    private Uri tempUri;
    private CropOptions cropOptions;
    private TakePhotoOptions takePhotoOptions;
    private CompressConfig compressConfig;
    private MultipleCrop multipleCrop;
    private PermissionManager.TPermissionType permissionType;
    /**
     * 是否显示压缩对话框
     */
    private boolean showCompressDialog;
    private ProgressDialog wailLoadDialog;

    public TakePhotoImpl(Activity activity, TakeResultListener listener) {
        contextWrap = TContextWrap.of(activity);
        this.listener = listener;
    }

    public TakePhotoImpl(Fragment fragment, TakeResultListener listener) {
        contextWrap = TContextWrap.of(fragment);
        this.listener = listener;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            cropOptions = (CropOptions) savedInstanceState.getSerializable("cropOptions");
            takePhotoOptions = (TakePhotoOptions) savedInstanceState.getSerializable("takePhotoOptions");
            showCompressDialog = savedInstanceState.getBoolean("showCompressDialog");
            outPutUri = savedInstanceState.getParcelable("outPutUri");
            tempUri = savedInstanceState.getParcelable("tempUri");
            compressConfig = (CompressConfig) savedInstanceState.getSerializable("compressConfig");
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("cropOptions", cropOptions);
        outState.putSerializable("takePhotoOptions", takePhotoOptions);
        outState.putBoolean("showCompressDialog", showCompressDialog);
        outState.putParcelable("outPutUri", outPutUri);
        outState.putParcelable("tempUri", tempUri);
        outState.putSerializable("compressConfig", compressConfig);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TConstant.RC_PICK_PICTURE_FROM_GALLERY_CROP:
                if (resultCode == Activity.RESULT_OK && data != null) {//从相册选择照片并裁剪
                    try {
                        onCrop(data.getData(), outPutUri, cropOptions);
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL://从相册选择照片不裁剪
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        takeResult(TResult.of(TImage.of(
                            TUriParse.getFilePathWithUri(data.getData(), contextWrap.getActivity()))));
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(data.getData())), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL://从文件选择照片不裁剪
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        takeResult(TResult.of(TImage.of(
                            TUriParse.getFilePathWithDocumentsUri(data.getData(), contextWrap.getActivity()))));
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_CROP://从文件选择照片，并裁剪
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        onCrop(data.getData(), outPutUri, cropOptions);
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP://拍取照片,并裁剪
                if (resultCode == Activity.RESULT_OK) {
                    ImageRotateUtil.of().correctImage(contextWrap.getActivity(), tempUri);
                    takeResult(TResult.of(TImage.of(outPutUri)));
                    //try {
                    //  onCrop(tempUri,
                    //      Uri.fromFile(new File(TUriParse.parseOwnUri(contextWrap.getActivity(), outPutUri))),
                    //      cropOptions);
                    //} catch (TException e) {
                    //  takeResult(TResult.of(TImage.of(outPutUri)), e.getDetailMessage());
                    //  e.printStackTrace();
                    //}
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_CAPTURE://拍取照片
                if (resultCode == Activity.RESULT_OK) {
                    ImageRotateUtil.of().correctImage(contextWrap.getActivity(), outPutUri);
                    try {
                        takeResult(TResult.of(
                            TImage.of(TUriParse.getFilePathWithUri(outPutUri, contextWrap.getActivity()))));
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_CROP://裁剪照片返回结果
                //case Crop.REQUEST_CROP://裁剪照片返回结果
                if (resultCode == Activity.RESULT_OK) {
                    if (multipleCrop != null) {
                        cropContinue(true);
                    } else {
                        try {
                            TImage image =
                                TImage.of(TUriParse.getFilePathWithUri(outPutUri, contextWrap.getActivity()));
                            image.setCropped(true);
                            takeResult(TResult.of(image));
                        } catch (TException e) {
                            takeResult(TResult.of(TImage.of(outPutUri.getPath())), e.getDetailMessage());
                            e.printStackTrace();
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {//裁剪的照片没有保存
                    if (multipleCrop != null) {
                        if (data != null) {
                            Bitmap bitmap = data.getParcelableExtra("data");//获取裁剪的结果数据
                            TImageFiles.writeToFile(bitmap, outPutUri);//将裁剪的结果写入到文件
                            cropContinue(true);
                        } else {
                            cropContinue(false);
                        }
                    } else {
                        if (data != null) {
                            Bitmap bitmap = data.getParcelableExtra("data");//获取裁剪的结果数据
                            TImageFiles.writeToFile(bitmap, outPutUri);//将裁剪的结果写入到文件

                            TImage image = TImage.of(outPutUri.getPath());
                            image.setCropped(true);
                            takeResult(TResult.of(image));
                        } else {
                            listener.takeCancel();
                        }
                    }
                } else {
                    if (multipleCrop != null) {
                        cropContinue(false);
                    } else {
                        listener.takeCancel();
                    }
                }
                break;
            case TConstant.RC_PICK_MULTIPLE://多选图片返回结果
                if (resultCode == Activity.RESULT_OK && data != null) {
                    boolean startCamera = data.getBooleanExtra(Constants.INTENT_EXTRA_START_CAMERA, false);
                    if (startCamera) {
                        File file = new File(Environment.getExternalStorageDirectory(),
                            "/temp/" + System.currentTimeMillis() + ".jpg");
                        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                        Uri imageUri = Uri.fromFile(file);
                        onPickFromCaptureWithCrop(imageUri, null);
                    } else {
                        ArrayList<Image> images =
                            data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                        if (cropOptions != null) {
                            try {
                                onCrop(MultipleCrop.of(
                                    TUtils.convertImageToUri(contextWrap.getActivity(), images),
                                    contextWrap.getActivity()), cropOptions);
                            } catch (TException e) {
                                cropContinue(false);
                                e.printStackTrace();
                            }
                        } else {
                            takeResult(TResult.of(TUtils.getTImagesWithImages(images)));
                        }
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            default:
                break;
        }
    }

    @Override public void onPickMultiple(boolean cameraEnable, int limit) {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        TUtils.startActivityForResult(contextWrap,
            new TIntentWap(IntentUtils.getPickMultipleIntent(contextWrap, cameraEnable, limit),
                TConstant.RC_PICK_MULTIPLE));
    }

    @Override
    public void onPickMultipleWithCrop(boolean cameraEnable, int limit, CropOptions options) {
        onPickMultiple(cameraEnable, limit);
        this.cropOptions = options;
    }

    /**
     * -----crop------
     **/
    @Override public void onCrop(Uri imageUri, Uri outPutUri, CropOptions options) throws TException {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        this.outPutUri = outPutUri;
        if (!TImageFiles.checkMimeType(contextWrap.getActivity(),
            TImageFiles.getMimeType(contextWrap.getActivity(), imageUri))) {
            Toast.makeText(contextWrap.getActivity(),
                contextWrap.getActivity().getResources().getText(R.string.tip_type_not_image),
                Toast.LENGTH_SHORT).show();
            throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        }
        cropWithNonException(imageUri, outPutUri, options);
    }

    @Override public void onCrop(MultipleCrop multipleCrop, CropOptions options) throws TException {
        this.multipleCrop = multipleCrop;
        onCrop(multipleCrop.getUris().get(0), multipleCrop.getOutUris().get(0), options);
    }

    private void cropWithNonException(Uri imageUri, Uri outPutUri, CropOptions options) {
        this.outPutUri = outPutUri;
        if (options.isWithOwnCrop()) {
            //TUtils.cropWithOwnApp(contextWrap, imageUri, outPutUri, options);
        } else {
            TUtils.cropWithOtherAppBySafely(contextWrap, imageUri, outPutUri, options);
        }
    }

    private void cropContinue(boolean preSuccess) {
        Map result = multipleCrop.setCropWithUri(outPutUri, preSuccess);
        int index = (int) result.get("index");
        boolean isLast = (boolean) result.get("isLast");

        if (isLast) {
            if (preSuccess) {
                takeResult(TResult.of(multipleCrop.gettImages()));
            } else {
                takeResult(TResult.of(multipleCrop.gettImages()),
                    outPutUri.getPath() + contextWrap.getActivity()
                        .getResources()
                        .getString(R.string.msg_crop_canceled));
            }
        } else {
            cropWithNonException(multipleCrop.getUris().get(index + 1),
                multipleCrop.getOutUris().get(index + 1), cropOptions);
        }
    }

    @Override public void onPickFromDocuments() {
        selectPicture(0, false);
    }

    @Override public void onPickFromGallery() {
        selectPicture(1, false);
    }

    private void selectPicture(int defaultIndex, boolean isCrop) {
        if (takePhotoOptions != null && takePhotoOptions.isWithOwnGallery()) {
            onPickMultiple(false, 1);
            return;
        }
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        ArrayList<TIntentWap> intentWapList = new ArrayList<>();
        intentWapList.add(new TIntentWap(IntentUtils.getPickIntentWithDocuments(),
            isCrop ? TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_CROP
                : TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL));
        intentWapList.add(new TIntentWap(IntentUtils.getPickIntentWithGallery(),
            isCrop ? TConstant.RC_PICK_PICTURE_FROM_GALLERY_CROP
                : TConstant.RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL));
        try {
            TUtils.sendIntentBySafely(contextWrap, intentWapList, defaultIndex, isCrop);
        } catch (TException e) {
            takeResult(TResult.of(TImage.of("")), e.getDetailMessage());
            e.printStackTrace();
        }
    }

    @Override public void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options) {
        this.cropOptions = options;
        this.outPutUri = outPutUri;
        selectPicture(1, true);
    }

    @Override public void onPickFromDocumentsWithCrop(Uri outPutUri, CropOptions options) {
        this.cropOptions = options;
        this.outPutUri = outPutUri;
        selectPicture(0, true);
    }

    @Override public void onPickFromCapture(Uri outPutUri) {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        if (Build.VERSION.SDK_INT >= 23) {
            this.outPutUri =
                TUriParse.convertFileUriToFileProviderUri(contextWrap.getActivity(), outPutUri);
        } else {
            this.outPutUri = outPutUri;
        }

        try {
            TUtils.captureBySafely(contextWrap,
                new TIntentWap(IntentUtils.getCaptureIntent(this.outPutUri),
                    TConstant.RC_PICK_PICTURE_FROM_CAPTURE));
        } catch (TException e) {
            takeResult(TResult.of(TImage.of("")), e.getDetailMessage());
            e.printStackTrace();
        }
    }

    @Override public void onPickFromCaptureWithCrop(Uri outPutUri, CropOptions options) {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        this.cropOptions = options;
        this.outPutUri = outPutUri;
        if (Build.VERSION.SDK_INT >= 23) {
            this.tempUri = TUriParse.getTempUri(contextWrap.getActivity());
        } else {
            this.tempUri = outPutUri;
        }

        try {
            TUtils.captureBySafely(contextWrap, new TIntentWap(IntentUtils.getCaptureIntent(this.tempUri),
                TConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP));
        } catch (TException e) {
            takeResult(TResult.of(TImage.of("")), e.getDetailMessage());
            e.printStackTrace();
        }
    }

    @Override public void onEnableCompress(CompressConfig config, boolean showCompressDialog) {
        this.compressConfig = config;
        this.showCompressDialog = showCompressDialog;
    }

    @Override public void setTakePhotoOptions(TakePhotoOptions options) {
        this.takePhotoOptions = options;
    }

    @Override public void permissionNotify(PermissionManager.TPermissionType type) {
        this.permissionType = type;
    }

    private void takeResult(final TResult result, final String... message) {
        if (null == compressConfig) {
            handleTakeCallBack(result, message);
        } else {
            if (showCompressDialog) {
                wailLoadDialog = TUtils.showProgressDialog(contextWrap.getActivity(),
                    contextWrap.getActivity().getResources().getString(R.string.tip_compress));
            }

            CompressImageImpl.of(contextWrap.getActivity(), compressConfig, result.getImages(),
                new CompressImage.CompressListener() {
                    @Override public void onCompressSuccess(ArrayList<TImage> images) {
                        handleTakeCallBack(result);
                        if (wailLoadDialog != null && !contextWrap.getActivity().isFinishing()) {
                            wailLoadDialog.dismiss();
                        }
                    }

                    @Override public void onCompressFailed(ArrayList<TImage> images, String msg) {
                        handleTakeCallBack(TResult.of(images), String.format(
                            contextWrap.getActivity().getResources().getString(R.string.tip_compress_failed),
                            message.length > 0 ? message[0] : "", msg, result.getImage().getPath()));
                        if (wailLoadDialog != null && !contextWrap.getActivity().isFinishing()) {
                            wailLoadDialog.dismiss();
                        }
                    }
                }).compress();
        }
    }

    private void handleTakeCallBack(final TResult result, String... message) {
        if (message.length > 0) {
            listener.takeFail(result, message[0]);
        } else if (multipleCrop != null && multipleCrop.hasFailed) {
            listener.takeFail(result,
                contextWrap.getActivity().getResources().getString(R.string.msg_crop_failed));
        } else if (compressConfig != null) {
            boolean hasFailed = false;
            for (TImage image : result.getImages()) {
                if (image == null || !image.isCompressed()) {
                    hasFailed = true;
                    break;
                }
            }
            if (hasFailed) {
                listener.takeFail(result,
                    contextWrap.getActivity().getString(R.string.msg_compress_failed));
            } else {
                listener.takeSuccess(result);
            }
        } else {
            listener.takeSuccess(result);
        }
        clearParams();
    }

    private void clearParams() {
        compressConfig = null;
        takePhotoOptions = null;
        cropOptions = null;
        multipleCrop = null;
    }
}