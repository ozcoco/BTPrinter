package org.oz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yf.btp.PrinterService;

import org.oz.databinding.FragmentPrinterBinding;

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

    public class Handles {

        public void onPrinter(View v) {

            new Thread(() -> {

                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_btm);

                mPrinterService.printBitmap(bmp);

            }).start();

        }

        public void onDiscovery(View v) {

            mPrinterService.pickPrinter(getContext());

        }

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

        initData();

        initView();

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
