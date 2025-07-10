package com.example.msdksample;


import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import dji.sdk.keyvalue.key.KeyTools;
import dji.sdk.keyvalue.key.ProductKey;
import dji.sdk.keyvalue.value.mop.PipelineDeviceType;
import dji.sdk.keyvalue.value.mop.TransmissionControlType;
import dji.v5.manager.KeyManager;
import dji.v5.utils.common.ContextUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;

public class MainActivity extends AppCompatActivity {

    private MopVM mopVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Completable.timer(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(ContextUtil.getContext(), KeyManager.getInstance().getValue(KeyTools.createKey(ProductKey.KeyProductType)).name(), Toast.LENGTH_LONG).show();

                    //初始化MopVM
                    mopVM = new MopVM();
                    //建立连接
                    connectToPSDK();
                    //发送数据到psdk
                    sendDataToPSDK("Hello from MSDK");
                    //断开连接
                    disconnectFromPSDK();
                });
    }

    /**
     * 连接到 PSDK 的通信管道
     */
    private void connectToPSDK() {
        int channelId = 1; // 管道 ID 示例
        PipelineDeviceType deviceType = PipelineDeviceType.PAYLOAD; // PAYLOAD 设备类型
        TransmissionControlType transferType = TransmissionControlType.STABLE; // 可靠传输
        boolean isUseForDown = false; // 是否用于下载

        mopVM.connect(channelId, deviceType, transferType, isUseForDown);
    }

    /**
     * 向 PSDK 发送数据
     *
     * @param message 要发送的字符串消息
     */
    private void sendDataToPSDK(String message) {
        byte[] data = message.getBytes(); // 将字符串转为字节数组
        mopVM.sendData(data);
    }

    /**
     * 断开与 PSDK 的通信管道
     */
    private void disconnectFromPSDK() {
        mopVM.stopMop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mopVM.stopMop(); // 确保在退出时断开管道
    }
}