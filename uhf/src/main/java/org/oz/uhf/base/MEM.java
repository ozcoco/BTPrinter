package org.oz.uhf.base;

/**
 * 存储区
 */
public enum MEM {

    RESERVED("保留区", (byte) 0x00, 4, 4),
    EPC("EPC区", (byte) 0x01, 17, 15), // 0->2 word 只可读
    TID("TID区", (byte) 0x02, 4, 0),
    USER("用户区", (byte) 0x03, 32, 32),
    ;

    /**
     * 存储区名称
     */
    private String name;

    /**
     * 存储区类型编码
     */
    private byte type;

    /**
     * 存储区可读的最大字长
     */
    private int readMax;

    /**
     * 存储区写的最大字长
     */
    private int writeMax;

    /**
     * 存储区访问密码, 默认为000000000
     */
    private String password = "00000000";


    MEM(String name, byte type, int readMax, int writeMax) {
        this.name = name;
        this.type = type;
        this.readMax = readMax;
        this.writeMax = writeMax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getReadMax() {
        return readMax;
    }

    public void setReadMax(int readMax) {
        this.readMax = readMax;
    }

    public int getWriteMax() {
        return writeMax;
    }

    public void setWriteMax(int writeMax) {
        this.writeMax = writeMax;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
