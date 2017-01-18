package com.jph.takephoto.model;

import java.io.Serializable;

/**
 * Luban配置类
 * Author: crazycodeboy
 * Date: 2016/11/5 0007 20:10
 * Version:4.0.1
 * 技术博文：http://www.devio.org/
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class LubanOptions implements Serializable {
  /**
   * 压缩到的最大大小，单位B
   */
  private int maxSize;
  private int maxHeight;
  private int maxWidth;

  private LubanOptions() {
  }

  public int getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  public int getMaxHeight() {
    return maxHeight;
  }

  public void setMaxHeight(int maxHeight) {
    this.maxHeight = maxHeight;
  }

  public int getMaxWidth() {
    return maxWidth;
  }

  public void setMaxWidth(int maxWidth) {
    this.maxWidth = maxWidth;
  }

  public static class Builder {
    private LubanOptions options;

    public Builder() {
      options = new LubanOptions();
    }

    public Builder setMaxSize(int maxSize) {
      options.setMaxSize(maxSize);
      return this;
    }

    public Builder setMaxHeight(int maxHeight) {
      options.setMaxHeight(maxHeight);
      return this;
    }

    public Builder setMaxWidth(int maxWidth) {
      options.setMaxWidth(maxWidth);
      return this;
    }

    public LubanOptions create() {
      return options;
    }
  }
}
