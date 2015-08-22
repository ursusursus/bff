package sk.ursus.bigfilesfinder.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import sk.ursus.bigfilesfinder.FinderService;
import sk.ursus.bigfilesfinder.R;
import sk.ursus.bigfilesfinder.model.FilePath;

public class MainActivity extends AppCompatActivity {

    public interface BackListener {
        boolean onBackPressed();
    }

    private ArrayList<BackListener> mBackListeners = new ArrayList<>();
    private ArrayList<FilePath> mSelectedFoldersList;
    private int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            swap(WelcomeFragment.newInstance(), WelcomeFragment.TAG);
        }
    }

    private void swap(BaseFragment f, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, f, tag)
                .commit();
    }

    public void onWelcomeFragmentFinished() {
        swap(FolderPickerFragment.newInstance(), FolderPickerFragment.TAG);
    }

    public void onFolderPickerFragmentFinished(HashSet<File> selectedFolders) {
        Log.d("Default", "DONE=" + selectedFolders.size());
        ArrayList<FilePath> filepaths = new ArrayList<>();
        final Iterator<File> iter = selectedFolders.iterator();
        while (iter.hasNext()) {
            File file = iter.next();
            filepaths.add(FilePath.fromFile(file));
            Log.d("Default", "FILE=" + file.getAbsolutePath());
        }

        mSelectedFoldersList = filepaths;
        swap(CountPickerFragment.newInstance(), CountPickerFragment.TAG);
    }

    public void onCountPickerFragmentFinished(int count) {
        mCount = count;
        swap(ResultsFragment.newInstance(), ResultsFragment.TAG);

        FinderService.launch(MainActivity.this, mCount, mSelectedFoldersList);
    }

    public void onResultsFragmentFinished() {
        mCount = 0;
        mSelectedFoldersList.clear();
        swap(WelcomeFragment.newInstance(), WelcomeFragment.TAG);
    }

    @Override
    public void onBackPressed() {
        boolean handled = false;
        for (int i = 0; i < mBackListeners.size(); i++) {
            if (mBackListeners.get(i).onBackPressed()) {
                handled = true;
                break;
            }
        }
        if (!handled) {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBackListeners.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            foobar();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void registerBackListener(BackListener listener) {
        mBackListeners.add(listener);
    }

    public void unregisterBackListener(BackListener listener) {
        mBackListeners.remove(listener);
    }

    private void foobar() {
        int countOfLargest = 10;
        // potom este check na null fily bude treba
        ArrayList<FilePath> folders = new ArrayList<>();
        folders.add(FilePath.fromFile(Environment.getExternalStorageDirectory()));
        folders.add(FilePath.fromFile(Environment.getExternalStorageDirectory()));
        folders.add(FilePath.fromFile(Environment.getExternalStorageDirectory()));
        folders.add(FilePath.fromFile(Environment.getDataDirectory()));

        FinderService.launch(this, countOfLargest, folders);
    }
}
