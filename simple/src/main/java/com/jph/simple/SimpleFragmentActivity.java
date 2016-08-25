package com.jph.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * Author: JPH
 * Date: 2016/8/11 15:47
 */
public class SimpleFragmentActivity extends FragmentActivity{
    SimpleFragment fragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_fragment_layout);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment1,fragment=new SimpleFragment(),"dd");
        transaction.commit();
    }
    public void cropPic(View v){
        fragment.cropPic(v);
    }
}
