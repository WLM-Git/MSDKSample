package com.example.msdksample;

import android.os.Bundle;
import android.util.Log;
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

// 实现我们自定义的回调接口
public class MainActivity extends AppCompatActivity implements MopVM.MopConnectionListener {

    private static final String TAG = "MainActivity";
    private MopVM mopVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 MopVM 和监听器
        mopVM = new MopVM();
        mopVM.setConnectionListener(this); // 将 MainActivity 作为回调监听器
        mopVM.initListener(); // 初始化 MSDK 的底层监听器
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 演示逻辑：5秒后自动开始连接
        Completable.timer(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(ContextUtil.getContext(), "准备连接到 " + KeyManager.getInstance().getValue(KeyTools.createKey(ProductKey.KeyProductType)).name(), Toast.LENGTH_LONG).show();
                    // 只需调用连接方法，后续操作将在回调中进行
                    connectToPSDK();
                });
    }

    // ------------------- 实现回调接口的方法 -------------------

    @Override
    public void onConnected() {
        // 此方法在 MopVM 确认连接成功后被调用
        // 在主线程上更新 UI 或执行后续操作
        runOnUiThread(() -> {
            Log.d(TAG, "MOP Channel Connected!");
            Toast.makeText(this, "连接成功!", Toast.LENGTH_SHORT).show();

            // 现在可以安全地发送数据了
            sendDataToPSDK("Hello from MSDK, connection is established!");

            // 演示：10秒后自动断开连接
            Completable.timer(10, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                    .subscribe(this::disconnectFromPSDK);
        });
    }

    @Override
    public void onDisconnected() {
        // 此方法在连接断开后被调用
        runOnUiThread(() -> {
            Log.d(TAG, "MOP Channel Disconnected!");
            Toast.makeText(this, "连接已断开!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDataReceived(byte[] data, int length) {
        // 此方法在收到 PSDK 数据后被调用
        final String receivedMessage = new String(data, 0, length);
        Log.d(TAG, "Data received from PSDK: " + receivedMessage);

        runOnUiThread(() -> {
            Toast.makeText(this, "收到数据: " + receivedMessage, Toast.LENGTH_SHORT).show();
        });
    }

    // ------------------- 业务逻辑方法（无需修改） -------------------

    /**
     * 连接到 PSDK 的通信管道
     */
    private void connectToPSDK() {
        int channelId = 1; // 管道 ID 示例
        PipelineDeviceType deviceType = PipelineDeviceType.PAYLOAD; // PAYLOAD 设备类型
        TransmissionControlType transferType = TransmissionControlType.STABLE; // 可靠传输
        boolean isUseForDown = false; // 是否用于下行数据（即 MSDK 接收数据）

        mopVM.connect(channelId, deviceType, transferType, isUseForDown);
    }

    /**
     * 向 PSDK 发送数据
     * @param message 要发送的字符串消息
     */
    private void sendDataToPSDK(String message) {
        byte[] data = message.getBytes();
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
