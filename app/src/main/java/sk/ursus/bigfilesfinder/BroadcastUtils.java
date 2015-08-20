package sk.ursus.bigfilesfinder;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ursusursus on 17.8.2015.
 */
public class BroadcastUtils {

    public static final String ACTION_SEARCH_STARTED = "sk.ursus.bigfilesfinder.ACTION_SEARCH_STARTED";
    public static final String ACTION_SEARCH_FINISHED = "sk.ursus.bigfilesfinder.ACTION_SEARCH_FINISHED";
    public static final String ACTION_SEARCH_ERROR = "sk.ursus.bigfilesfinder.ACTION_SEARCH_ERROR";

    public static final String EXTRA_FILES = "files";
    public static final String EXTRA_ERROR = "error";

    public static void sendSearchFinished(Context context, ArrayList<File> files) {
        final ArrayList<FilePath> filePaths = new ArrayList<FilePath>(files.size());
        for (int i = 0; i < files.size(); i++) {
             filePaths.add(FilePath.fromFile(files.get(i)));

        }
        final Intent intent = new Intent(ACTION_SEARCH_FINISHED)
                .putParcelableArrayListExtra(EXTRA_FILES, filePaths);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendSearchStarted(Context context) {
        final Intent intent = new Intent(ACTION_SEARCH_STARTED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendSearchError(Context context, int errorCode) {
        final Intent intent = new Intent(ACTION_SEARCH_ERROR)
                .putExtra(EXTRA_ERROR, errorCode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
