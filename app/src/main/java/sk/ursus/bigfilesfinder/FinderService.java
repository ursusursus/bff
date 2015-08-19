package sk.ursus.bigfilesfinder;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ursusursus on 17.8.2015.
 */
public class FinderService extends Service {

    private static final String TAG = "Default";

    private static final String ACTION_FIND_LARGEST_FILES = "sk.ursus.bigfilesfinder.ACTION_FIND_LARGEST_FILES";
    private static final String EXTRA_LARGEST_COUNT = "largest_count";
    private static final String EXTRA_FOLDERS = "folder_names";

    private NotificationManager mNotificationManager;
    private Set<FindLargestFilesTask> mTasksInFlight;
    private FilesPriorityQueueWrapper mFilesQueueWrapper;
    private long mStart;


    public static void launch(Context context, int countOfLargest, ArrayList<FileWrapper> folders) {
        final Intent intent = new Intent(context, FinderService.class)
                .setAction(ACTION_FIND_LARGEST_FILES)
                .putExtra(EXTRA_LARGEST_COUNT, countOfLargest)
                .putExtra(EXTRA_FOLDERS, folders);

        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Default", "onStartCommand");
        if (ACTION_FIND_LARGEST_FILES.equals(intent.getAction())) {
            findLargestFiles(intent);
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FinderService # onCreate");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "FinderService # onDestroy");
    }

    private void findLargestFiles(Intent intent) {
        if(mTasksInFlight != null && !mTasksInFlight.isEmpty()) {
            // Some tasks are still running, ignoring...
            return;
        }

        final int countOfLargest = intent.getIntExtra(EXTRA_LARGEST_COUNT, -1);
        final ArrayList<FileWrapper> folders = intent.getParcelableArrayListExtra(EXTRA_FOLDERS);
        if (countOfLargest == -1 || folders == null || folders.size() <= 0) {
            // error: invalid input
            return;
        }

        doFindLargestFiles(countOfLargest, folders);
    }

    private void doFindLargestFiles(int countOfLargest, ArrayList<FileWrapper> folders) {
        NotificationUtils.showProgressNotif(this, mNotificationManager);

        mTasksInFlight = Collections.synchronizedSet(new HashSet<FindLargestFilesTask>());
        mFilesQueueWrapper = new FilesPriorityQueueWrapper(countOfLargest);

        Log.d("Default", "START");
        mStart = System.currentTimeMillis();

        for (int i = 0; i < folders.size(); i++) {
            final File folder = folders.get(i).toFile();

            if (folder.exists() && folder.isDirectory()) {
                final FindLargestFilesTask task = new FindLargestFilesTask();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, folder);
                mTasksInFlight.add(task);
            }
        }

        if (mTasksInFlight.isEmpty()) {
            // error: no valid folders
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleTaskFinished(FindLargestFilesTask task) {
        mTasksInFlight.remove(task);
        if (mTasksInFlight.isEmpty()) {
            handleAllTasksFinished();
        }
    }

    private void handleAllTasksFinished() {
        final ArrayList<File> largestFiles = mFilesQueueWrapper.toList();
//        Log.d("Default", "SIZE=" + largestFiles.size());
//        for (File file : largestFiles) {
//            Log.d("Default", "F=" + file.getAbsolutePath() + " SIZE=" + bytesToMegabytes(file.length()));
//        }

        long time = System.currentTimeMillis() - mStart;
        Log.d("Default", "END      TOOK=" + time + "ms");

        // Broadcast
        BroadcastUtils.sendSearchFinished(this, largestFiles);

        NotificationUtils.cancelProgressNotif(mNotificationManager);
        NotificationUtils.showFinishedNotif(FinderService.this, mNotificationManager);
    }

    private long bytesToMegabytes(long bytes) {
        return bytes / (1024 * 1024);
    }

    private class FindLargestFilesTask extends AsyncTask<File, Void, List<File>> {

        @Override
        protected List<File> doInBackground(File... params) {
            Log.d("Default", "LAUNCH");
            if (params.length >= 0) {
                final File folder = params[0];
                if (folder != null && folder.exists() && folder.isDirectory()) {
                    collectFiles(folder, mFilesQueueWrapper);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            super.onPostExecute(files);
            handleTaskFinished(this);
        }

        private void collectFiles(File folder, FilesPriorityQueueWrapper filesQueueWrapper) {
            final File[] files = folder.listFiles();
            if (files == null || files.length <= 0) {
                return;
            }

            for (int i = 0; i < files.length; i++) {
                final File file = files[i];
                if (file.isDirectory()) {
                    collectFiles(file, filesQueueWrapper);
                } else {
                    filesQueueWrapper.add(file);
                }
            }
        }

    }

}
