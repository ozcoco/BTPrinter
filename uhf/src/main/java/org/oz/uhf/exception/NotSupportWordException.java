package org.oz.uhf.exception;

/**
 * @Name NotSupportWordException
 * @package org.oz.uhf.exception
 * @Author oz
 * @Email 857527916@qq.com
 * @Time 2019/1/8 17:04
 * @Description UHF不支持的字符串长度
 */
public class NotSupportWordException extends Exception {

    public NotSupportWordException() {

        this("字符串长度必须满足 length%4=0 ");
    }

    public NotSupportWordException(String message) {
        super(message);
    }
}
