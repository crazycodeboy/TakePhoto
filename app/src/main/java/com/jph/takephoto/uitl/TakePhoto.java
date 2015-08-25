package com.jph.takephoto.uitl;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 拍照及从图库选择照片框架
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 *
 * @author JPH
 * @date 2015.08.04
 */
public class TakePhoto {
    /**
     * request Code 从相册选择照片并裁切
     **/
    public final static int PIC_SELECT_CROP = 123;
    /**
     * request Code 从相册选择照片不裁切
     **/
    public final static int PIC_SELECT_ORIGINAL = 126;
    /**
     * request Code 拍取照片并裁切
     **/
    public final static int PIC_TAKE_CROP = 124;
    /**
     * request Code 拍取照片不裁切
     **/
    public final static int PIC_TAKE_ORIGINAL = 127;
    /**
     * request Code 裁切照片
     **/
    public final static int PIC_CROP = 125;
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
            case PIC_SELECT_CROP:
                if (resultCode == Activity.RESULT_OK) {//从相册选择照片并裁切
                    l.takeSuccess(imageUri);
                } else {
                    l.takeCancel();
                }
                break;
            case PIC_SELECT_ORIGINAL://从相册选择照片不裁切
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = activity.getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);  //获取照片路径
                    cursor.close();
                    if (!TextUtils.isEmpty(picturePath)) {
                        l.takeSuccess(Uri.parse(picturePath));
                    } else {
                        l.takeFail("文件没找到");
                    }
                } else {
                    l.takeCancel();
                }
                break;
            case PIC_TAKE_CROP://拍取照片,并裁切
                if (resultCode == Activity.RESULT_OK) {
                    cropImageUri(imageUri, 480, 480, PIC_CROP);
                }
                break;
            case PIC_TAKE_ORIGINAL://拍取照片
                if (resultCode == Activity.RESULT_OK) {
                    l.takeSuccess(imageUri);
                } else {
                    l.takeCancel();
                }
                break;
            case PIC_CROP://裁剪照片
                if (resultCode == Activity.RESULT_OK) {
                    l.takeSuccess(imageUri);
                } else if (resultCode == Activity.RESULT_CANCELED) {//裁切的照片没有保存
                    if (data != null) {
                        Bitmap bitmap = data.getParcelableExtra("data");//获取裁切的结果数据
                        //将裁切的结果写入到文件
                        writeToFile(bitmap);
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
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
        intent.setType("image/*");//从所有图片中进行选择
        activity.startActivityForResult(intent, PIC_SELECT_ORIGINAL);
    }

    /**
     * 从相册选择照片进行裁剪
     *
     * @param uri    图片保存的路径
     * @param with   裁切的宽度
     * @param height 裁切的高度
     */
    public void picSelectCrop(Uri uri, int with, int height) {
        imageUri = uri;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
        intent.setType("image/*");//从所有图片中进行选择
        intent.putExtra("crop", "true");//设置为裁切
        intent.putExtra("aspectX", 1);//裁切的宽比例
        intent.putExtra("aspectY", 1);//裁切的高比例
        intent.putExtra("outputX", with);//裁切的宽度
        intent.putExtra("outputY", height);//裁切的高度
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将裁切的结果输出到指定的Uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//裁切成的图片的格式
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, PIC_SELECT_CROP);
    }

    /**
     * 从相册选择照片进行裁剪(裁切图片大小600*600)
     *
     * @param uri 图片保存的路径
     */
    public void picSelectCrop(Uri uri) {
        imageUri = uri;
        picSelectCrop(uri, 600, 600);
    }

    /**
     * 拍取照片不裁切
     *
     * @param uri 图片保存的路径
     */
    public void picTakeOriginal(Uri uri) {
        imageUri = uri;
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//将拍取的照片保存到指定URI
        activity.startActivityForResult(intent, PIC_TAKE_ORIGINAL);
    }

    /**
     * 从相机拍取照片进行裁剪
     *
     * @param uri 图片保存的路径
     */
    public void picTakeCrop(Uri uri) {
        imageUri = uri;
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
//        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);//设置竖屏
        activity.startActivityForResult(intent, PIC_TAKE_CROP);
    }

    /**
     * 裁剪指定uri对应的照片
     *
     * @param imageUri：uri对应的照片
     * @param outputX：裁剪宽
     * @param outputY：裁剪高
     * @param requestCode：请求码
     */
    private void cropImageUri(Uri imageUri, int outputX, int outputY, int requestCode) {
        boolean isReturnData = isReturnData();
        Log.w("ksdinf","isReturnData:"+( isReturnData ? "true" : "false"));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", isReturnData);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 是否裁剪之后返回数据
     **/
    private boolean isReturnData() {
       String release= Build.VERSION.RELEASE;
       int sdk= Build.VERSION.SDK_INT;
        Log.i("ksdinf","release:"+release+"sdk:"+sdk);
//        String manufacturer = android.os.Build.MANUFACTURER;
//        if (!TextUtils.isEmpty(manufacturer)) {
//            if (manufacturer.toLowerCase().contains("lenovo")) {//对于联想的手机返回数据
//                return true;
//            }
//        }
        if (sdk>=21){//5.0或以上版本要求返回数据
            return  true;
        }
        return false;
    }

    /**
     * 将bitmap写入到文件
     *
     * @param bitmap
     */
    private void writeToFile(Bitmap bitmap) {
        if (bitmap == null) return;
        File file = new File(imageUri.getPath());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bos.toByteArray());
            bos.flush();
            fos.flush();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) try {
                fos.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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