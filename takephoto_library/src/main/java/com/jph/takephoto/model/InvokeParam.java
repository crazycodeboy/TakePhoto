package com.jph.takephoto.model;

import java.lang.reflect.Method;

/**
 * Created by penn on 16/9/22.
 */
public class InvokeParam {
    private Object proxy;
    private Method method;
    private Object[] args;

    public InvokeParam(Object proxy, Method method, Object[] args) {
        this.proxy = proxy;
        this.method = method;
        this.args = args;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
