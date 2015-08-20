package sk.ursus.bigfilesfinder;

import android.os.Build;
import android.transition.TransitionManager;
import android.view.ViewGroup;

/**
 * Created by ursusursus on 20.8.2015.
 */
public class Utils {

    public static void beginDelayedTransition(ViewGroup viewGroup) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(viewGroup);
        }
    }
}
