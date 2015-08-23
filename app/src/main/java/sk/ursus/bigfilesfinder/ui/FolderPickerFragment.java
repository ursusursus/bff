package sk.ursus.bigfilesfinder.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import sk.ursus.bigfilesfinder.FileChipsView;
import sk.ursus.bigfilesfinder.R;
import sk.ursus.bigfilesfinder.adapter.FilesAdapter;
import sk.ursus.bigfilesfinder.util.AnimUtils;
import sk.ursus.bigfilesfinder.util.Utils;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FolderPickerFragment extends BaseFragment implements MainActivity.BackListener {

    public static final String TAG = "file_picker";
    private static final String EXTRA_SELECTED_PATHS = "selected_paths";
    private static final String EXTRA_CURRENT_FOLDER = "current_folder";

    private FilesAdapter mAdapter;
    private File mCurrentFolder;
    private HashSet<String> mSelectedPaths;
    private FileChipsView mChipsView;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private TransitionWrapper mTransitionWrapper;

    public static FolderPickerFragment newInstance() {
        FolderPickerFragment f = new FolderPickerFragment();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectedPaths = (HashSet<String>) savedInstanceState.getSerializable(EXTRA_SELECTED_PATHS);
        } else {
            mSelectedPaths = new HashSet<>();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).registerBackListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).unregisterBackListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folderpicker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.toolbar_choose_folders);

        mChipsView = (FileChipsView) view.findViewById(R.id.chipsView);
        mChipsView.setOnDismissListener(new FileChipsView.OnDismissListener() {
            @Override
            public void onDismiss(File file) {
                Utils.beginDelayedTransition((ViewGroup) getView(), mTransitionWrapper.getTransition());
                removeFolder(file);
                updateChipsView();
            }
        });

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).onFolderPickerFragmentFinished(mSelectedPaths);
            }
        });

        mAdapter = new FilesAdapter(getActivity(), mCheckedListener);
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(mItemClickListener);
        listView.setAdapter(mAdapter);

        //
        mTransitionWrapper = TransitionWrapper.newInstance(mFab);

        if (savedInstanceState != null && savedInstanceState.getString(EXTRA_CURRENT_FOLDER) != null) {
            mCurrentFolder = new File(savedInstanceState.getString(EXTRA_CURRENT_FOLDER));
        } else {
            // "Root"
            mCurrentFolder = Environment.getExternalStorageDirectory();
        }
        navigateIn(mCurrentFolder);
        updateChipsView();
    }

    private File[] listFolders(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        return file.listFiles(mFilter);
    }

    private void navigateIn(File file) {
        final File[] files = listFolders(file);
        if (files == null || files.length <= 0) {
            Toast.makeText(getActivity(), "Folder " + file.getName() + " has no subfolders", Toast.LENGTH_SHORT).show();
            return;
        }

        Arrays.sort(files, ALPHABETICAL_ORDER);
        mAdapter.setSelected(mSelectedPaths);
        mAdapter.setFiles(files);
        mAdapter.notifyDataSetChanged();

        mCurrentFolder = file;
        mToolbar.setSubtitle(file.getAbsolutePath());
    }

    @Override
    public boolean onBackPressed() {
        Log.d("Default", "onBackPressed");
        if (mCurrentFolder != null) {
            final File parentFolder = mCurrentFolder.getParentFile();
            if (parentFolder != null) {
                navigateIn(parentFolder);
                return true;
            }
        }
        return false;
    }

    private void updateChipsView() {
        if (!mSelectedPaths.isEmpty()) {
            if (mFab.getVisibility() != View.VISIBLE) {
                AnimUtils.bounceIn(mFab);
            }
            mChipsView.setVisibility(View.VISIBLE);
        } else {
            if (mFab.getVisibility() != View.INVISIBLE) {
                AnimUtils.bounceOut(mFab);
            }
            mChipsView.setVisibility(View.GONE);
        }
    }

    private FileFilter mFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final File file = (File) mAdapter.getItem(position);
            navigateIn(file);
        }
    };

    private FilesAdapter.OnCheckedListener mCheckedListener = new FilesAdapter.OnCheckedListener() {
        @Override
        public void onChecked(int position, boolean checked) {
            final File file = (File) mAdapter.getItem(position);

            Utils.beginDelayedTransition((ViewGroup) getView(), mTransitionWrapper.getTransition());
            if (checked) {
                addFolder(file);
            } else {
                removeFolder(file);
            }
            update();
        }
    };

    private void update() {
        updateChipsView();
        mAdapter.setSelected(mSelectedPaths);
        mAdapter.notifyDataSetChanged();
    }

    private void removeFolder(File file) {
        mSelectedPaths.remove(file.getAbsolutePath());
        mChipsView.remove(file);
    }

    private void addFolder(File file) {
        mSelectedPaths.add(file.getAbsolutePath());
        mChipsView.add(file);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_SELECTED_PATHS, mSelectedPaths);
        if (mCurrentFolder != null) {
            outState.putString(EXTRA_CURRENT_FOLDER, mCurrentFolder.getAbsolutePath());
        }
    }

    private static final Comparator<File> ALPHABETICAL_ORDER = new Comparator<File>() {

        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
        }
    };

    public static abstract class TransitionWrapper {
        public static TransitionWrapper newInstance(FloatingActionButton fab) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return new RealTransition(fab);
            } else {
                return new DummyTransitionWrapper(fab);
            }
        }

        public abstract Transition getTransition();
    }

    public static class RealTransition extends TransitionWrapper {

        private final Transition mTransition;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public RealTransition(FloatingActionButton fab) {
            mTransition = new AutoTransition().excludeTarget(fab, true);
        }

        @Override
        public Transition getTransition() {
            return mTransition;
        }
    }

    public static class DummyTransitionWrapper extends TransitionWrapper {

        public DummyTransitionWrapper(FloatingActionButton fab) {
        }

        @Override
        public Transition getTransition() {
            return null;
        }
    }
}
