package org.oz.uhf.base;

import android.content.Context;

import androidx.annotation.Nullable;


/**
 * @Name ILifecycle.java
 * @package org.oz.uhf
 * @Author oz
 * @Email 857527916@qq.com
 * @Time 2019/1/4 12:13
 * @Description 用于管理Service的生命周期
 */
public interface ILifecycle {

    void init(@Nullable Context context);

    void onCleared();

}
