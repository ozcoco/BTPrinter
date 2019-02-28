package org.oz.uhf;


import org.oz.uhf.base.DataCallback;
import org.oz.uhf.base.DataStatefulCallback;
import org.oz.uhf.base.ILifecycle;
import org.oz.uhf.base.LockMem;
import org.oz.uhf.base.MEM;
import org.oz.uhf.base.ScanControl;
import org.oz.uhf.emtity.Tag;
import org.oz.uhf.exception.NotSupportWordException;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

public interface IUHFService extends ILifecycle {

    /**
     * 扫描返回数据的存储区类型，用于标签扫描
     **/
    enum SCAN_TYPE {
        TID,    //扫描标签得到的数据为tid
        EPC     //扫描标签得到的数据为epc
    }

    /**
     * 盘存模式，用于标签扫描
     */
    enum SESSION_MODE {
        /*单标签*/
        SINGLE,
        /*多标签*/
        MULTI,
        /*自动*/
        AUTO
    }


    /**
     * @Name read
     * @Params [mem,    指定存储区
     * epc,     标签epc号
     * password,  指定存储区访问密码
     * start,   指定在指定存储区读取数据的起始位置
     * length ,  需要读写数据的长度，即字符个数，length必须满足length%4=0
     * callback     数据回调
     * ]
     * @Return void
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/8 16:24
     * @Description 读取指定存储区的数据
     */
    void read(@NonNull MEM mem, @NonNull String epc, @NonNull String password, int start, int length, @NonNull DataStatefulCallback<String> callback);

    /**
     * @Name write
     * @Params [mem,    指定存储
     * epc,     标签epc号
     * password,    指定存储区访问密码
     * start,       指定在指定存储区读取数据的起始位置
     * data     需要写入的数据，字符串的字符个数必须满足 length%4=0
     * ]
     * @Return boolean true: 成功， false: 失败
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/8 16:20
     * @Description 写入数据到指定的存储区
     */
    boolean write(@NonNull MEM mem, @NonNull String epc, @NonNull String password, int start, @NonNull String data) throws NotSupportWordException;


    /**
     * @Name lock
     * @Params []
     * @Return boolean true: 成功， false: 失败
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/8 16:04
     * @Description 锁定存储区，对存储区的读写进行限制
     */
    boolean lock(@NonNull String epc, @NonNull LockMem.PROTECT_MEM mem, @NonNull LockMem.PROTECT protect, @NonNull String password);


    /**
     * @Name setPower
     * @Params [power]
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/4 16:00
     * @Description 返回调用是否成功，true成功，反之失败
     */
    boolean setPower(int power);

    /**
     * @Name clearTagCache
     * @Params []
     * @Return void
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/7 15:19
     * @Description 清除Tag缓存
     */
    void clearTagCache();


    /**
     * @Name getPower
     * @Params []
     * @Return int
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/7 15:20
     * @Description 获取功率
     */
    int getPower();


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
    void singleScan(@NonNull SCAN_TYPE scanType, @NonNull DataCallback<Tag> callback);


    /**
     * @Name multiScan
     * @Params []
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/4 16:32
     * @Description 多Tag扫描，持续扫描标签，不能运行在UI线程中，返回扫描到有效标签的集合，需要控制扫描的结
     * 束。注：若扫描器使用不当，则扫不到Tag，例如：功率太低、Tag距离太远。
     */
    @WorkerThread
    ScanControl multiScan(@NonNull SCAN_TYPE scanType, @NonNull DataCallback<List<Tag>> callback);

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
    ScanControl autoScan(@NonNull SCAN_TYPE scanType, @NonNull DataCallback<List<Tag>> callback);


    /**
     * @Name connect
     * @Params []
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2018/12/20 14:17
     * @Description 通过串口，连接UHF,  return ,true connected, unable connected
     */
    boolean connect();

    /**
     * @Name close
     * @Params []
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2018/12/20 14:23
     * @Description 释放串口资源，断开UHF， return, true 成功，false失败
     */
    boolean close();

}
