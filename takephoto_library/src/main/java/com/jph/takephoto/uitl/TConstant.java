package com.jph.takephoto.uitl;

/**
 * 常量类
 * @author JPH
 * Date 2016/6/7 0007 9:39
 */
public class TConstant {
    /**
     * 裁剪默认宽度
     */
    public final static int outputX=480;
    /**
     * 裁剪默认高度
     */
    public final static int outputY=480;
    /**
     * request Code 从相册选择照片并裁切
     **/
    public final static int PIC_SELECT_CROP = 123;
    /**
     * request Code 从相册选择照片不裁切
     **/
    public final static int PICK_PICTURE_FROM_GALLERY_ORIGINAL = 126;
    /**
     * request Code 拍取照片并裁切
     **/
    public final static int PIC_TAKE_CROP = 124;
    /**
     * request Code 拍取照片不裁切
     **/
    public final static int PIC_TAKE_ORIGINAL = 127;
    /**
     * request Code 裁切照片
     **/
    public final static int PIC_CROP = 125;
    /**
     * request Code 从文件中选择照片
     **/
    public final static int PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL = 128;
 }