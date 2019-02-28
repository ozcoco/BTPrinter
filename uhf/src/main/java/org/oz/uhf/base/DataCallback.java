package org.oz.uhf.base;


import androidx.annotation.WorkerThread;

/**
 * @Name DataCallback.java
 * @package org.oz.uhf.base
 * @Author oz
 * @Email 857527916@qq.com
 * @Time 2019/1/4 16:17
 * @Description 数据回调
 */
public interface DataCallback<T> {

    @WorkerThread
    void callback(T data);

}
