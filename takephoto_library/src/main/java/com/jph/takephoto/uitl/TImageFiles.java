package com.jph.takephoto.uitl;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.jph.takephoto.R;
import com.jph.takephoto.model.TException;
import com.jph.takephoto.model.TExceptionType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * ImageFiles工具类
 * @author JPH
 * Date 2016/6/7 0007 9:39
 */
public class TImageFiles {
    private static final String TAG = IntentUtils.class.getName();
    /**
     * 将bitmap写入到文件
     *
     * @param bitmap
     */
    public static void writeToFile(Bitmap bitmap, Uri imageUri) {
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
     * InputStream 转File
     * */
    public static void inputStreamToFile(InputStream is, File file) throws TException {
        if (file==null){
            Log.i(TAG,"inputStreamToFile:file not be null");
            throw new TException(TExceptionType.TYPE_WRITE_FAIL);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 10];
            int i;
            while ((i = is.read(buffer)) != -1) {
                fos.write(buffer, 0, i);
            }
        } catch (IOException e) {
            Log.e(TAG,"InputStream 写入文件出错:"+e.toString());
            throw new TException(TExceptionType.TYPE_WRITE_FAIL);
        } finally {
            try {
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取临时文件
     * @param context
     * @param photoUri
     * @return
     */
    public static File getTempFile(Activity context, Uri photoUri)throws TException {
        String minType=getMimeType(context, photoUri);
        if (!checkMimeType(context,minType))throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        File filesDir=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!filesDir.exists())filesDir.mkdirs();
        File photoFile = new File(filesDir, UUID.randomUUID().toString() + "." +minType );
        return photoFile;
    }

    /**
     * 检查文件类型是否是图片
     * @param minType
     * @return
     */
    public static boolean checkMimeType(Context context,String minType) {
        boolean isPicture=TextUtils.isEmpty(minType)?false:".jpg|.gif|.png|.bmp|.jpeg|.webp|".contains(minType.toLowerCase())?true:false;
        if (!isPicture)Toast.makeText(context,context.getResources().getText(R.string.tip_type_not_image),Toast.LENGTH_SHORT).show();
        return isPicture;
    }

    /**
     * To find out the extension of required object in given uri
     * Solution by http://stackoverflow.com/a/36514823/1171484
     */
    public static String getMimeType(Activity context, Uri uri) {
        String extension;
        //Check uri format to avoid null
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            //If scheme is a content
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
            if (TextUtils.isEmpty(extension))extension=MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            if (TextUtils.isEmpty(extension))extension=MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
        }
        if(TextUtils.isEmpty(extension)){
            extension=getMimeTypeByFileName(TUriParse.getFileWithUri(uri,context).getName());
        }
        return extension;
    }
    public static String getMimeTypeByFileName(String fileName){
        return fileName.substring(fileName.lastIndexOf("."),fileName.length());
    }
 }