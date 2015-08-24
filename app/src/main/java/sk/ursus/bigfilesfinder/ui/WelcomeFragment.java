package sk.ursus.bigfilesfinder.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import sk.ursus.bigfilesfinder.R;
import sk.ursus.bigfilesfinder.util.AnimUtils;

/**
 * Created by vbrecka on 21.8.2015.
 */
public class WelcomeFragment extends BaseFragment {

    public static final String TAG = "welcome_fragment";

    private static final int DURATION_ALPHA = 1250;
    private static final int DURATION_TRANSLATE = 1250;
    private static final int START_OFFSET = 125;

    public interface OnWelcomeFragmentListener {
        void onWelcomeFragmentFinished();
    }
    private OnWelcomeFragmentListener mListener;

    public static WelcomeFragment newInstance() {
        final WelcomeFragment f = new WelcomeFragment();
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnWelcomeFragmentListener) {
            mListener = (OnWelcomeFragmentListener) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View revealView = view.findViewById(R.id.revealView);
        final TextView textView1 = (TextView) view.findViewById(R.id.textView1);
        final TextView textView2 = (TextView) view.findViewById(R.id.textView2);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AnimUtils.reveal(getResources(), fab, revealView, new Runnable() {
                        @Override
                        public void run() {
                            if(mListener != null) {
                                mListener.onWelcomeFragmentFinished();
                            }
                        }
                    });
                } else {
                    if(mListener != null) {
                        mListener.onWelcomeFragmentFinished();
                    }
                }
            }
        });

        if (savedInstanceState == null) {
            textView1.setAlpha(0F);
            textView1.setTranslationY(START_OFFSET);
            textView2.setAlpha(0F);
            textView2.setTranslationY(START_OFFSET);
            fab.setVisibility(View.INVISIBLE);

            textView1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    textView1.getViewTreeObserver().removeOnPreDrawListener(this);

                    final LinearInterpolator alphaInterpolator = new LinearInterpolator();
                    final DecelerateInterpolator translateInterpolator = new DecelerateInterpolator();

                    // TextView1
                    final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(textView1, View.ALPHA, 0F, 1F);
                    alphaAnimator.setInterpolator(alphaInterpolator);
                    alphaAnimator.setDuration(DURATION_ALPHA);

                    final ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(textView1, View.TRANSLATION_Y, textView1.getTranslationY(), 0F);
                    translateAnimator.setInterpolator(translateInterpolator);
                    translateAnimator.setDuration(DURATION_TRANSLATE);

                    // TextView2
                    final ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(textView2, View.ALPHA, 0F, 1F);
                    alphaAnimator2.setInterpolator(alphaInterpolator);
                    alphaAnimator2.setDuration(DURATION_ALPHA);

                    final ObjectAnimator translateAnimator2 = ObjectAnimator.ofFloat(textView2, View.TRANSLATION_Y, textView2.getTranslationY(), 0F);
                    translateAnimator2.setInterpolator(translateInterpolator);
                    translateAnimator2.setDuration(DURATION_TRANSLATE);

                    final AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(alphaAnimator, translateAnimator);
                    animatorSet.setStartDelay(150);
                    animatorSet.start();

                    final AnimatorSet animatorSet2 = new AnimatorSet();
                    animatorSet2.setStartDelay(450);
                    animatorSet2.playTogether(alphaAnimator2, translateAnimator2);
                    animatorSet2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            AnimUtils.bounceIn(fab);
                        }
                    });
                    animatorSet2.start();
                    return true;
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
