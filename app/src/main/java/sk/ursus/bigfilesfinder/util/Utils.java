package sk.ursus.bigfilesfinder.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by ursusursus on 20.8.2015.
 */
public class Utils {

    public static void beginDelayedTransition(ViewGroup viewGroup) {
        beginDelayedTransition(viewGroup, null);
    }

    public static void beginDelayedTransition(ViewGroup viewGroup, Transition transition) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(viewGroup, transition);
        }
    }

    public static void hideKeyboard(Activity activity) {
        final View v = activity.getCurrentFocus();
        if (v != null) {
            final InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        }
    }

    public static long bytesToMegabytes(long bytes) {
        return bytes / (1024 * 1024);
    }
}
