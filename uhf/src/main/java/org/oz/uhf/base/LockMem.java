package org.oz.uhf.base;

/**
 * @Name LockMem.java
 * @package org.oz.uhf.base
 * @Author oz
 * @Email 857527916@qq.com
 * @Time 2019/1/9 10:36
 * @Description 锁定存储区参数
 */
public interface LockMem {


    /**
     * 可锁定的存储区
     */
    enum PROTECT_MEM {

        KILL((byte) 0x00),  //控制Kill密码读写保护设定
        ACCESS_PWD((byte) 0x01), //控制访问密码读写保护设定
        EPC((byte) 0x02), //控制EPC存储器读写保护设定
        TID((byte) 0x03), // 控制TID存储器读写保护设定
        USER((byte) 0x04) // 控制用户存储器读写保护设定
        ;

        private byte value;

        PROTECT_MEM(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }


    /**
     * 可锁定的存储区的权限
     * 当PROTECT_MEM为0x00或0x01，PROTECT值代表的意义如下:
     * 0x00 – 设置为可读写
     * 0x01 – 设置为永远可读写
     * 0x02 – 设置为带密码可读写
     * 0x03 – 设置为永远不可读写
     * 当Select为0x02、0x03、0x04时，SetProtect值代表的意义如下:
     * 0x00 – 设置为可写
     * 0x01 – 设置为永远可写
     * 0x02 – 设置为带密码可写
     * 0x03 – 设置为永远不可写。
     */
    enum PROTECT {
        RW((byte) 0x00),
        F_RW((byte) 0x01),
        PWD_RW((byte) 0x02),
        N_RW((byte) 0x03);

        private byte value;

        PROTECT(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

}
