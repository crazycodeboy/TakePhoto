package com.jph.simple;

import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;


/**
 * - 支持通过相机拍照获取图片
 * - 支持从相册选择图片
 * - 支持从文件选择图片
 * - 支持多图选择
 * - 支持批量图片裁切
 * - 支持批量图片压缩
 * - 支持对图片进行压缩
 * - 支持对图片进行裁剪
 * - 支持对裁剪及压缩参数自定义
 * - 提供自带裁剪工具(可选)
 * - 支持智能选取及裁剪异常处理
 * - 支持因拍照Activity被回收后的自动恢复
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:3.0.0
 * 技术博文：http://www.cboy.me
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class CustomHelper implements View.OnClickListener{
    private View rootView;
    private Button btnPickBySelect;
    private Button btnPickByTake;
    private RadioGroup rgCrop;
    public static CustomHelper of(View rootView){
        return new CustomHelper(rootView);
    }
    private CustomHelper(View rootView) {
        this.rootView = rootView;
        init();
    }
    private void init(){
        btnPickBySelect= (Button) rootView.findViewById(R.id.btnPickBySelect);
        btnPickByTake= (Button) rootView.findViewById(R.id.btnPickByTake);
        rgCrop= (RadioGroup) rootView.findViewById(R.id.rgCrop);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPickBySelect:
                break;
            case R.id.btnPickByTake:
                break;
            default:
                break;
        }
    }
}
