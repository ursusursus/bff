package sk.ursus.bigfilesfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public interface BackListener {

        boolean onBackPressed();

    }
    public ArrayList<BackListener> mBackListeners = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.ACTION_SEARCH_FINISHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);

        swap(WelcomeFragment.newInstance(), FilePickerFragment.TAG);
    }

    private void swap(BaseFragment f, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, f, tag)
                .commit();
    }

    public void onAnimationFinished() {

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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadcastUtils.ACTION_SEARCH_FINISHED.equals(intent.getAction())) {
                handleLargestFilesFound(intent);
            }
        }

        private void handleLargestFilesFound(Intent intent) {
            final TextView textView = (TextView) findViewById(R.id.textView);
            final ArrayList<FilePath> filePaths = intent.getParcelableArrayListExtra(BroadcastUtils.EXTRA_FILES);
            for (FilePath fw : filePaths) {
                final File f = fw.toFile();
                textView.append("F=" + f.getAbsolutePath() + " S=" + f.length() + "\n\n");
            }
        }
    };
}
