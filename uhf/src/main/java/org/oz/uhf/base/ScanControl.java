package org.oz.uhf.base;


/**
 * @Name ScanControl.java
 * @package org.oz.uhf.base
 * @Author oz
 * @Email 857527916@qq.com
 * @Time 2019/1/7 12:16
 * @Description Tag扫描器控制接口， 用于多标签扫描，控制扫描器的结束
 */
public interface ScanControl {


    /**
     * @Name stop
     * @Params []
     * @Return void
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/7 12:18
     * @Description 条可停止扫描
     */
    void stop();

    /**
     * @Name isScan
     * @Params []
     * @Return boolean
     * @Author oz
     * @Email 857527916@qq.com
     * @Time 2019/1/7 16:47
     * @Description 是否扫描中
     */
    boolean isScan();
}
