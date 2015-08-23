package sk.ursus.bigfilesfinder;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FileChipsView extends FlowLayout {

    public interface OnDismissListener {
        void onDismiss(String folderPath);
    }

    private ArrayList<String> mFolderPaths = new ArrayList<>();
    private OnDismissListener mOnDismissListener;
    private LayoutInflater mInflater;

    public FileChipsView(Context context) {
        super(context);
        init();
    }

    public FileChipsView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public FileChipsView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
    }

    public void add(final String path) {
        final View childView = mInflater.inflate(R.layout.chip, this, false);

        final TextView textView = (TextView) childView.findViewById(R.id.textView);
        textView.setText(path);

        final ImageView cancelImageView = (ImageView) childView.findViewById(R.id.cancelImageView);
        cancelImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(path);
                }
            }
        });

        addView(childView);
        mFolderPaths.add(path);
    }

    public void remove(String folderPath) {
        final int index = mFolderPaths.indexOf(folderPath);
        if (index >= 0) {
            removeViewAt(index);
            mFolderPaths.remove(index);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), mFolderPaths);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState savedState = (SavedState) state;
        for (int i = 0; i < savedState.mFolderPaths.size(); i++) {
            add(savedState.mFolderPaths.get(i));
        }
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    private static class SavedState extends BaseSavedState {
        private ArrayList<String> mFolderPaths;

        public SavedState(Parcelable superState, ArrayList<String> folderPaths) {
            super(superState);
            mFolderPaths = folderPaths;
        }

        private SavedState(Parcel in) {
            super(in);
            mFolderPaths = new ArrayList<>();
            in.readStringList(mFolderPaths);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeStringList(mFolderPaths);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
