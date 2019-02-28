package org.oz.uhf.base;


import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;

/**
 * @Name DataCallback.java
 * @package org.oz.uhf.base
 * @Author oz
 * @Email 857527916@qq.com
 * @Time 2019/1/4 16:17
 * @Description 数据回调
 */
public interface DataStatefulCallback<T> {


    /**
     * @Name callback
     * @Params [succeed,
     * data
     * ]
     * @Return void
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/8 18:21
     * @Description 使用该回调可得到UHF的执行命令的状态和回调数据 succeed为false时 data为空
     */
    @AnyThread
    void callback(boolean succeed, @Nullable T data);

}
