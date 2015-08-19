package sk.ursus.bigfilesfinder;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ursusursus on 17.8.2015.
 */
public class FinderService extends Service {

    private HashSet<FindTask> mTasksInFlight;
    private ArrayList<File> mAllFiles;
    private long mStart;

    public interface OnSearchFinishedListener {
        void onFindFilesTaskFinished(FindTask task, List<File> largestFiles);
    }

    private static final String TAG = "Default";

    private static final String ACTION_FIND_LARGEST_FILES = "sk.ursus.bigfilesfinder.ACTION_FIND_LARGEST_FILES";
    private static final String EXTRA_LARGEST_COUNT = "largest_count";
    private static final String EXTRA_FOLDER_NAMES = "folder_names";

    private NotificationManager mNotificationManager;

    public static void launch(Context context, int countOfLargest, String[] folderNames) {
        final Intent intent = new Intent(context, FinderService.class)
                .setAction(ACTION_FIND_LARGEST_FILES)
                .putExtra(EXTRA_LARGEST_COUNT, countOfLargest)
                .putExtra(EXTRA_FOLDER_NAMES, folderNames);

        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // co ked naspamuje sa launch() ?
        if (ACTION_FIND_LARGEST_FILES.equals(intent.getAction())) {
            findLargestFiles(intent);
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FinderService # onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "FinderService # onDestroy");
    }

    private void findLargestFiles(Intent intent) {
        final int countOfLargest = intent.getIntExtra(EXTRA_LARGEST_COUNT, -1);
//        final String[] foldersNames = intent.getStringArrayExtra(EXTRA_FOLDER_NAMES);
//
//        if (countOfLargest == -1 || foldersNames == null || foldersNames.length <= 0) {
//            return;
//        }
//
//        for (int i = 0; i < foldersNames.length; i++) {
//            final String folderName = foldersNames[i];
//            if (!TextUtils.isEmpty(folderName)) {
//                final File folderFile = new File(folderName);
//                new FindTask().execute();
//                if (folderFile.exists() && folderFile.isDirectory()) {
//                }
//            }
//        }
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationUtils.showProgressNotif(this, mNotificationManager);

        final File[] folders = new File[]{
                Environment.getRootDirectory(),
                Environment.getExternalStorageDirectory()
        };

        if (countOfLargest == -1 || folders == null || folders.length <= 0) {
            // dafuq
            return;
        }

        mTasksInFlight = new HashSet<>();
        mAllFiles = new ArrayList<>();

        Log.d("Default", "START");
        mStart = System.currentTimeMillis();
        for (int i = 0; i < folders.length; i++) {
            final File folder = folders[i];
            if (folder.exists() && folder.isDirectory()) {
                final FindTask task = new FindTask(folder, countOfLargest, mListener);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                mTasksInFlight.add(task);
            }
        }

        if (mTasksInFlight.isEmpty()) {
            // dafuq
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class FindTask extends AsyncTask<Void, Void, List<File>> {

        private final File mFolder;
        private final int mCountOfLargest;
        private final OnSearchFinishedListener mListener;

        public FindTask(File folder, int countOfLargest, OnSearchFinishedListener listener) {
            mFolder = folder;
            mCountOfLargest = countOfLargest;
            mListener = listener;
        }

        @Override
        protected List<File> doInBackground(Void... params) {
            Log.d("Default", "LAUNCH");
            if (mFolder == null || !mFolder.exists() || !mFolder.isDirectory()) {
                return null;
            }

            final ArrayList<File> filesList = new ArrayList<File>();

//            Log.i(TAG, "BEGIN");
            collect(mFolder, filesList);
//            List<File> sublist = sortAndSlice(filesList);
//            Log.i(TAG, "END");
//
//            long end = System.currentTimeMillis();
//            Log.i(TAG, "took " + (end - start) + "ms");
//            return sublist;
            return filesList;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            super.onPostExecute(files);
            mListener.onFindFilesTaskFinished(this, files);
        }

        private void collect(File folder, ArrayList<File> filesList) {
            final File[] files = folder.listFiles();
            if (files == null || files.length <= 0) {
                return;
            }

            for (int i = 0; i < files.length; i++) {
                final File file = files[i];
                if (file.isDirectory()) {
                    // Log.d(TAG, file.getAbsolutePath() + "/");
                    collect(file, filesList);
                } else {
                    // Log.d(TAG, file.getAbsolutePath());
                    filesList.add(file);
                }
            }
        }

        private List<File> sortAndSlice(ArrayList<File> filesList) {
            Collections.sort(filesList, FILE_SIZE_ORDER);
            // ceknut ci sublist z mensieho sa da
            return filesList.subList(0, mCountOfLargest + 1);
        }

    }

    private long bytesToMegabytes(long bytes) {
        return bytes / (1024 * 1024);
    }

    private final OnSearchFinishedListener mListener = new OnSearchFinishedListener() {
        @Override
        public void onFindFilesTaskFinished(FindTask task, List<File> largestFiles) {
            handleTaskFinished(task, largestFiles);
        }
    };

    private void handleTaskFinished(FindTask task, List<File> largestFiles) {
        synchronized (this) {
            mAllFiles.addAll(largestFiles);
            Log.d("Default", "INTERIM SIZE=" + mAllFiles.size());
            mTasksInFlight.remove(task);
            if (mTasksInFlight.isEmpty()) {
                handleAllTasksFinished();
            }
        }
    }

    private void handleAllTasksFinished() {
        Collections.sort(mAllFiles, FILE_SIZE_ORDER);
        NotificationUtils.cancelProgressNotif(mNotificationManager);
        NotificationUtils.showFinishedNotif(FinderService.this, mNotificationManager);
        Log.d("Default", "END");
        long time = System.currentTimeMillis() - mStart;
        Log.d("Default", "TOOK=" + time + "ms");

//            Intent intent = new Intent(FinderService.this, MainActivity.class);
//            intent.putStringArrayListExtra()
    }

    private static final Comparator<File> FILE_SIZE_ORDER = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            if (f1.length() == f2.length()) {
                return 0;
            } else if (f1.length() < f2.length()) {
                return 1;
            } else {
                return -1;
            }
        }
    };

}
