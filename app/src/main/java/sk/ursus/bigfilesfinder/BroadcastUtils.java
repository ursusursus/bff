package sk.ursus.bigfilesfinder;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ursusursus on 17.8.2015.
 */
public class BroadcastUtils {

    public static final String ACTION_LARGEST_FILES_FOUND = "sk.ursus.bigfilesfinder.ACTION_LARGEST_FILES_FOUND";
    public static final String EXTRA_FILES = "files";

    public static void sendLargestFiles(Context context, ArrayList<File> largestFiles) {
//        final Intent intent = new Intent(ACTION_LARGEST_FILES_FOUND)
//                .put(EXTRA_FILES, largestFiles);
//
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
