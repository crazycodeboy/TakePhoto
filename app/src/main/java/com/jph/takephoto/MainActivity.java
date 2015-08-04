package com.jph.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 *
 * @author JPH
 *         Date:2014.10.09
 *         last modified:2014.11.04
 */
public class MainActivity extends Activity {
    /**
     * request Code 从相册选择照片并裁切
     **/
    private final static int SELECT_PIC = 123;
    /**
     * request Code 从相册选择照片不裁切
     **/
    private final static int SELECT_ORIGINAL_PIC = 126;
    /**
     * request Code 拍取照片并裁切
     **/
    private final static int TAKE_PIC = 124;
    /**
     * request Code 拍取照片不裁切
     **/
    private final static int TAKE_ORIGINAL_PIC = 127;
    /**
     * request Code 裁切照片
     **/
    private final static int CROP_PIC = 125;
    private Uri imageUri;
    private ImageView imgShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化imageUri
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), +System.currentTimeMillis() + ".jpg"));
        imgShow = (ImageView) findViewById(R.id.imgShow);
        Log.w("info", "msg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        StringBuffer sb = new StringBuffer();
        sb.append("requestCode:").append(requestCode).append("--resultCode:").append(resultCode).append("--data:").append(data).append("--imageUri:").append(imageUri);
        Log.w("info", sb.toString());
        switch (requestCode) {
            case SELECT_PIC:
                if (resultCode == RESULT_OK) {//从相册选择照片并裁切
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));//将imageUri对象的图片加载到内存
                        imgShow.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_ORIGINAL_PIC://从相册选择照片不裁切
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                        imgShow.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case TAKE_PIC://拍取照片,并裁切
                if (resultCode == RESULT_OK) {
                    cropImageUri(imageUri, 600, 600, CROP_PIC);
                }
                break;
            case TAKE_ORIGINAL_PIC://拍取照片
                if (resultCode == RESULT_OK) {
                    String imgPath = imageUri.getPath();//获取拍摄照片路径
                    Uri uri = Uri.fromFile(new File(imgPath));
                    imgShow.setImageURI(uri);
                }
                break;
            case CROP_PIC://裁剪照片
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));//将imageUri对象的图片加载到内存
                        imgShow.setImageBitmap(bitmap);

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (resultCode == RESULT_CANCELED) {//裁切的照片没有保存
                    if (data != null) {
                        Bitmap bitmap2 = data.getParcelableExtra("data");//获取裁切的结果数据
                        imgShow.setImageBitmap(bitmap2);
                        Log.w("info", bitmap2 == null ? "null" : "not null");
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪指定uri对应的照片
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
        startActivityForResult(intent, requestCode);
    }

    public void cropPic(View view) {
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), +System.currentTimeMillis() + ".jpg"));
        switch (view.getId()) {
            case R.id.btnCropFromGallery://从相册选择照片进行裁剪
                cropFromGallery();
                break;
            case R.id.btnCropFromTake://从相机拍取照片进行裁剪
                cropFromTake();
                break;
            case R.id.btnOriginal://从相册选择照片不裁切
                selectFromGallery();
                break;
            case R.id.btnTakeOriginal://从相机拍取照片不裁剪
                selectFromTake();
                break;

            default:
                break;
        }
    }

    /**
     * 从相册选择原生的照片（不裁切）
     */
    private void selectFromGallery() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
        intent.setType("image/*");//从所有图片中进行选择
        startActivityForResult(intent, SELECT_ORIGINAL_PIC);
    }

    /**
     * 从相册选择照片进行裁剪
     */
    private void cropFromGallery() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
        intent.setType("image/*");//从所有图片中进行选择
        intent.putExtra("crop", "true");//设置为裁切
        intent.putExtra("aspectX", 1);//裁切的宽比例
        intent.putExtra("aspectY", 1);//裁切的高比例
        intent.putExtra("outputX", 600);//裁切的宽度
        intent.putExtra("outputY", 600);//裁切的高度
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将裁切的结果输出到指定的Uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//裁切成的图片的格式
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, SELECT_PIC);
    }

    /**
     * 拍取照片不裁切
     */
    private void selectFromTake() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, TAKE_ORIGINAL_PIC);
    }

    /**
     * 从相机拍取照片进行裁剪
     */
    private void cropFromTake() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, TAKE_PIC);
    }
}
