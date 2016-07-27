package com.jph.takephoto.model;

import android.content.Intent;

/**
 * Author: JPH
 * Date: 2016/7/26 14:23
 */
public class TIntentWap {
    private Intent intent;
    private int requestCode;

    public TIntentWap() {
    }

    public TIntentWap(Intent intent, int requestCode) {
        this.intent = intent;
        this.requestCode = requestCode;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
