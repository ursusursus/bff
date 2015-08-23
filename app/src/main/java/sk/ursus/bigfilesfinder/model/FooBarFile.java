package sk.ursus.bigfilesfinder.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by ursusursus on 19.8.2015.
 */
public class FooBarFile implements Parcelable {

    private final String mName;
    private final String mPath;
    private final long mSize;

    public static FooBarFile fromFile(File file) {
        return new FooBarFile(file.getName(), file.getAbsolutePath(), file.length());
    }

    protected FooBarFile(String name, String path, long size) {
        mName = name;
        mPath = path;
        mSize = size;
    }

    public String getPath() {
        return mPath;
    }

    public long getSize() {
        return mSize;
    }

    public String getName() {
        return mName;
    }

    // Parcelable boilerplate bonanza
    public static final Creator<FooBarFile> CREATOR = new Creator<FooBarFile>() {
        public FooBarFile createFromParcel(Parcel source) {
            return new FooBarFile(source);
        }

        public FooBarFile[] newArray(int size) {
            return new FooBarFile[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPath);
        dest.writeLong(mSize);
    }

    protected FooBarFile(Parcel in) {
        mName = in.readString();
        mPath = in.readString();
        mSize = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
