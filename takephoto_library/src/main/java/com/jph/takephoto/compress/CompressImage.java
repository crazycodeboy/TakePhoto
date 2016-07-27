package com.jph.takephoto.compress;

/**
 * 压缩照片2.0
 *
 * Author JPH
 * Date 2015-08-26 下午1:44:26
 */
public interface CompressImage {
    void compress(String imagePath, CompressListener listener);

    /**
     * 压缩结果监听器
     */
    interface CompressListener {
        /**
         * 压缩成功
         *
         * @param imgPath 压缩图片的路径
         */
        void onCompressSuccess(String imgPath);

        /**
         * 压缩失败
         * @param imgPath 压缩失败的图片
         * @param msg 失败的原因
         */
        void onCompressFailed(String imgPath,String msg);
    }
}
