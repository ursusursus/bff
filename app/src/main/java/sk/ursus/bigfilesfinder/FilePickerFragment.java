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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FilePickerFragment extends BaseFragment {

    public static final String TAG = "file_picker";
    private FilesAdapter mAdapter;
    private File mCurrentFolder;
    private HashSet<File> mSelectedFolders;
    private FileChipsView mChipsView;

    public static FilePickerFragment newInstance() {
        FilePickerFragment f = new FilePickerFragment();
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

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Choose folders");

        mChipsView = (FileChipsView) view.findViewById(R.id.chipsView);

        final Button doneButton = (Button) view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Default","DONE=" + mSelectedFolders.size());
                final Iterator<File> iter = mSelectedFolders.iterator();
                while(iter.hasNext()) {
                    File file = iter.next();
                    Log.d("Default","FILE=" + file.getAbsolutePath());
                }
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
        final File[] files = file.listFiles(mFilter);
        Arrays.sort(files, ALPHABETICAL_ORDER);
        return files;
    }

    private void navigateIn(File file) {
        File[] files = listFolders(file);
        if(files == null || files.length <=0) {
            Toast.makeText(getActivity(), "Folder " + file.getName() + " has no subfolders", Toast.LENGTH_SHORT).show();
            return;
        }

        mAdapter.setFiles(listFolders(file));
        mAdapter.notifyDataSetChanged();

        mCurrentFolder = file;
    }

    @Override
    public boolean onBackPressed() {
        Log.d("Default", "onBackPress");
        if(mCurrentFolder != null) {
            final File parentFolder = mCurrentFolder.getParentFile();
            if(parentFolder != null) {
                navigateIn(parentFolder);
                return true;
            }
        }
        return false;
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
            Log.d("Default","onChecked pos=" + position + " checked=" + checked);
            final File file = (File) mAdapter.getItem(position);
            if(checked) {
                mSelectedFolders.add(file);
                mChipsView.add(file);
            } else {
                mSelectedFolders.remove(file);
            }
        }
    };

    private static final Comparator<File> ALPHABETICAL_ORDER = new Comparator<File>() {

        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
        }
    };
}
