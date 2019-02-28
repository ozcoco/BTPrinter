package org.oz.uhf.base;

/**
 * @Name RESULT
 * @package org.oz.demo.ui.rfid
 * @Author oz
 * @Email 857527916@qq.com
 * @Time 2018/12/28 19:56
 * @Description UHF调用命令操作结果值，通过该类可得知返回值代表的含义
 */
public final class RESULT {

    public final static int SUCCESS = 0x00; //执行成功。
    public final static int ERR_NO_TAG = 0x01; //未询查到电子标签
    public final static int ERR_ACCESS_PASSWD = 0x05; //访问密码错误
    public final static int ERR_DESTROY_PASSWD = 0x09; //销毁密码错误
    public final static int ERR_DESTROY_PASSWD_INCOMPLETE = 0x0A; //销毁密码不能为全0
    public final static int ERR_NO_SUPPORT = 0x0B; //电子标签不支持该命令
    public final static int ERR_ACCESS_PASSWD_0 = 0x0C; //对该命令，访问密码不能为0
    public final static int ERR_TAG_PROTECTED = 0x0D; //电子标签已经被设置了读保护，不能再次设置
    public final static int ERR_TAG_NO_PROTECTED = 0x0E; //电子标签没有被设置读保护，不需要解锁

    public final static int ERR_MEM_LOCKED = 0x10; //有字节空间被锁定，写入失败。
    public final static int ERR_NO_LOCK = 0x11; //不能锁定
    public final static int ERR_NO_AGAIN_LOCK = 0x12; //已经锁定，不能再次锁定
    public final static int ERR_SAVE_FAIL = 0x13; //参数保存失败,但设置的值在读写模块断电前有效
    public final static int ERR_NO_REVISION = 0x14; //无法调整
    public final static int ERR_CHECK_BEFORE_RESULT = 0x15; //询查时间结束前返回
    public final static int ERR_ALSO_MSG = 0x17; //本条消息之后，还有消息
    public final static int ERR_FULL = 0x18; //读写模块存储空间已满
    public final static int ERR_COMMAND_NO_SUPPORT = 0x19; //电子不支持该命令或者访问密码不能为0

    public final static int ERR_AERIAL = 0xF8; //天线检测错误
    public final static int ERR_COMMAND = 0xF9; //命令执行出错
    public final static int ERR_WEAK_SIGNAL = 0xFA; //有电子标签，但通信不畅，无法操作
    public final static int ERR_NO_TAG_OPERAT = 0xFB; //无电子标签可操作
    public final static int ERR_TAG_RETURN_ERR_CODE = 0xFC; //电子标签返回错误代码
    public final static int ERR_COMMAND_LEN = 0xFD; //命令长度错误
    public final static int ERR_ILLEGAL_COMMAND = 0xFE; //不合法的命令
    public final static int ERR_ILLEGAL_PARAMETER = 0xFF; //参数错误
    public final static int ERR_COMMUNICATION = 0x30; //通讯错误

}
