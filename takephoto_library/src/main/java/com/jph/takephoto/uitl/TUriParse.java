package com.jph.takephoto.uitl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Uri解析工具类
 * Author: JPH
 * Date: 2015/8/26 0026 16:23
 */
public class TUriParse {
    private static final String TAG = IntentUtils.class.getName();
    /**
     * 通过URI获取文件的路径
     * @param uri
     * @param activity
     * @return
     * @Author JPH
     * Date 2016/6/6 0006 20:01
     */
    public static String getFilePathWithUri(Uri uri, Activity activity)throws TException{
        if(uri==null){
            Log.w(TAG,"uri is null,activity may have been recovered?");
            throw new TException(TExceptionType.TYPE_URI_NULL);
        }
        String picturePath = null;
        String scheme=uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)){
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri,
                    filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);  //获取照片路径
            cursor.close();
        }else if (ContentResolver.SCHEME_FILE.equals(scheme)){
            picturePath=uri.getPath();
        }
        if (TextUtils.isEmpty(picturePath))throw new TException(TExceptionType.TYPE_URI_PARSE_FAIL);
        if (!TImageFiles.checkMimeType(TImageFiles.getMimeType(activity,uri)))throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        return picturePath;
    }
    /**
     * 通过从文件中得到的URI获取文件的路径
     * @param uri
     * @param activity
     * @return
     * @Author JPH
     * Date 2016/6/6 0006 20:01
     */
    public static String getFilePathWithDocumentsUri(Uri uri,Activity activity) throws TException {
        if(uri==null){
            Log.e(TAG,"uri is null,activity may have been recovered?");
            return null;
        }
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())&&uri.getPath().contains("document")){
            File tempFile = TImageFiles.getTempFile(activity,uri);
            try {
                TImageFiles.inputStreamToFile(activity.getContentResolver().openInputStream(uri),tempFile);
                return tempFile.getPath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new TException(TExceptionType.TYPE_NO_FIND);
            }
        }else {
            return getFilePathWithUri(uri,activity);
        }
    }
}
