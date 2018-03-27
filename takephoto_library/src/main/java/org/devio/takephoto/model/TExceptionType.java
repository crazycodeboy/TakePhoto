package org.devio.takephoto.model;

/**
 * Author: JPH
 * Date: 2016/7/26 11:01
 */
public enum TExceptionType {
    TYPE_NOT_IMAGE("选择的文件不是图片"), TYPE_WRITE_FAIL("保存选择的的文件失败"), TYPE_URI_NULL("所选照片的Uri 为null"), TYPE_URI_PARSE_FAIL("从Uri中获取文件路径失败"),
    TYPE_NO_MATCH_PICK_INTENT("没有匹配到选择图片的Intent"), TYPE_NO_MATCH_CROP_INTENT("没有匹配到裁切图片的Intent"), TYPE_NO_CAMERA("没有相机"),
    TYPE_NO_FIND("选择的文件没有找到");

    String stringValue;

    TExceptionType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
