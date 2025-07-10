package com.example.msdksample;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import dji.v5.common.error.IDJIError;
import dji.v5.common.register.DJISDKInitEvent;
import dji.v5.manager.SDKManager;
import dji.v5.manager.interfaces.SDKManagerCallback;


public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 在调用 install 前，请勿调用任何 MSDK 相关接口
        // MSDK v5.10.0 之前的版本请使用 com.secneo.sdk.Helper.install(this)
        com.cySdkyc.clx.Helper.install(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication", "onCreate started");
        // 初始化 MSDK，建议初始化逻辑放在 Application 中，当然也可以根据自己的需要放在任意地方。
        SDKManager.getInstance().init(this, new SDKManagerCallback() {
            @Override
            public void onInitProcess(DJISDKInitEvent event, int totalProcess) {
                Log.i(TAG, "onInitProcess: ");
                if (event == DJISDKInitEvent.INITIALIZE_COMPLETE) {
                    SDKManager.getInstance().registerApp();
                }
            }

            @Override
            public void onRegisterSuccess() {
                Log.i(TAG, "onRegisterSuccess: ");
            }

            @Override
            public void onRegisterFailure(IDJIError error) {
                Log.i(TAG, "onRegisterFailure: ");
            }

            @Override
            public void onProductConnect(int productId) {
                Log.i(TAG, "onProductConnect: ");
            }

            @Override
            public void onProductDisconnect(int productId) {
                Log.i(TAG, "onProductDisconnect: ");
            }

            @Override
            public void onProductChanged(int productId) {
                Log.i(TAG, "onProductChanged: ");
            }

            @Override
            public void onDatabaseDownloadProgress(long current, long total) {
                Log.i(TAG, "onDatabaseDownloadProgress: " + (current / total));
            }
        });
    }
}
