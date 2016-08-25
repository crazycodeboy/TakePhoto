package com.jph.simple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 从相册选择照片进行裁剪，从相机拍取照片进行裁剪<br>
 * 从相册选择照片（不裁切），并获取照片的路径<br>
 * 拍取照片（不裁切），并获取照片路径
 * Author JPH
 * Date 2016/6/7 0007 16:01
 */
public class MainActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnTakePhotoActivity:
                startActivity(new Intent(this,SimpleActivity.class));
                break;
            case R.id.btnTakePhotoFragment:
                startActivity(new Intent(this,SimpleFragmentActivity.class));
                break;
            default:
        }
    }
}
