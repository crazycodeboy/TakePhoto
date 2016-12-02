package com.jph.takephoto.uitl;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.jph.takephoto.model.TException;
import com.jph.takephoto.model.TExceptionType;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Uri解析工具类
 * Author: JPH
 * Date: 2015/8/26 0026 16:23
 */
public class TUriParse {
    private static final String TAG = IntentUtils.class.getName();

    /**
     * 将scheme为file的uri转成FileProvider 提供的content uri
     * @param context
     * @param uri
     * @return
     */
    public static Uri convertFileUriToFileProviderUri(Context context,Uri uri){
        if(uri==null)return null;
        if(ContentResolver.SCHEME_FILE.equals(uri.getScheme())){
            return getUriForFile(context,new File(uri.getPath()));
        }
        return uri;

    }

    /**
     * 获取一个临时的Uri, 文件名随机生成
     * @param context
     * @return
     */
    public static Uri getTempUri(Context context){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File file=new File(Environment.getExternalStorageDirectory(), "/images/"+timeStamp + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        return getUriForFile(context,file);
    }

    /**
     * 获取一个临时的Uri, 通过传入字符串路径
     *
     * @param context
     * @param path
     * @return
     */
    public static Uri getTempUri(Context context, String path) {
        File file = new File(path);
        return getTempUri(context, file);
    }

    /**
     * 获取一个临时的Uri, 通过传入File对象
     * @param context
     * @return
     */
    public static Uri getTempUri(Context context, File file){
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        return getUriForFile(context,file);
    }

    /**
     * 创建一个用于拍照图片输出路径的Uri (FileProvider)
     * @param context
     * @return
     */
    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context,TConstant.getFileProviderName(context), file);
    }

    /**
     * 将TakePhoto 提供的Uri 解析出文件绝对路径
     * @param uri
     * @return
     */
    public static String parseOwnUri(Context context,Uri uri){
        if(uri==null)return null;
        String path;
        if(TextUtils.equals(uri.getAuthority(),TConstant.getFileProviderName(context))){
            path=new File(uri.getPath().replace("camera_photos/","")).getAbsolutePath();
        }else {
            path=uri.getPath();
        }
        return path;
    }
    /**
     * 通过URI获取文件的路径
     * @param uri
     * @param activity
     * @return
     * Author JPH
     * Date 2016/6/6 0006 20:01
     */
    public static String getFilePathWithUri(Uri uri, Activity activity)throws TException {
        if(uri==null){
            Log.w(TAG,"uri is null,activity may have been recovered?");
            throw new TException(TExceptionType.TYPE_URI_NULL);
        }
        File picture=getFileWithUri(uri,activity);
        String picturePath=picture==null? null:picture.getPath();
        if (TextUtils.isEmpty(picturePath))throw new TException(TExceptionType.TYPE_URI_PARSE_FAIL);
        if (!TImageFiles.checkMimeType(activity,TImageFiles.getMimeType(activity,uri)))throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        return picturePath;
    }
    /**
     * 通过URI获取文件
     * @param uri
     * @param activity
     * @return
     * Author JPH
     * Date 2016/10/25
     */
    public static File getFileWithUri(Uri uri, Activity activity) {
        String picturePath = null;
        String scheme=uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)){
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri,
                    filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if(columnIndex>=0){
                picturePath = cursor.getString(columnIndex);  //获取照片路径
            }else if(TextUtils.equals(uri.getAuthority(),TConstant.getFileProviderName(activity))){
                picturePath=parseOwnUri(activity,uri);
            }
            cursor.close();
        }else if (ContentResolver.SCHEME_FILE.equals(scheme)){
            picturePath=uri.getPath();
        }
        return TextUtils.isEmpty(picturePath)? null:new File(picturePath);
    }
    /**
     * 通过从文件中得到的URI获取文件的路径
     * @param uri
     * @param activity
     * @return
     * Author JPH
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
