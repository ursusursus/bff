package sk.ursus.bigfilesfinder;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
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

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FilePickerFragment extends BaseFragment {

    public static final String TAG = "file_picker";
    private FilesAdapter mAdapter;
    private File mCurrentFolder;

    public static FilePickerFragment newInstance() {
        FilePickerFragment f = new FilePickerFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filepicker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new FilesAdapter(getActivity());
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
        mAdapter.setFiles(listFolders(file));
        mAdapter.notifyDataSetChanged();

        if (mAdapter.isEmpty()) {
            Toast.makeText(getActivity(), "Folder " + file.getName() + " has no subfolders", Toast.LENGTH_SHORT).show();
        }

        mCurrentFolder = file;
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

    private static final Comparator<File> ALPHABETICAL_ORDER = new Comparator<File>() {

        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
        }
    };
}
