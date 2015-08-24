package sk.ursus.bigfilesfinder;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    public static final int ERROR_EMPTY_RESULTS = 126;

    private NotificationManager mNotificationManager;
    private Set<FindLargestFilesTask> mTasksInFlight;
    private FilesBoundedPriorityQueue mFilesPriorityQueue;


    public static void launch(Context context, int countOfLargest, ArrayList<String> folders) {
        final Intent intent = new Intent(context, FinderService.class)
                .setAction(ACTION_FIND_LARGEST_FILES)
                .putExtra(EXTRA_LARGEST_COUNT, countOfLargest)
                .putExtra(EXTRA_FOLDERS, folders);

        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_FIND_LARGEST_FILES.equals(intent.getAction())) {
            findLargestFiles(intent);
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void findLargestFiles(Intent intent) {
        // Check for tasks still running
        if (mTasksInFlight != null && !mTasksInFlight.isEmpty()) {
            BroadcastUtils.sendSearchError(this, ERROR_TASKS_RUNNING);
            return;
        }

        // Check for invalid input
        final int countOfLargest = intent.getIntExtra(EXTRA_LARGEST_COUNT, -1);
        ArrayList<String> folderPaths = intent.getStringArrayListExtra(EXTRA_FOLDERS);
        if (countOfLargest == -1 || folderPaths == null || folderPaths.size() <= 0) {
            BroadcastUtils.sendSearchError(this, ERROR_INVALID_INPUT);
            return;
        }

        final ArrayList<File> folders = removeDuplicatesAndSelfSubdirectories2(folderPaths);
        doFindLargestFiles(countOfLargest, folders);
    }

    /**
     * Because it doesn't make sense to search a directory
     * if we will search it's superdirectory too
     */
    private ArrayList<File> removeDuplicatesAndSelfSubdirectories2(ArrayList<String> paths) {
        // Remove duplicates and non-sense
        final HashSet<File> set = new HashSet<>();
        for (int i = 0; i < paths.size(); i++) {
            final File folder = new File(paths.get(i));
            if (folder.exists() && folder.isDirectory()) {
                set.add(folder);
            }
        }
        final ArrayList<File> folders = new ArrayList<>(set);
        set.clear();

        // Collect all self-subdirectories
        final String trailingSlash = File.separator;
        for (int i = 0; i < folders.size(); i++) {
            // File.getCanonicalPath() doesn't add a trailing slash
            // on a directory...and we need that, because
            // "foo/bar" could then appear as superdirectory of "/foo/barred"

            // BTW getCanonicalPath resolves all symlinks, ".", etc. which needed
            // when comparing paths, but is more expensive
            try {
                final String fi = folders.get(i).getCanonicalPath() + trailingSlash;
                for (int j = 0; j < folders.size(); j++) {
                    if (j == i) {
                        continue;
                    }
                    try {
                        final String fj = folders.get(j).getCanonicalPath() + trailingSlash;
                        if (fj.startsWith(fi)) {
                            set.add(folders.get(j));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        folders.removeAll(set);
        return folders;
    }

    private void doFindLargestFiles(int countOfLargest, ArrayList<File> folders) {
        BroadcastUtils.sendSearchStarted(this);
        NotificationUtils.cancelFinishedNotif(mNotificationManager);
        NotificationUtils.showProgressNotif(this, mNotificationManager);

        mTasksInFlight = Collections.synchronizedSet(new HashSet<FindLargestFilesTask>());
        mFilesPriorityQueue = new FilesBoundedPriorityQueue(countOfLargest);

        // Launch search async task per folder
        for (int i = 0; i < folders.size(); i++) {
            final FindLargestFilesTask task = new FindLargestFilesTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, folders.get(i));
            mTasksInFlight.add(task);
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

        BroadcastUtils.sendSearchFinished(this, largestFiles);
        NotificationUtils.cancelProgressNotif(mNotificationManager);
        NotificationUtils.showFinishedNotif(FinderService.this, mNotificationManager);

        stopSelf();
    }

    private class FindLargestFilesTask extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... params) {
            if (params.length > 0) {
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
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
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
