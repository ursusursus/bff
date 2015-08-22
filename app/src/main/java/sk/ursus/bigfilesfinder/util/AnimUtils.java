package sk.ursus.bigfilesfinder.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by ursusursus on 22.8.2015.
 */
public class AnimUtils {

    private static TimeInterpolator sDecelerateInterpolator = new DecelerateInterpolator();
    private static OvershootInterpolator sOvershootInterpolator = new OvershootInterpolator();

    private static final long FADE_DURATION = 350;
    public static final int BOUNCE_DURATION = 250;

    public static void crossfade(final View fadeInView, final View fadeOutView) {
        fadeInView.setVisibility(View.VISIBLE);
        fadeInView.setAlpha(0F);
        fadeInView.animate().alpha(1F)
                .setDuration(FADE_DURATION)
                .setInterpolator(sDecelerateInterpolator)
                .setListener(null);

        fadeOutView.setVisibility(View.VISIBLE);
        fadeOutView.setAlpha(1F);
        fadeOutView.animate().alpha(0F)
                .setDuration(FADE_DURATION)
                .setInterpolator(sDecelerateInterpolator)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeOutView.setVisibility(View.GONE);
                    }
                });
    }

    public static void bounceIn(final FloatingActionButton fab) {
        fab.setVisibility(View.VISIBLE);
        fab.setAlpha(0F);
        fab.setScaleX(0F);
        fab.setScaleY(0F);
        fab.animate()
                .setDuration(BOUNCE_DURATION)
                .setInterpolator(sOvershootInterpolator)
                .alpha(1F)
                .scaleX(1F)
                .scaleY(1F)
                .setListener(null);
    }

    public static void bounceOut(final FloatingActionButton fab) {
        fab.setAlpha(1F);
        fab.setScaleX(1F);
        fab.setScaleY(1F);
        fab.animate()
                .setDuration(BOUNCE_DURATION)
                .setInterpolator(sOvershootInterpolator)
                .alpha(0F)
                .scaleX(0F)
                .scaleY(0F)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
