package com.jph.takephoto.compress;

import android.content.Context;
import android.text.TextUtils;
import com.jph.takephoto.model.TImage;
import java.io.File;
import java.util.ArrayList;

/**
 * 压缩照片
 *
 * Date: 2016/9/21 0007 20:10
 * Version:3.0.0
 * 技术博文：http://www.cboy.me
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class CompressImageImpl implements CompressImage {
    private CompressImageUtil compressImageUtil;
    private ArrayList<TImage> images;
    private CompressImage.CompressListener listener;

    public static CompressImage of(Context context,CompressConfig config, ArrayList<TImage> images, CompressImage.CompressListener listener) {
        if(config.getLubanOptions()!=null){
            return new CompressWithLuBan(context,config,images,listener);
        }else {
            return new CompressImageImpl(context,config,images,listener);
        }
    }
    private CompressImageImpl(Context context,CompressConfig config, ArrayList<TImage> images, CompressImage.CompressListener listener){
        compressImageUtil = new CompressImageUtil(context,config);
        this.images = images;
        this.listener = listener;
    }

    @Override
    public void compress() {
        if(images==null||images.isEmpty())listener.onCompressFailed(images," images is null");
        for(TImage image:images){
            if(image==null){
                listener.onCompressFailed(images," There are pictures of compress  is null.");
                return;
            }
        }
        compress(images.get(0));
    }

    private void compress(final TImage image) {
        if (TextUtils.isEmpty(image.getPath())){
            continueCompress(image,false);
            return;
        }

        File file = new File(image.getPath());
        if (file == null || !file.exists() || !file.isFile()){
            continueCompress(image,false);
            return;
        }

        compressImageUtil.compress(image.getPath(), new CompressImageUtil.CompressListener() {
            @Override
            public void onCompressSuccess(String imgPath) {
                image.setPath(imgPath);
                continueCompress(image,true);
            }

            @Override
            public void onCompressFailed(String imgPath, String msg) {
                continueCompress(image,false,msg);
            }
        });
    }

    private void continueCompress(TImage image,boolean preSuccess,String...message){
        image.setCompressed(preSuccess);
        int index=images.indexOf(image);
        boolean isLast=index== images.size() - 1;
        if (isLast) {
            handleCompressCallBack(message);
        }else {
            compress(images.get(index+1));
        }
    }
    private void handleCompressCallBack(String...message){
        if(message.length>0){
            listener.onCompressFailed(images,message[0]);
            return;
        }

        for(TImage image:images){
            if(!image.isCompressed()){
                listener.onCompressFailed(images,image.getPath()+" is compress failures");
                return;
            }
        }
        listener.onCompressSuccess(images);
    }
}
