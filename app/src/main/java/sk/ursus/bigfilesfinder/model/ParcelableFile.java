package sk.ursus.bigfilesfinder.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by ursusursus on 19.8.2015.
 */
public class ParcelableFile implements Parcelable {

    private final String mName;
    private final String mPath;
    private final long mSize;

    public static ParcelableFile fromFile(File file) {
        return new ParcelableFile(file.getName(), file.getAbsolutePath(), file.length());
    }

    protected ParcelableFile(String name, String path, long size) {
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
    public static final Creator<ParcelableFile> CREATOR = new Creator<ParcelableFile>() {
        public ParcelableFile createFromParcel(Parcel source) {
            return new ParcelableFile(source);
        }

        public ParcelableFile[] newArray(int size) {
            return new ParcelableFile[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPath);
        dest.writeLong(mSize);
    }

    protected ParcelableFile(Parcel in) {
        mName = in.readString();
        mPath = in.readString();
        mSize = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
