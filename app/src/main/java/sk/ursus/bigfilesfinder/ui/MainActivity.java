package sk.ursus.bigfilesfinder.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import sk.ursus.bigfilesfinder.FinderService;
import sk.ursus.bigfilesfinder.R;

public class MainActivity extends AppCompatActivity implements
        WelcomeFragment.OnWelcomeFragmentListener,
        FolderPickerFragment.OnFolderPickerFragmentListener,
        CountPickerFragment.OnCountPickerFragmentListener,
        ResultsFragment.OnResultsFragmentListener {


    private static final String EXTRA_SELECTED_PATHS = "selected_paths";
    private static final String EXTRA_COUNT = "count";

    public interface BackListener {
        boolean onBackPressed();
    }

    private BackListener mBackListener;
    private ArrayList<String> mSelectedPaths;
    private int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            swap(WelcomeFragment.newInstance(), WelcomeFragment.TAG, false);
        } else {
            mSelectedPaths = (ArrayList<String>) savedInstanceState.getStringArrayList(EXTRA_SELECTED_PATHS);
            mCount = savedInstanceState.getInt(EXTRA_COUNT);
        }
    }

    private void swap(BaseFragment f, String tag) {
        swap(f, tag, true);
    }

    private void swap(BaseFragment f, String tag, boolean animate) {
        final FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.addToBackStack(null);
        if (animate) {
            tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        }
        tr.replace(R.id.container, f, tag);
        tr.commit();
    }

    @Override
    public void onWelcomeFragmentFinished() {
        swap(FolderPickerFragment.newInstance(), FolderPickerFragment.TAG);
    }

    @Override
    public void onFolderPickerFragmentFinished(HashSet<String> selectedPathsSet) {
        final ArrayList<String> selectedPaths = new ArrayList<>();
        final Iterator<String> iter = selectedPathsSet.iterator();
        while (iter.hasNext()) {
            selectedPaths.add(iter.next());
        }

        mSelectedPaths = selectedPaths;
        swap(CountPickerFragment.newInstance(), CountPickerFragment.TAG);
    }

    @Override
    public void onCountPickerFragmentFinished(int count) {
        mCount = count;
        swap(ResultsFragment.newInstance(), ResultsFragment.TAG);

        FinderService.launch(MainActivity.this, mCount, mSelectedPaths);
    }

    @Override
    public void onResultsFragmentFinished() {
        mCount = 0;
        mSelectedPaths.clear();
        swap(WelcomeFragment.newInstance(), WelcomeFragment.TAG);
    }

    @Override
    public void onBackPressed() {
        if (mBackListener != null) {
            if (mBackListener.onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBackListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(EXTRA_SELECTED_PATHS, mSelectedPaths);
        outState.putInt(EXTRA_COUNT, mCount);
    }

    public void registerBackListener(BackListener listener) {
        mBackListener = listener;
    }

    public void unregisterBackListener(BackListener listener) {
        mBackListener = null;
    }
}
