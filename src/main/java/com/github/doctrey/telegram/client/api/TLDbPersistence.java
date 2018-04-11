package com.github.doctrey.telegram.client.api;

import org.telegram.tl.TLObject;

/**
 * Created by s_tayari on 4/11/2018.
 */
public abstract class TLDbPersistence<T extends TLObject> {

    private static final String TAG = "KernelPersistence";

    private Class<T> destClass;
    private T obj;

    public TLDbPersistence(Class<T> destClass) throws Exception {
        this.destClass = destClass;
        this.obj = loadData();
    }

    protected abstract T loadData() throws Exception;

    protected abstract void updateData() throws Exception;

    protected void afterLoaded() {

    }

    public T getObj() {
        return obj;
    }
}
