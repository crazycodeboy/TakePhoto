package org.devio.takephoto.compress;

import org.devio.takephoto.model.TImage;

import java.util.ArrayList;

/**
 * 压缩照片2.0
 * <p>
 * Author JPH
 * Date 2015-08-26 下午1:44:26
 */
public interface CompressImage {
    void compress();

    /**
     * 压缩结果监听器
     */
    interface CompressListener {
        /**
         * 压缩成功
         *
         * @param images 已经压缩图片
         */
        void onCompressSuccess(ArrayList<TImage> images);

        /**
         * 压缩失败
         *
         * @param images 压缩失败的图片
         * @param msg    失败的原因
         */
        void onCompressFailed(ArrayList<TImage> images, String msg);
    }
}
