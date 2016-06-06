package com.jph.takephoto.uitl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Author: JPH
 * Date: 2015/8/26 0026 16:23
 */
public class TUtils {

    /**
     * 通过URI获取文件的路径
     * @param uri
     * @param activity
     * @return
     * @Author JPH
     * Date 2016/6/6 0006 20:01
     */
    public static String getFilePathWithUri(Uri uri, Activity activity) {
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
