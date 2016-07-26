package com.jph.takephoto.uitl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.jph.takephoto.entity.TIntentWap;

import java.util.List;

/**
 * 工具类
 * Author: JPH
 * Date: 2016/7/26 10:04
 */
public class TUtils {
    private static final String TAG = IntentUtils.class.getName();

    /**
     * 安全地发送Intent
     * @param activity
     * @param intentWapList 要发送的Intent以及候选Intent
     * @param defaultIndex 默认发送的Intent
     * @param isCrop 是否为裁切照片的Intent
     * @throws TException
     */
    public static void sendIntentWithSafely(Activity activity, List<TIntentWap> intentWapList,int defaultIndex,boolean isCrop)throws TException{
        if (defaultIndex+1>intentWapList.size())throw new TException(isCrop?TExceptionType.TYPE_NO_MATCH_PICK_INTENT:TExceptionType.TYPE_NO_MATCH_CROP_INTENT);
        TIntentWap intentWap=intentWapList.get(defaultIndex);
        List result=activity.getPackageManager().queryIntentActivities(intentWap.getIntent(),PackageManager.MATCH_ALL);
        if (result.isEmpty()){
            sendIntentWithSafely(activity,intentWapList,++defaultIndex,isCrop);
        }else {
            activity.startActivityForResult(intentWap.getIntent(),intentWap.getRequestCode());
        }
    }
    /**
     * 是否裁剪之后返回数据
     **/
    public static boolean isReturnData() {
        String release= Build.VERSION.RELEASE;
        int sdk= Build.VERSION.SDK_INT;
        Log.i(TAG,"release:"+release+"sdk:"+sdk);
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
