package com.jph.takephoto.compress;


import java.io.Serializable;

/**
 * 压缩配置类
 * Author: JPH
 * Date: 2016/6/7 0007 18:01
 */
public class CompressConfig implements Serializable {

    /**
     * 长或宽不超过的最大像素,单位px
     */
    private int maxPixel=1200;
    /**
     * 压缩到的最大大小，单位B
     */
    private int maxSize=100*1024;
    /**
     * 是否显示压缩对话框
     */
    private boolean isShowCompressDialog;
    public int getMaxPixel() {
        return maxPixel;
    }
    public static CompressConfig getDefaultConfig(){
        return new CompressConfig();
    }
    public CompressConfig setMaxPixel(int maxPixel) {
        this.maxPixel = maxPixel;
        return this;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public CompressConfig setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public boolean isShowCompressDialog() {
        return isShowCompressDialog;
    }

    public CompressConfig setShowCompressDialog(boolean showCompressDialog) {
        isShowCompressDialog = showCompressDialog;
        return this;
    }
}

