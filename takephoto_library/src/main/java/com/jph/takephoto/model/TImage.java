package com.jph.takephoto.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * TakePhoto 操作成功返回的处理结果
 *
 * Author: JPH
 * Date: 2016/8/11 17:01
 */
public class TImage implements Serializable{
    private String originalPath;
    private String compressPath;
    private FromType fromType;
    private boolean cropped;
    private boolean compressed;
    public static TImage of(String path, FromType fromType){
        return new TImage(path, fromType);
    }
    public static TImage of(Uri uri, FromType fromType){
        return new TImage(uri, fromType);
    }
    private TImage(String path, FromType fromType) {
        this.originalPath = path;
        this.fromType = fromType;
    }
    private TImage(Uri uri, FromType fromType) {
        this.originalPath = uri.getPath();
        this.fromType = fromType;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public FromType getFromType() {
        return fromType;
    }

    public void setFromType(FromType fromType) {
        this.fromType = fromType;
    }

    public boolean isCropped() {
        return cropped;
    }

    public void setCropped(boolean cropped) {
        this.cropped = cropped;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public enum FromType {
        CAMERA, OTHER
    }
}
