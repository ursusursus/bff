package sk.ursus.bigfilesfinder.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewAnimationUtils;
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void reveal(Resources res, FloatingActionButton fab, View revealView, final Runnable endAction) {
        // Oh Android...
        int statusBarHeight = 0;
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        final Rect rect = new Rect();
        fab.getGlobalVisibleRect(rect);

        //
        final int cX = rect.centerX();
        final int cY = rect.centerY() - statusBarHeight;
        final int radius = (int) Math.sqrt(Math.pow(cX, 2) + Math.pow(cY, 2));

        final Animator revealAnim = ViewAnimationUtils.createCircularReveal(revealView, cX, cY, 0F, radius);
        revealAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (endAction != null) {
                    endAction.run();
                }
            }
        });
        revealView.setVisibility(View.VISIBLE);
        revealAnim.start();
    }
}
