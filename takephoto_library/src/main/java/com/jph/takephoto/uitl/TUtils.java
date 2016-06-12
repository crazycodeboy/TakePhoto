package com.jph.takephoto.uitl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: JPH
 * Date: 2015/8/26 0026 16:23
 */
public class TUtils {
    private static final String TAG = IntentUtils.class.getName();
    /**
     * 是否裁剪之后返回数据
     **/
    public static boolean isReturnData() {
        String release= Build.VERSION.RELEASE;
        int sdk= Build.VERSION.SDK_INT;
        Log.i("ksdinf","release:"+release+"sdk:"+sdk);
        String manufacturer = android.os.Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer)) {
            if (manufacturer.toLowerCase().contains("lenovo")) {//对于联想的手机返回数据
                return true;
            }
        }
//        if (sdk>=21){//5.0或以上版本要求返回数据
//            return  true;
//        }
        return false;
    }
    /**
     * 将bitmap写入到文件
     *
     * @param bitmap
     */
    public static void writeToFile(Bitmap bitmap,Uri imageUri) {
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
     * 通过URI获取文件的路径
     * @param uri
     * @param activity
     * @return
     * @Author JPH
     * Date 2016/6/6 0006 20:01
     */
    public static String getFilePathWithUri(Uri uri, Activity activity) {
        if(uri==null){
            Log.w(TAG,"uri is null,activity may have been recovered?");
            return null;
        }
        String picturePath = null;
        String scheme=uri.getScheme();
        if ("content".equals(scheme)){
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri,
                    filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);  //获取照片路径
            cursor.close();
        }else if ("file".equals(scheme)){
            picturePath=uri.getPath();
        }
        return picturePath;
    }
    /**
     * 显示圆形进度对话框
     *
     * @author JPH
     * Date 2014-12-12 下午7:04:09
     * @param activity
     * @param progressTitle
     *            显示的标题
     * @return
     */
    public static ProgressDialog showProgressDialog(final Activity activity,
                                                    String... progressTitle) {
        if(activity==null||activity.isFinishing())return null;
        String title = "提示";
        if (progressTitle != null && progressTitle.length > 0)
            title = progressTitle[0];
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(title);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }
}
