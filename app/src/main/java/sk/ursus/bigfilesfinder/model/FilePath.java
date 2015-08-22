package sk.ursus.bigfilesfinder.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by ursusursus on 19.8.2015.
 */
public class FilePath implements Parcelable {

    private final String mPath;
    private final long mSize;

    public static FilePath fromFile(File file) {
        return new FilePath(file.getAbsolutePath(), file.length());
    }

    protected FilePath(String path, long size) {
        mPath = path;
        mSize = size;
    }

    public File toFile() {
        return new File(mPath);
    }

    public String getPath() {
        return mPath;
    }

    public long getSize() {
        return mSize;
    }

    // Parcelable boilerplate
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeLong(mSize);
    }

    protected FilePath(Parcel in) {
        mPath = in.readString();
        mSize = in.readLong();
    }

    public static final Creator<FilePath> CREATOR = new Creator<FilePath>() {
        public FilePath createFromParcel(Parcel source) {
            return new FilePath(source);
        }

        public FilePath[] newArray(int size) {
            return new FilePath[size];
        }
    };

    public String getName() {
        return "VID1065156.mp4";
    }
}
