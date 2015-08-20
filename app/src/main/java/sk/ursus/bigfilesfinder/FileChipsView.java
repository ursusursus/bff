package sk.ursus.bigfilesfinder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FileChipsView extends FlowLayout {

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

    private void init() {
        mInflater = LayoutInflater.from(getContext());
    }

    public void add(File file) {
        final View childView = mInflater.inflate(R.layout.chip, this, false);

        final TextView textView = (TextView) childView.findViewById(R.id.textView);
        textView.setText(file.getAbsolutePath());
        final ImageView cancelImageView = (ImageView) childView.findViewById(R.id.cancelImageView);
        cancelImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.beginDelayedTransition(FileChipsView.this);
                removeView(childView);
                foobarVisibility();
            }
        });

        Utils.beginDelayedTransition(this);
        addView(childView);
        foobarVisibility();
    }

    private void foobarVisibility() {
        setVisibility(getChildCount() > 0 ? View.VISIBLE : View.GONE);
    }
}
