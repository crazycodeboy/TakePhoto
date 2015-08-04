package com.jph.takephoto.uitl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;

import com.jph.takephoto.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
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
    private Uri imageUri;

    public TakePhoto(Activity activity, Uri imageUri) {
        this.activity = activity;
        this.imageUri = imageUri;
    }

    public Bitmap onResult(int requestCode, int resultCode, Intent data) {
        StringBuffer sb = new StringBuffer();
        sb.append("requestCode:").append(requestCode).append("--resultCode:").append(resultCode).append("--data:").append(data).append("--imageUri:").append(imageUri);
        Log.w("info", sb.toString());
        Bitmap bitmap = null;
        switch (requestCode) {
            case PIC_SELECT_CROP:
                if (resultCode == Activity.RESULT_OK) {//从相册选择照片并裁切
                    try {
                        bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri));//将imageUri对象的图片加载到内存
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case PIC_SELECT_ORIGINAL://从相册选择照片不裁切
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = activity.getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        bitmap = BitmapFactory.decodeFile(picturePath);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case PIC_TAKE_CROP://拍取照片,并裁切
                if (resultCode == Activity.RESULT_OK) {
                    cropImageUri(imageUri, 600, 600, PIC_CROP);
                }
                break;
            case PIC_TAKE_ORIGINAL://拍取照片
                if (resultCode == Activity.RESULT_OK) {
                    String imgPath = imageUri.getPath();//获取拍摄照片路径
                    bitmap = BitmapFactory.decodeFile(imgPath);
                }
                break;
            case PIC_CROP://裁剪照片
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(activity.getContentResolver().
                                openInputStream(imageUri));//将imageUri对象的图片加载到内存

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {//裁切的照片没有保存
                    if (data != null) {
                        bitmap = data.getParcelableExtra("data");//获取裁切的结果数据
                        //将裁切的结果写入到文件
                        writeToFile(bitmap);
                        Log.w("info", bitmap == null ? "null" : "not null");
                    }
                }
                break;
            default:
                break;
        }
        return bitmap;
    }
    /**
     * 将bitmap写入到文件
     * @param bitmap
     * */
    private void writeToFile(Bitmap bitmap) {
        File file=new File(imageUri.getPath());
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(file);
            fos.write(bos.toByteArray());
            bos.flush();
            fos.flush();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }finally {
            if (fos!=null) try {
                fos.close();
               if(bos!=null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 从相册选择原生的照片（不裁切）
     */
    public void picSelectOriginal() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
        intent.setType("image/*");//从所有图片中进行选择
        activity.startActivityForResult(intent, PIC_SELECT_ORIGINAL);
    }

    /**
     * 从相册选择照片进行裁剪
     *
     * @param with   裁切的宽度
     * @param height 裁切的高度
     */
    public void picSelectCrop(int with, int height) {
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
     */
    public void picSelectCrop() {
        picSelectCrop(600, 600);
    }

    /**
     * 拍取照片不裁切
     */
    public void picTakeOriginal() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        activity.startActivityForResult(intent, PIC_TAKE_ORIGINAL);
    }

    /**
     * 从相机拍取照片进行裁剪
     */
    public void picTakeCrop() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
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
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }
}