package org.devio.takephoto.model;

import java.io.Serializable;

/**
 * 裁剪配置类
 * Author: JPH
 * Date: 2016/7/27 13:19
 */
public class CropOptions implements Serializable {
    /**
     * 使用TakePhoto自带的裁切工具进行裁切
     */
    private boolean withOwnCrop;
    private int aspectX;
    private int aspectY;
    private int outputX;
    private int outputY;

    private CropOptions() {
    }

    public int getAspectX() {
        return aspectX;
    }

    public void setAspectX(int aspectX) {
        this.aspectX = aspectX;
    }

    public int getAspectY() {
        return aspectY;
    }

    public void setAspectY(int aspectY) {
        this.aspectY = aspectY;
    }

    public int getOutputX() {
        return outputX;
    }

    public void setOutputX(int outputX) {
        this.outputX = outputX;
    }

    public int getOutputY() {
        return outputY;
    }

    public void setOutputY(int outputY) {
        this.outputY = outputY;
    }

    public boolean isWithOwnCrop() {
        return withOwnCrop;
    }

    public void setWithOwnCrop(boolean withOwnCrop) {
        this.withOwnCrop = withOwnCrop;
    }

    public static class Builder {
        private CropOptions options;

        public Builder() {
            options = new CropOptions();
        }

        public Builder setAspectX(int aspectX) {
            options.setAspectX(aspectX);
            return this;
        }

        public Builder setAspectY(int aspectY) {
            options.setAspectY(aspectY);
            return this;
        }

        public Builder setOutputX(int outputX) {
            options.setOutputX(outputX);
            return this;
        }

        public Builder setOutputY(int outputY) {
            options.setOutputY(outputY);
            return this;
        }

        public Builder setWithOwnCrop(boolean withOwnCrop) {
            options.setWithOwnCrop(withOwnCrop);
            return this;
        }

        public CropOptions create() {
            return options;
        }
    }
}
