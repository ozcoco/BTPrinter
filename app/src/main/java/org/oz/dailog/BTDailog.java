package org.oz.dailog;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import org.oz.R;
import org.oz.entity.BtDev;
import org.oz.widgets.DecorativeAdapter;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BTDailog extends AppCompatDialog {


    public interface OnControlListener {

        void connect(BtDev dev);

        void disconnect(BtDev dev);
    }

    private RecyclerView rcy_pair;

    private RecyclerView rcy_discovery;

    private AppCompatButton btn_scan;

    private DecorativeAdapter<BtHolder, BtDev> pairAdapter;

    private DecorativeAdapter<BtHolder, BtDev> discoveryAdapter;

    private OnControlListener mOnControlListener;


    public OnControlListener getOnControlListener() {
        return mOnControlListener;
    }

    public void setOnControlListener(OnControlListener onControlListener) {
        this.mOnControlListener = onControlListener;
    }

    public BTDailog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dailog_bt);

        btn_scan = findViewById(R.id.btn_scan);

        rcy_pair = findViewById(R.id.rcy_pair);

        rcy_discovery = findViewById(R.id.rcy_discovery);

        rcy_pair.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        rcy_discovery.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        pairAdapter = new DecorativeAdapter<>(getContext(), new DecorativeAdapter.IAdapterDecorator<BtHolder, BtDev>() {

            @Override
            public BtHolder onCreateViewHolder(@NonNull Context context, @NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {

                return new BtHolder(inflater.inflate(R.layout.item_bt, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull Context context, @NonNull BtHolder holder, @NonNull BtDev data, int position) {

                holder.tx_name.setText(data.getName());

                holder.tx_mac.setText(data.getMac());

                holder.btn_connect.setOnClickListener(v -> {

                    v.setEnabled(false);

                    holder.btn_disconnect.setEnabled(true);

                    if (mOnControlListener != null)
                        mOnControlListener.connect(data);

                });

                holder.btn_disconnect.setOnClickListener(v -> {

                    v.setEnabled(false);

                    holder.btn_connect.setEnabled(true);

                    if (mOnControlListener != null)
                        mOnControlListener.disconnect(data);

                });


            }
        });


        discoveryAdapter = new DecorativeAdapter<>(getContext(), new DecorativeAdapter.IAdapterDecorator<BtHolder, BtDev>() {

            @Override
            public BtHolder onCreateViewHolder(@NonNull Context context, @NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
                return new BtHolder(inflater.inflate(R.layout.item_bt, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull Context context, @NonNull BtHolder holder, @NonNull BtDev data, int position) {

                holder.tx_name.setText(data.getName());

                holder.tx_mac.setText(data.getMac());

                holder.btn_connect.setOnClickListener(v -> {

                    v.setEnabled(false);

                    holder.btn_disconnect.setEnabled(true);

                    BluetoothDevice remoteDevice = BleManager.getInstance().getBluetoothAdapter().getRemoteDevice(data.getMac());

//                    remoteDevice.setPairingConfirmation(false);
//
//                    remoteDevice.setPin("0000".getBytes());

                    remoteDevice.createBond();

                    if (mOnControlListener != null)
                        mOnControlListener.connect(data);

                });

                holder.btn_disconnect.setOnClickListener(v -> {

                    v.setEnabled(false);

                    holder.btn_connect.setEnabled(true);

                    if (mOnControlListener != null)
                        mOnControlListener.disconnect(data);

                });

            }

        });


        rcy_pair.setAdapter(pairAdapter);

        rcy_discovery.setAdapter(discoveryAdapter);

        btn_scan.setOnClickListener(v -> scan());

        List<BtDev> btDevList = new ArrayList<>();

        for (BluetoothDevice device : BleManager.getInstance().getBluetoothAdapter().getBondedDevices()) {
            BtDev dev = new BtDev(device.getName(), "", device.getAddress(), "" + device.getType());

            btDevList.add(dev);
        }

        pairAdapter.setData(btDevList);
    }


    private void pairDevice(BluetoothDevice device) {
        try {
            Log.d("pairDevice()", "Start Pairing...");
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("pairDevice()", "Pairing finished.");
        } catch (Exception e) {
            Log.e("pairDevice()", e.getMessage());
        }
    }


    /**
     * bt scan
     */
    private void scan() {

        Log.i("^_*>>>", "开始扫描……");

        BleManager.getInstance().scanAndConnect(new BleScanAndConnectCallback() {
            @Override
            public void onScanFinished(BleDevice scanResult) {

            }

            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {

            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

            }

            @Override
            public void onScanStarted(boolean success) {

            }

            @Override
            public void onScanning(BleDevice device) {

                if (device.getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {

//                    device.getDevice().getBluetoothClass()

                    BtDev dev = new BtDev(device.getName(), "" + device.getRssi(), device.getMac(), "" + device.getDevice().getType());

                    discoveryAdapter.addData(dev);
                }
            }
        });

    }


    public static class PairingRequest extends BroadcastReceiver {
        public PairingRequest() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int pin = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0);
                    //the pin in case you need to accept for an specific pin
                    Log.d("PIN", " " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0));
                    //maybe you look for a name or address
                    Log.d("Bonded", device.getName());
                    byte[] pinBytes;
                    pinBytes = ("" + pin).getBytes(StandardCharsets.UTF_8);
                    device.setPin(pinBytes);
                    //setPairing confirmation if neeeded
                    device.setPairingConfirmation(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
