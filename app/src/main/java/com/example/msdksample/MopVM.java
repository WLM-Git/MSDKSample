package com.example.msdksample;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import dji.sdk.keyvalue.key.KeyTools;
import dji.sdk.keyvalue.key.ProductKey;
import dji.sdk.keyvalue.value.mop.PipelineDeviceType;
import dji.sdk.keyvalue.value.mop.TransmissionControlType;
import dji.v5.common.error.DJIPipeLineError;
import dji.v5.common.error.IDJIError;
import dji.v5.manager.KeyManager;
import dji.v5.manager.mop.DataResult;
import dji.v5.manager.mop.Pipeline;
import dji.v5.manager.mop.PipelineManager;
import dji.v5.utils.common.ContextUtil;
import dji.v5.utils.common.DJIExecutor;
import io.reactivex.rxjava3.disposables.Disposable;

public class MopVM {
    private boolean isStop = false;
    private final MutableLiveData<Map<Integer, Pipeline>> pipelineMapLiveData = new MutableLiveData<>();
    private final byte[] data = new byte[19004];
    private final ExecutorService executorService = DJIExecutor.getExecutorFor(DJIExecutor.Purpose.URGENT);
    private Disposable mReadDataDisposable;
    private Pipeline pipeline;
    private Param currentConnectParam;

    // ------------------- 新增代码开始 -------------------

    /**
     * 定义连接回调接口
     */
    public interface MopConnectionListener {
        void onConnected();
        void onDisconnected();
        void onDataReceived(byte[] data, int length);
    }

    private MopConnectionListener connectionListener;

    /**
     * 设置回调监听器
     * @param listener
     */
    public void setConnectionListener(MopConnectionListener listener) {
        this.connectionListener = listener;
    }

    // ------------------- 新增代码结束 -------------------


    public void initListener() {
        // 监听 MSDK 的管道连接事件
        PipelineManager.getInstance().addPipelineConnectionListener((pipelineMap -> {
            // 当连接建立或断开时，此回调会被触发
            // 我们检查我们尝试连接的管道是否在列表中
            if (currentConnectParam != null && pipelineMap.containsKey(currentConnectParam.getId())) {
                // 确认管道已连接
                this.pipeline = pipelineMap.get(currentConnectParam.getId());
                isStop = false;

                // 通过回调通知外部（MainActivity）连接已成功
                if (connectionListener != null) {
                    connectionListener.onConnected();
                }

                // 在独立的线程中开始循环读取数据
                if (currentConnectParam != null && !currentConnectParam.isUseForDown()) {
                    executorService.execute(this::readDataLoop);
                }

            } else {
                // 管道已断开
                if (connectionListener != null) {
                    connectionListener.onDisconnected();
                }
            }
            pipelineMapLiveData.postValue(pipelineMap);
        }));
    }

    //建立管道连接
    public void connect(int id, PipelineDeviceType deviceType, TransmissionControlType transmissionControlType, boolean isUseForDown) {
        // 保存连接参数，用于后续在 Listener 中进行比对
        currentConnectParam = new Param(id, transmissionControlType, deviceType, isUseForDown);

        // 异步发起连接请求
        executorService.execute(() -> {
            PipelineManager.getInstance().connectPipeline(id, deviceType, transmissionControlType);
            // 注意：这里不再做任何操作，等待上面的 Listener 回调
        });
    }

    /**
     * 修改 readData 为一个循环读取的方法
     * 这个方法将在连接成功后被调用
     */
    private void readDataLoop() {
        // 只要没有被停止，就持续读取
        while (!isStop) {
            if (pipeline == null) {
                // 如果管道为空，则停止循环
                stopMop();
                break;
            }

            DataResult result = pipeline.readData(data);
            int len = result.getLength();

            if (len > 0) {
                // 通过回调将收到的数据传出
                if (connectionListener != null) {
                    connectionListener.onDataReceived(data, len);
                }
            } else if (len < 0) {
                // 如果读到的数据长度小于0，说明出现错误
                if (!isStop && !result.getError().errorCode().equals(DJIPipeLineError.TIMEOUT)) {
                    // 对于非超时的错误，我们认为连接已断开
                    stopMop();
                }
                // 如果是TIMEOUT，则循环会继续，尝试下一次读取
            }
        }
    }

    public void sendData(byte[] byteArray) {
        executorService.submit(() -> {
            if (pipeline == null || isStop) {
                return;
            }
            pipeline.writeData(byteArray);
        });
    }

    private void disconnectMop() {
        executorService.execute(() -> {
            if (pipeline == null || currentConnectParam == null) {
                return;
            }
            isStop = true; // 设置停止标志，这将使 readDataLoop 退出

            PipelineManager.getInstance().disconnectPipeline(
                    currentConnectParam.getId(),
                    currentConnectParam.getDeviceType(),
                    currentConnectParam.getTransmissionControlType()
            );
            // 清理状态
            pipeline = null;
            currentConnectParam = null;
            if(connectionListener != null) {
                connectionListener.onDisconnected();
            }
        });
    }

    private void stopReadDataTimer() {
        if (mReadDataDisposable != null) {
            mReadDataDisposable.dispose();
            mReadDataDisposable = null;
        }
    }

    public void stopMop() {
        if (!isStop) {
            isStop = true; // 确保即使 disconnectMop 尚未执行，读取循环也会停止
            stopReadDataTimer();
            disconnectMop();
        }
    }

    // 在 Param 中增加 isUseForDown 字段
    public static class Param {
        private final int id;
        private final TransmissionControlType transmissionControlType;
        private final PipelineDeviceType deviceType;
        private final boolean isUseForDown;

        public Param(int id, TransmissionControlType transmissionControlType, PipelineDeviceType deviceType, boolean isUseForDown) {
            this.id = id;
            this.transmissionControlType = transmissionControlType;
            this.deviceType = deviceType;
            this.isUseForDown = isUseForDown;
        }

        public int getId() {
            return id;
        }
        public TransmissionControlType getTransmissionControlType() {
            return transmissionControlType;
        }
        public PipelineDeviceType getDeviceType() {
            return deviceType;
        }
        public boolean isUseForDown() { return isUseForDown; }
    }
}
