package sk.ursus.bigfilesfinder;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by ursusursus on 19.8.2015.
 */
public class FilePath implements Parcelable {

    private final String mPath;

    public static FilePath fromFile(File file) {
        return new FilePath(file.getAbsolutePath());
    }

    protected FilePath(String path) {
        mPath = path;
    }

    public File toFile() {
        return new File(mPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPath);
    }

    protected FilePath(Parcel in) {
        this.mPath = in.readString();
    }

    public static final Creator<FilePath> CREATOR = new Creator<FilePath>() {
        public FilePath createFromParcel(Parcel source) {
            return new FilePath(source);
        }

        public FilePath[] newArray(int size) {
            return new FilePath[size];
        }
    };

}
