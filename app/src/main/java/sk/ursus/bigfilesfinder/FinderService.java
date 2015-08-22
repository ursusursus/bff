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

import sk.ursus.bigfilesfinder.model.FilePath;
import sk.ursus.bigfilesfinder.util.BroadcastUtils;
import sk.ursus.bigfilesfinder.util.FilesBoundedPriorityQueue;
import sk.ursus.bigfilesfinder.util.NotificationUtils;

/**
 * Created by ursusursus on 17.8.2015.
 */
public class FinderService extends Service {

    private static final String TAG = "Default";

    private static final String ACTION_FIND_LARGEST_FILES = "sk.ursus.bigfilesfinder.ACTION_FIND_LARGEST_FILES";
    private static final String EXTRA_LARGEST_COUNT = "largest_count";
    private static final String EXTRA_FOLDERS = "folder_names";

    public static final int ERROR_INVALID_INPUT = 123;
    public static final int ERROR_NO_VALID_FOLDERS = 124;
    public static final int ERROR_TASKS_RUNNING = 125;

    private NotificationManager mNotificationManager;
    private Set<FindLargestFilesTask> mTasksInFlight;
    private FilesBoundedPriorityQueue mFilesPriorityQueue;
    private long mStart;


    public static void launch(Context context, int countOfLargest, ArrayList<FilePath> folders) {
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
        // Check for tasks still running
        if(mTasksInFlight != null && !mTasksInFlight.isEmpty()) {
            BroadcastUtils.sendSearchError(this, ERROR_TASKS_RUNNING);
            return;
        }

        // Check for invalid input
        final int countOfLargest = intent.getIntExtra(EXTRA_LARGEST_COUNT, -1);
        final ArrayList<FilePath> folders = intent.getParcelableArrayListExtra(EXTRA_FOLDERS);
        if (countOfLargest == -1 || folders == null || folders.size() <= 0) {
            BroadcastUtils.sendSearchError(this, ERROR_INVALID_INPUT);
            return;
        }

        optimize(folders);
        // doFindLargestFiles(countOfLargest, folders);
    }

    private void optimize(ArrayList<FilePath> folders) {
//        for(int i = 0; i < folders.size(); i++) {
//            for(int j = 0; j < folders.size(); j++) {
//                if(folders.get(i).folders.get(j)
//            }
//        }

//        ArrayList<FilePath> copy = new ArrayList<FilePath>(folders);
//        for (int i = 0; i < copy.size(); i++) {
//             if(i % 2 == 0) {
//                folders.remove(copy.get(i));
//             }
//        }

        final ArrayList<FilePath> toRemove = new ArrayList<>();
        for (int i = 0; i < folders.size(); i++) {
            for (int j = 0; j < folders.size(); j++) {
                if(folders.get(j).getPath().startsWith(folders.get(i).getPath())) {
                    toRemove.add(folders.get(j));
                    break;
                }
            }
        }

        HashSet<FilePath> set = new HashSet<FilePath>();
        set.contains()

        for (FilePath folder : toRemove) {
            Log.d("Default", "FOLDER=" + folder.getPath());
        }

        // two nested loops
        // if a.startsWith(me)
        //    list.remove(a)
    }

    private void doFindLargestFiles(int countOfLargest, ArrayList<FilePath> folders) {
        NotificationUtils.cancelFinishedNotif(mNotificationManager);
        NotificationUtils.showProgressNotif(this, mNotificationManager);

        mTasksInFlight = Collections.synchronizedSet(new HashSet<FindLargestFilesTask>());
        mFilesPriorityQueue = new FilesBoundedPriorityQueue(countOfLargest);

        Log.d("Default", "START");
        mStart = System.currentTimeMillis();

        // Launch search async task per folder
        for (int i = 0; i < folders.size(); i++) {
            final File folder = folders.get(i).toFile();

            if (folder.exists() && folder.isDirectory()) {
                final FindLargestFilesTask task = new FindLargestFilesTask();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, folder);
                mTasksInFlight.add(task);
            }
        }

        if (mTasksInFlight.isEmpty()) {
            BroadcastUtils.sendSearchError(this, ERROR_NO_VALID_FOLDERS);
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
        final ArrayList<File> largestFiles = mFilesPriorityQueue.toList();

        long time = System.currentTimeMillis() - mStart;
        Log.d("Default", "END      TOOK=" + time + "ms");

        BroadcastUtils.sendSearchFinished(this, largestFiles);
        NotificationUtils.cancelProgressNotif(mNotificationManager);
        NotificationUtils.showFinishedNotif(FinderService.this, mNotificationManager);

        stopSelf();
    }

    private class FindLargestFilesTask extends AsyncTask<File, Void, List<File>> {

        @Override
        protected List<File> doInBackground(File... params) {
            Log.d("Default", "LAUNCH");
            if (params.length >= 0) {
                final File folder = params[0];
                if (folder != null && folder.exists() && folder.isDirectory()) {
                    // Dive in and go through all the files in given
                    // subfolder tree
                    collectFiles(folder, mFilesPriorityQueue);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            super.onPostExecute(files);
            handleTaskFinished(this);
        }

        private void collectFiles(File folder, FilesBoundedPriorityQueue filesQueueWrapper) {
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
