package sk.ursus.bigfilesfinder;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by ursusursus on 19.8.2015.
 */
public class FileWrapper implements Parcelable {

    private final String mPath;

    public static FileWrapper fromFile(File file) {
        return new FileWrapper(file.getAbsolutePath());
    }

    protected FileWrapper(String path) {
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

    protected FileWrapper(Parcel in) {
        this.mPath = in.readString();
    }

    public static final Creator<FileWrapper> CREATOR = new Creator<FileWrapper>() {
        public FileWrapper createFromParcel(Parcel source) {
            return new FileWrapper(source);
        }

        public FileWrapper[] newArray(int size) {
            return new FileWrapper[size];
        }
    };

}
