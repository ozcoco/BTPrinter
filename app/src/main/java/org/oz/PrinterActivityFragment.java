package org.oz;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yf.btp.PrinterService;

import org.oz.databinding.FragmentPrinterBinding;

import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class PrinterActivityFragment extends Fragment {

    private FragmentPrinterBinding mBinding;

    private final Handles mHandles = new Handles();

    private PrinterService mPrinterService;

    private Intent intentPrinterService;

    private BluetoothAdapter mBluetoothAdapter;

    public class Handles {

        public void onPrinter(View v) {

            Log.i("^_*>>>", mPrinterService.getPackageName());
        }

        public void onDiscovery(View v) {

            Log.i("^_*>>>", "开始扫描……");

            mBluetoothAdapter.startDiscovery();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_printer, container, false);

        mBinding.setLifecycleOwner(this);

        mBinding.setHandles(mHandles);

        return mBinding.getRoot();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        Objects.requireNonNull(getActivity()).registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        bindService();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBT();

        initData();

        initView();

    }


    private int REQUEST_ENABLE_BT = 0x01011;

    private StringBuilder btStrBuilder = new StringBuilder();

    private void initBT() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {

            if (!mBluetoothAdapter.isEnabled()) {

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }


            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    pairedDevices.forEach(this::contactBtStr);
                else
                    for (BluetoothDevice device : pairedDevices)
                        contactBtStr(device);
            }

        }

    }


    private void contactBtStr(BluetoothDevice device) {

        btStrBuilder.append("Device Name :").append(device.getName()).append('\n');

        btStrBuilder.append("Device Address :").append(device.getAddress()).append('\n').append('\n').append('\n');


    }


    private void initData() {


    }

    private void initView() {

        mBinding.setContent(btStrBuilder.toString());

    }

    private StringBuilder discoveryBtStrBuilder = new StringBuilder();
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            Log.i("^_*>>>", "接收扫描广播……");

            String action = intent.getAction();

            Log.i("^_*>>>", "扫描广播action：" + action);

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView

                discoveryBtStrBuilder.append("Device Name :").append(device.getName()).append('\n');

                discoveryBtStrBuilder.append("Device Detail :").append(device.toString()).append('\n');

                discoveryBtStrBuilder.append("Device Address :").append(device.getAddress()).append('\n').append('\n').append('\n');


                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {

                    mBinding.setDiscovery(discoveryBtStrBuilder.toString());

                });

                Log.e("^_*>>>", discoveryBtStrBuilder.toString());

                if (device.getName().contains("1324D")) {

                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                        device.setPin("0000".getBytes());

                        if (device.createBond()) {

                        }

                    }
                }


            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                //todo


            }

        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_ENABLE_BT == requestCode) {

        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Objects.requireNonNull(getActivity()).unregisterReceiver(mReceiver);

        unbindService();

        if (mBinding != null)
            mBinding.unbind();

        mBinding = null;
    }

    private void bindService() {

        intentPrinterService = new Intent(getContext(), PrinterService.class);

        Objects.requireNonNull(getActivity()).startService(intentPrinterService);

        Intent intent = new Intent(getContext(), PrinterService.class);

        Objects.requireNonNull(getActivity()).bindService(intent, mConnection, Context.BIND_AUTO_CREATE); // bindService

    }

    private void unbindService() {

        Objects.requireNonNull(getActivity()).unbindService(mConnection);

        Objects.requireNonNull(getActivity()).stopService(intentPrinterService);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            PrinterService.LocalBinder binder = (PrinterService.LocalBinder) service;

            mPrinterService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mPrinterService = null;
        }
    };


}
