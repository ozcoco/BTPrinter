package org.oz.dailog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.clj.fastble.BleManager;

import org.oz.R;
import org.oz.entity.BtDev;
import org.oz.widgets.DecorativeAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BTDialog extends AppCompatDialog {


    public interface OnControlListener {

        void connect(BluetoothDevice dev);

        void disconnect(BluetoothDevice dev);
    }

    private RecyclerView rcy_pair;

    private RecyclerView rcy_discovery;

    private AppCompatButton btn_scan;

    private ContentLoadingProgressBar progressBar;

    private DecorativeAdapter<BtHolder, BluetoothDevice> pairAdapter;

    private DecorativeAdapter<BtHolder, BluetoothDevice> discoveryAdapter;

    private OnControlListener mOnControlListener;

    private BluetoothAdapter mBluetoothAdapter;


    public OnControlListener getOnControlListener() {
        return mOnControlListener;
    }

    public void setOnControlListener(OnControlListener onControlListener) {
        this.mOnControlListener = onControlListener;
    }

    public BTDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dailog_bt);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBleDevice();//判断是否支持蓝牙，并打开蓝牙

        initData();

        initView();
    }


    /**
     * 蓝牙接收广播
     */
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {
        //接收
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            Object[] lstName = new Object[0];

            if (b != null)
                lstName = b.keySet().toArray();

            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Log.e("bluetooth", keyName + ">>>" + String.valueOf(b.get(keyName)));
            }
            BluetoothDevice device;
            // 搜索发现设备时，取得设备的信息；注意，这里有可能重复搜索同一设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
                discoveryAdapter.addData(device);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                progressBar.setVisibility(View.INVISIBLE);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                progressBar.setVisibility(View.VISIBLE);

            }
            //状态改变时
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对
                        Log.d("BlueToothTestActivity", "正在配对......");
//                        onRegisterBltReceiver.onBltIng(device);
                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束
                        Log.d("BlueToothTestActivity", "完成配对");
//                        onRegisterBltReceiver.onBltEnd(device);
                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对
                        Log.d("BlueToothTestActivity", "取消配对");
//                        onRegisterBltReceiver.onBltNone(device);
                    default:
                        break;
                }
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        initComponent();

    }


    @Override
    protected void onStop() {
        super.onStop();

        getContext().unregisterReceiver(searchDevices);

        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }

        mBluetoothAdapter = null;

    }


    private void initComponent() {

        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//扫描完成
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//扫描开始
        getContext().registerReceiver(searchDevices, intent);


    }

    private void initData() {


    }


    /**
     * 判断是否支持蓝牙，并打开蓝牙
     * 获取到BluetoothAdapter之后，还需要判断是否支持蓝牙，以及蓝牙是否打开。
     * 如果没打开，需要让用户打开蓝牙：
     */
    public void checkBleDevice() {

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        } else {
            Log.i("blueTooth", "该手机不支持蓝牙");
        }
    }

    private void btScan() {

        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            } else {
                mBluetoothAdapter.startDiscovery();
            }

        }
    }


    private void initView() {

        progressBar = findViewById(R.id.progressBar);

        btn_scan = findViewById(R.id.btn_scan);

        rcy_pair = findViewById(R.id.rcy_pair);

        rcy_discovery = findViewById(R.id.rcy_discovery);

        rcy_pair.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        rcy_discovery.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        pairAdapter = new DecorativeAdapter<>(getContext(), new DecorativeAdapter.IAdapterDecorator<BtHolder, BluetoothDevice>() {

            @Override
            public BtHolder onCreateViewHolder(@NonNull Context context, @NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {

                return new BtHolder(inflater.inflate(R.layout.item_bt, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull Context context, @NonNull BtHolder holder, @NonNull BluetoothDevice dev, int position) {

                holder.tx_name.setText(dev.getName());

                holder.tx_mac.setText(dev.getAddress());

                holder.btn_connect.setOnClickListener(v -> {

                    v.setEnabled(false);

                    holder.btn_disconnect.setEnabled(true);

                    if (mOnControlListener != null)
                        mOnControlListener.connect(dev);

                });

                holder.btn_disconnect.setOnClickListener(v -> {

                    v.setEnabled(false);

                    holder.btn_connect.setEnabled(true);

                    if (mOnControlListener != null)
                        mOnControlListener.disconnect(dev);

                });


            }
        });


        discoveryAdapter = new DecorativeAdapter<>(getContext(), new DecorativeAdapter.IAdapterDecorator<BtHolder, BluetoothDevice>() {

            @Override
            public BtHolder onCreateViewHolder(@NonNull Context context, @NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
                return new BtHolder(inflater.inflate(R.layout.item_bt, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull Context context, @NonNull BtHolder holder, @NonNull BluetoothDevice dev, int position) {

                holder.tx_name.setText(dev.getName());

                holder.tx_mac.setText(dev.getAddress());

                holder.btn_connect.setOnClickListener(v -> {

                    v.setEnabled(false);

                    holder.btn_disconnect.setEnabled(true);

                    dev.createBond();

//                    if (mOnControlListener != null)
//                        mOnControlListener.connect(dev);

                });

                holder.btn_disconnect.setOnClickListener(v -> {

                    v.setEnabled(false);

                    holder.btn_connect.setEnabled(true);

                    if (mOnControlListener != null)
                        mOnControlListener.disconnect(dev);

                });

            }

        });

        rcy_pair.setAdapter(pairAdapter);

        rcy_discovery.setAdapter(discoveryAdapter);

        btn_scan.setOnClickListener(v -> btScan());

        List<BluetoothDevice> btDevList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());

        pairAdapter.setData(btDevList);

    }


    public class BtHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tx_name;
        AppCompatTextView tx_mac;

        AppCompatButton btn_connect;

        AppCompatButton btn_disconnect;

        BtHolder(@NonNull View itemView) {
            super(itemView);

            tx_name = itemView.findViewById(R.id.tx_name);
            tx_mac = itemView.findViewById(R.id.tx_mac);
            btn_connect = itemView.findViewById(R.id.btn_connect);
            btn_disconnect = itemView.findViewById(R.id.btn_disconnect);
        }

    }


}
