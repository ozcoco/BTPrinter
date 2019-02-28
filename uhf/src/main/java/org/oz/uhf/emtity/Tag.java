package org.oz.uhf.emtity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @Name Tag
 * @package org.oz.uhf.base
 * @Author oz
 * @Email 857527916@qq.com
 * @Time 2019/1/4 16:08
 * @Description 标签实体类
 */
public class Tag implements Serializable, Cloneable, Parcelable {

    private String data;

    public Tag() {
    }

    public Tag(String data) {
        this.data = data;
    }

    protected Tag(Parcel in) {
        data = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "data='" + data + '\'' +
                '}';
    }
}
