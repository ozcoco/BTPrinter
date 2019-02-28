package org.oz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import org.oz.uhf.IUHFService;
import org.oz.uhf.UHFService;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());


        initData();

        initView();

    }

    private void initView() {

    }

    private void initData() {

        UHFService.getInstance().init(this);

        UHFService.getInstance().connect();


    }


    private final BroadcastReceiver keyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int keyCode = intent.getIntExtra("keyCode", 0);
            // H941
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }

            boolean keyDown = intent.getBooleanExtra("keydown", false);

            if (keyCode == KeyEvent.KEYCODE_F3) {
                //todo

                Log.e("BroadcastReceiver>>>>>>", String.format(Locale.CHINA, "button------>1 : %d", KeyEvent.KEYCODE_F3));

                //                ToastUtils.info(context, String.format(Locale.CHINA, "button : %d", KeyEvent.KEYCODE_F3), Gravity.CENTER_VERTICAL, Toast.LENGTH_SHORT).show();

                if (keyDown) {


                    scan();

                } else {


                }

            }

        }
    };


    private void scan() {

        new Thread(() -> UHFService.getInstance().singleScan(IUHFService.SCAN_TYPE.TID, tag -> {

            //todo
            Log.e("^_*>>>>>>>tagTID:", tag.getData().toLowerCase());

        })).start();

    }


    @Override
    public void onStart() {
        super.onStart();

        //注册扫码按钮广播接收器，监听扫码按钮事件
        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        registerReceiver(keyReceiver, filter);

    }


    @Override
    public void onPause() {
        super.onPause();

        //注销扫码按钮广播接收器
        unregisterReceiver(keyReceiver);
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
//        System.loadLibrary("native-lib");
    }
}
