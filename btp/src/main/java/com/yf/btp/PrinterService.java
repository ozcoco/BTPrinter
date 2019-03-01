package com.yf.btp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.save.PortParamDataBase;
import com.gprinter.service.GpPrintService;

public class PrinterService extends Service {

    public final static int PORT_TYPE_BLUETOOTH = PortParameters.BLUETOOTH;

    public final static int PORT_TYPE_SERIAL = PortParameters.SERIAL;


    public static final String TAG = PrinterService.class.getCanonicalName();

    public static final String ACTION_CONNECT_STATUS = "action.connect.status";

    private GpService mGpService;

    private PrinterServiceConnection mConn;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {

        public PrinterService getService() {

            // Return this instance of LocalService so clients can call public methods
            return PrinterService.this;
        }
    }


    private void bindService() {

        mConn = new PrinterServiceConnection();

        Intent intent = new Intent(this, GpPrintService.class);

        bindService(intent, mConn, Context.BIND_AUTO_CREATE); // bindService

    }

    private void unbindService() {

        unbindService(mConn);
    }


    class PrinterServiceConnection implements ServiceConnection {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("^_* ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.i("^_* ServiceConnection", "onServiceConnected() called");

            mGpService = GpService.Stub.asInterface(service);

            Log.i("^_* ServiceConnection", mGpService.toString());

        }
    }


    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (ACTION_CONNECT_STATUS.equals(intent.getAction())) {

                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);

                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);

                Log.d(TAG, "^_* connect status " + id + ":" + type);

                if (type == GpDevice.STATE_CONNECTING) {


                } else if (type == GpDevice.STATE_NONE) {


                } else if (type == GpDevice.STATE_VALID_PRINTER) {

                    try {
                        printeTestPage(id);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                } else if (type == GpDevice.STATE_INVALID_PRINTER) {

                }
            }
        }
    };


    private void registerBroadcast() {

        IntentFilter filter = new IntentFilter();

        filter.addAction(ACTION_CONNECT_STATUS);

        registerReceiver(PrinterStatusBroadcastReceiver, filter);
    }

    private void unRegisterBroadcast() {

        unregisterReceiver(PrinterStatusBroadcastReceiver);

    }

    public PrinterService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerBroadcast();

        bindService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unRegisterBroadcast();

        unbindService();
    }

    public int openPort(int PrinterId, int PortType, String DeviceName, int PortNumber) throws RemoteException {

        return mGpService.openPort(PrinterId, PortType, DeviceName, PortNumber);

    }

    public void closePort(int PrinterId) throws RemoteException {

        mGpService.closePort(PrinterId);

    }

    public int getPrinterConnectStatus(int PrinterId) throws RemoteException {

        return mGpService.getPrinterConnectStatus(PrinterId);
    }

    public int printeTestPage(int PrinterId) throws RemoteException {

        return mGpService.printeTestPage(PrinterId);
    }

    public void queryPrinterStatus(int PrinterId, int Timesout, int requestCode) throws RemoteException {
        mGpService.queryPrinterStatus(PrinterId, Timesout, requestCode);
    }

    public int getPrinterCommandType(int PrinterId) throws RemoteException {

        return mGpService.getPrinterCommandType(PrinterId);

    }

    public int sendEscCommand(int PrinterId, String b64) throws RemoteException {
        return mGpService.sendEscCommand(PrinterId, b64);
    }

    public int sendLabelCommand(int PrinterId, String b64) throws RemoteException {
        return mGpService.sendLabelCommand(PrinterId, b64);
    }

    public void isUserExperience(boolean userExperience) throws RemoteException {
        mGpService.isUserExperience(userExperience);
    }

    public String getClientID() throws RemoteException {
        return mGpService.getClientID();
    }

    public int setServerIP(String ip, int port) throws RemoteException {
        return mGpService.setServerIP(ip, port);
    }


}
