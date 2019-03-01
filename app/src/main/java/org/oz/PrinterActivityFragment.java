package org.oz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.yf.btp.PrinterService;

import org.oz.dailog.BTDailog;
import org.oz.databinding.FragmentPrinterBinding;
import org.oz.entity.BtDev;

import java.util.Objects;

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

    private BTDailog mBTDailog;

    public class Handles {

        public void onPrinter(View v) {

            Log.i("^_*>>>", mPrinterService.getPackageName());
        }

        public void onDiscovery(View v) {

            discoveryBt();

        }

    }

    private void discoveryBt() {

        mBTDailog.show();

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_printer, container, false);

        mBinding.setLifecycleOwner(this);

        mBinding.setHandles(mHandles);

        return mBinding.getRoot();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        bindService();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBtDialog();

        initData();

        initView();

    }

    private void initBtDialog() {

        mBTDailog = new BTDailog(getContext(), android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);

        mBTDailog.setOnControlListener(new BTDailog.OnControlListener() {
            @Override
            public void connect(BtDev dev) {

                try {

                    if (0 == mPrinterService.openPort(dev.getUuid(), PrinterService.PORT_TYPE_BLUETOOTH, dev.getMac(), 0)) {

                        Toast.makeText(getContext(), "connected!!!", Toast.LENGTH_SHORT).show();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void disconnect(BtDev dev) {

                try {
                    mPrinterService.closePort(dev.getUuid());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            }
        });

    }


    private StringBuilder btStrBuilder = new StringBuilder();


    private void contactBtStr(BleDevice device) {

        btStrBuilder.append("Device Name :").append(device.getName()).append('\n');

        btStrBuilder.append("Device Rssi :").append(device.getRssi()).append('\n');

        btStrBuilder.append("Device Address :").append(device.getMac()).append('\n').append('\n').append('\n');

    }


    private void initData() {


    }

    private void initView() {


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        BleManager.getInstance().destroy();

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
