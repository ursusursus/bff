package sk.ursus.bigfilesfinder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FileChipsView extends FlowLayout {

    public interface OnDismissListener {
        void onDismiss(File file);
    }

    private ArrayList<File> mFiles = new ArrayList<>();
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

    public void add(final File file) {
        final View childView = mInflater.inflate(R.layout.chip, this, false);

        final TextView textView = (TextView) childView.findViewById(R.id.textView);
        textView.setText(file.getAbsolutePath());

        final ImageView cancelImageView = (ImageView) childView.findViewById(R.id.cancelImageView);
        cancelImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(file);
                }
            }
        });

        addView(childView);
        mFiles.add(file);
    }

    public void remove(File file) {
        final int index = mFiles.indexOf(file);
        if (index >= 0) {
            removeViewAt(index);
            mFiles.remove(index);
        }
    }
}
