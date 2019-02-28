package org.oz.uhf;

import android.content.Context;
import android.os.Build;
import android.util.ArrayMap;

import com.uhf.scanlable.UHfData;

import org.oz.uhf.base.DataCallback;
import org.oz.uhf.base.DataStatefulCallback;
import org.oz.uhf.base.LockMem;
import org.oz.uhf.base.MEM;
import org.oz.uhf.base.ScanControl;
import org.oz.uhf.emtity.Tag;
import org.oz.uhf.exception.NotSupportWordException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import cn.pda.serialport.Tools;

public class UHFServiceImpl implements IUHFService {


    /**
     * UHF是否已经连接
     */
    private final AtomicBoolean isConnected = new AtomicBoolean(false);

    /**
     * UHF功率，默认值为30dBm
     **/
    private int power = 30;

    /**
     * 扫描结果为tid数据的标签集合
     */
    private ArrayMap<String, UHfData.InventoryTagMap> tidTagMap = new ArrayMap<>();

    /**
     * 扫描结果为epc数据的标签集合
     */
    private ArrayMap<String, UHfData.InventoryTagMap> epcTagMap = new ArrayMap<>();


    private UHFServiceImpl() {
    }

    public synchronized static IUHFService newInstance() {

        return new UHFServiceImpl();
    }

    @Override
    public void init(@Nullable Context context) {

        if (connect()) {
            isConnected.set(true);

            setPower(power);
        } else {
            init(null);
        }
    }

    @Override
    public void onCleared() {

        tidTagMap.clear();

        tidTagMap = null;

        epcTagMap.clear();

        epcTagMap = null;
    }


    /**
     * @Name read
     * @Params [mem,    指定存储区
     * epc,     标签epc号
     * password,  指定存储区访问密码
     * start,   指定在指定存储区读取数据的起始位置（单位为word）
     * length   需要读写数据的字长，即word = 2byte = 4bit(4个字符)
     * ]
     * @Return void
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/8 16:24
     * @Description 读取指定存储区的数据
     */
    @Override
    public synchronized void read(@NonNull MEM mem, @NonNull String epc, @NonNull String password, int start, int length, @NonNull DataStatefulCallback<String> callback) {

        final byte eNum = Integer.valueOf(epc.length() / 4).byteValue();

        final byte[] wEpc = UHfData.UHfGetData.hexStringToBytes(epc);

        final byte[] pwd = UHfData.UHfGetData.hexStringToBytes(password);

        final byte type = mem.getType();

        final byte[] wordPtr = Tools.intToByte(start);

        final byte len = Integer.valueOf(length).byteValue();

        if (UHfData.UHfGetData.Read6C(eNum, wEpc, type, wordPtr, len, pwd) == 0) {

            final byte[] hexData = UHfData.UHfGetData.getRead6Cdata();

            final String data = UHfData.UHfGetData
                    .bytesToHexString(hexData, 0, length * 2);
//                    .toUpperCase();

            callback.callback(true, data);

        } else {
            callback.callback(false, null);
        }
    }

    /**
     * @Name write
     * @Params [mem,    指定存储
     * epc,     标签epc号
     * password,    指定存储区访问密码
     * start,       指定在指定存储区读取数据的起始位置(单位为word)
     * data     需要写入的数据，字符串的字符个数必须满足 length%4=0
     * ]
     * @Return boolean true: 成功， false: 失败
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/8 16:20
     * @Description 写入数据到指定的存储区
     */
    @Override
    public synchronized boolean write(@NonNull MEM mem, @NonNull String epc, @NonNull String password, int start, @NonNull String data) throws NotSupportWordException {

        if (data.length() % 4 != 0)
            throw new NotSupportWordException();

        final byte len = Integer.valueOf(data.length() / 4).byteValue();

        if (len > mem.getWriteMax() - start)
            throw new NotSupportWordException("写入数据不能大于" + mem.getName());

        final byte eNum = Integer.valueOf(epc.length() / 4).byteValue();

        final byte[] pwd = UHfData.UHfGetData.hexStringToBytes(password);

        final byte[] wEpc = UHfData.UHfGetData.hexStringToBytes(epc);

        final byte[] wData = UHfData.UHfGetData.hexStringToBytes(data);

        final byte type = mem.getType();

        final byte[] wordPtr = Tools.intToByte(start);

        if (mem == MEM.EPC) {   // EPC存储区

            return UHfData.UHfGetData.WriteEPC(eNum, pwd, wEpc, wData) == 0;

        } else {  //保留区、TID存储区、用户存储区、其它

            return UHfData.UHfGetData.Write6c(len, eNum, wEpc, type, wordPtr, wData, pwd) == 0;
        }

    }

    /**
     * @Name lock
     * @Params []
     * @Return boolean true: 成功， false: 失败
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/8 16:04
     * @Description 锁定存储区，对存储区的读写进行限制
     */
    @Override
    public synchronized boolean lock(@NonNull String epc, @NonNull LockMem.PROTECT_MEM mem, @NonNull LockMem.PROTECT protect, @NonNull String password) {

        final int eNum = epc.length() / 4;

        final byte[] wEpc = UHfData.UHfGetData.hexStringToBytes(epc);

        final byte select = mem.getValue();

        final byte setProtect = protect.getValue();

        final byte[] pwd = UHfData.UHfGetData.hexStringToBytes(password);

        final String rst = UHfData.UHfGetData.lock6C(eNum, wEpc, select, setProtect, pwd);

        return Integer.valueOf(rst) == 0;
    }


    /**
     * @Name setPower
     * @Params [power]
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/4 16:31
     * @Description 返回调用是否成功，true成功，反之失败
     */
    @Override
    public boolean setPower(int power) {

        return 0 == UHfData.UHfGetData.SetRfPower(Integer.valueOf(this.power = power).byteValue());
    }


    /**
     * @Name clearTagCache
     * @Params []
     * @Return void
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/7 15:21
     * @Description 清除Tag缓存
     */
    @Override
    public void clearTagCache() {

        tidTagMap.clear();

        epcTagMap.clear();
    }


    /**
     * @Name getPower
     * @Params []
     * @Return int
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/7 15:20
     * @Description 获取功率
     */
    @Override
    public int getPower() {

        return power;
    }


    /**
     * @Name singleScan
     * @Params []
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/4 16:31
     * @Description 单个Tag扫描，一次扫描一个Tag 。注：并非每次都能扫到有效的Tag，若扫描器使用不
     * 当，则扫不到Tag；例如：功率太低、Tag距离太远。
     */
    @WorkerThread
    @Override
    public synchronized void singleScan(@NonNull SCAN_TYPE scanType, @NonNull DataCallback<Tag> callback) {

        new Thread(() -> {
            //清除累计的数据
            UHfData.lsTagList.clear();

            UHfData.dtIndexMap.clear();

            int session = 1;    //单标签

            int tidFlag = scanType == SCAN_TYPE.TID ? 1 : 0;

            //检查有效范围内是否有符合协议的电子标签存在
            UHfData.Inventory_6c(session, tidFlag);

            final List<UHfData.InventoryTagMap> list = new ArrayList<>(UHfData.lsTagList);

            if (list.size() == 1) {

                final Tag tag = new Tag(list.get(0).strEPC);

                callback.callback(tag);

                if (scanType == SCAN_TYPE.TID)
                    tidTagMap.put(list.get(0).strEPC, list.get(0));
                else
                    epcTagMap.put(list.get(0).strEPC, list.get(0));

            } else if (list.size() > 1) {

                if (scanType == SCAN_TYPE.TID) { //结果集为tid

                    if (tidTagMap.size() > 0) { // tidTagMap集合有元素时

                        for (UHfData.InventoryTagMap bean : list) {

                            if (!tidTagMap.containsKey(bean.strEPC)) {

                                final Tag tag = new Tag(bean.strEPC);

                                callback.callback(tag);

                                tidTagMap.put(bean.strEPC, bean);

                                return;
                            }
                        }

                        final Tag tag = new Tag(list.get(0).strEPC);

                        callback.callback(tag);

                    } else {    // tidTagMap集合无元素时

                        final Tag tag = new Tag(list.get(0).strEPC);

                        callback.callback(tag);

                        tidTagMap.put(list.get(0).strEPC, list.get(0));
                    }

                } else { //结果集为epc

                    if (epcTagMap.size() > 0) {  // epcTagMap集合有元素时

                        for (UHfData.InventoryTagMap bean : list) {

                            if (!epcTagMap.containsKey(bean.strEPC)) {

                                final Tag tag = new Tag(bean.strEPC);

                                callback.callback(tag);

                                epcTagMap.put(bean.strEPC, bean);

                                return;
                            }
                        }

                        final Tag tag = new Tag(list.get(0).strEPC);

                        callback.callback(tag);

                    } else {    // epcTagMap集合无元素时

                        final Tag tag = new Tag(list.get(0).strEPC);

                        callback.callback(tag);

                        epcTagMap.put(list.get(0).strEPC, list.get(0));
                    }

                }
            }

        }).start();

    }


    /**
     * @Name multiScan
     * @Params []
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/4 16:32
     * @Description 多Tag扫描，持续扫描标签，返回扫描到有效标签的集合，需要控制扫描的结
     * 束。注：若扫描器使用不当，则扫不到Tag，例如：功率太低、Tag距离太远。
     */
    @WorkerThread
    @Override
    public synchronized ScanControl multiScan(@NonNull SCAN_TYPE scanType, @NonNull DataCallback<List<Tag>> callback) {

        final AtomicBoolean isScan = new AtomicBoolean(true);

        new Thread(() -> {

            for (; isScan.get(); ) {

                //清除累计的数据
                UHfData.lsTagList.clear();

                UHfData.dtIndexMap.clear();

                int session = 0;    //多标签

                int tidFlag = scanType == SCAN_TYPE.TID ? 1 : 0;

                //检查有效范围内是否有符合协议的电子标签存在
                UHfData.Inventory_6c(session, tidFlag);

                final List<UHfData.InventoryTagMap> list = new ArrayList<>(UHfData.lsTagList);

                if (list.size() == 0)
                    continue;

                final List<Tag> tags = new ArrayList<>();

                for (UHfData.InventoryTagMap bean : list)
                    tags.add(new Tag(bean.strEPC));

                callback.callback(tags);

            }

        }).start();

        return new ScanControl() {
            @Override
            public void stop() {
                isScan.set(false);
            }

            @Override
            public boolean isScan() {
                return isScan.get();
            }
        };
    }


    /**
     * @Name autoScan
     * @Params [scanType, sessionMode, callback]
     * @Return org.oz.uhf.base.ScanControl
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/7 15:13
     * @Description 自动Tag扫描，持续扫描标签，直到扫描到有效标签会自动结束扫描，也可控制结束扫描，返回扫描到有效标签的集
     * 合，需要控制扫描的结束。注：若扫描器使用不当，则扫不到Tag，例如：功率太低、Tag距离太远。
     */
    @WorkerThread
    @Override
    public synchronized ScanControl autoScan(@NonNull SCAN_TYPE scanType, @NonNull DataCallback<List<Tag>> callback) {

        final AtomicBoolean isScan = new AtomicBoolean(true);

        new Thread(() -> {

            for (; isScan.get(); ) {

                //清除累计的数据
                UHfData.lsTagList.clear();

                UHfData.dtIndexMap.clear();

                int session = 2;    //自动

                int tidFlag = scanType == SCAN_TYPE.TID ? 1 : 0;

                //检查有效范围内是否有符合协议的电子标签存在
                UHfData.Inventory_6c(session, tidFlag);

                final List<UHfData.InventoryTagMap> list = new ArrayList<>(UHfData.lsTagList);

                if (list.size() == 0)
                    continue;

                final List<Tag> tags = new ArrayList<>();

                for (UHfData.InventoryTagMap bean : list)
                    tags.add(new Tag(bean.strEPC));

                callback.callback(tags);

                break;
            }

        }).start();

        return new ScanControl() {
            @Override
            public void stop() {
                isScan.set(false);
            }

            @Override
            public boolean isScan() {
                return isScan.get();
            }
        };
    }

    /**
     * @Name connect
     * @Params []
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2018/12/20 14:17
     * @Description 通过串口，连接UHF,  return ,true connected, unable connected
     */
    @Override
    public boolean connect() {

        final int state = UHfData.UHfGetData.OpenUHf("/dev/ttyMT1", 57600);

        return state == 0;
    }


    /**
     * @Name close
     * @Params []
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2018/12/20 14:23
     * @Description 释放串口资源，断开UHF， return, true 成功，false失败
     */
    @Override
    public boolean close() {

        isConnected.set(false);

        UHfData.lsTagList.clear();

        UHfData.dtIndexMap.clear();

        return UHfData.UHfGetData.CloseUHf() == 0;
    }

}
