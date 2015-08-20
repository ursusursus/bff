package sk.ursus.bigfilesfinder;

import android.content.Context;
import android.os.Build;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(this);
        }
        addView(childView);

    }
}
