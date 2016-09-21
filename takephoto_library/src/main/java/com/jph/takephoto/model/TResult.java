package com.jph.takephoto.model;

import java.util.ArrayList;

/**
 * TakePhoto 操作成功返回的处理结果
 * Author: JPH
 * Date: 2016/8/11 17:01
 */
public class TResult {
    private  ArrayList<TImage> images;
    private TImage image;
    public static TResult of(TImage image){
        ArrayList<TImage> images=new ArrayList<>(1);
        images.add(image);
        return new TResult(images);
    }
    public static TResult of(ArrayList<TImage> images){
        return new TResult(images);
    }
    private TResult(ArrayList<TImage> images) {
        this.images = images;
        if(images!=null&&!images.isEmpty())this.image=images.get(0);
    }

    public ArrayList<TImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<TImage> images) {
        this.images = images;
    }

    public TImage getImage() {
        return image;
    }

    public void setImage(TImage image) {
        this.image = image;
    }
}
