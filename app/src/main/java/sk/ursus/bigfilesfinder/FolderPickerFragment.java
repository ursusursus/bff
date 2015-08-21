package sk.ursus.bigfilesfinder;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FolderPickerFragment extends BaseFragment {

    public static final String TAG = "file_picker";
    private FilesAdapter mAdapter;
    private File mCurrentFolder;
    private HashSet<File> mSelectedFolders;
    private FileChipsView mChipsView;
    private Toolbar mToolbar;

    public static FolderPickerFragment newInstance() {
        FolderPickerFragment f = new FolderPickerFragment();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedFolders = new HashSet<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filepicker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle("Choose folders");

        mChipsView = (FileChipsView) view.findViewById(R.id.chipsView);
        mChipsView.setOnDismissListener(new FileChipsView.OnDismissListener() {
            @Override
            public void onDismiss(File file) {
                Utils.beginDelayedTransition((ViewGroup) getView());
                removeFolder(file);
                updateChipsView();
            }
        });

        final Button doneButton = (Button) view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Default", "DONE=" + mSelectedFolders.size());
                ArrayList<FilePath> filepaths = new ArrayList<>();
                final Iterator<File> iter = mSelectedFolders.iterator();
                while (iter.hasNext()) {
                    File file = iter.next();
                    filepaths.add(FilePath.fromFile(file));
                    Log.d("Default", "FILE=" + file.getAbsolutePath());
                }

                FinderService.launch(getActivity(), 5, filepaths);
            }
        });

        mAdapter = new FilesAdapter(getActivity(), mCheckedListener);
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(mItemClickListener);
        listView.setAdapter(mAdapter);

        // Root sdcard
        mCurrentFolder = Environment.getExternalStorageDirectory();
        navigateIn(mCurrentFolder);
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
        mAdapter.setFiles(files);
        mAdapter.notifyDataSetChanged();

        mCurrentFolder = file;
        mToolbar.setSubtitle(file.getAbsolutePath());
    }

    @Override
    public boolean onBackPressed() {
        Log.d("Default", "onBackPress");
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
        mChipsView.setVisibility(mChipsView.getChildCount() > 0 ? View.VISIBLE : View.GONE);
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
            Log.d("Default", "onChecked pos=" + position + " checked=" + checked);
            final File file = (File) mAdapter.getItem(position);

            Utils.beginDelayedTransition((ViewGroup) getView());
            if (checked) {
                addFolder(file);
            } else {
                removeFolder(file);
            }
            updateChipsView();
        }
    };

    private void removeFolder(File file) {
        mSelectedFolders.remove(file);
        mChipsView.remove(file);
    }

    private void addFolder(File file) {
        mSelectedFolders.add(file);
        mChipsView.add(file);
    }

    private static final Comparator<File> ALPHABETICAL_ORDER = new Comparator<File>() {

        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
        }
    };
}
