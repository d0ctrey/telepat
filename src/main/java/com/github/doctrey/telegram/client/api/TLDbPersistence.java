package com.github.doctrey.telegram.client.api;

import org.telegram.tl.TLObject;

/**
 * Created by s_tayari on 4/11/2018.
 */
public abstract class TLDbPersistence<T extends TLObject> {

    private static final String TAG = "KernelPersistence";

    private Class<T> destClass;
    protected String phoneNumber;
    private T obj;

    public TLDbPersistence(String phoneNumber, Class<T> destClass) {
        this.phoneNumber = phoneNumber;
        this.destClass = destClass;
        this.obj = loadData();
        if (obj == null) {
            try {
                obj = destClass.newInstance();
            } catch (Exception e1) {
                throw new RuntimeException("Unable to instantiate default settings");
            }
        }
    }

    protected abstract T loadData();

    protected abstract void updateData();

    protected void afterLoaded() {

    }

    public T getObj() {
        return obj;
    }
}
