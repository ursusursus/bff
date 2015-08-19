package sk.ursus.bigfilesfinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by ursusursus on 19.8.2015.
 *
 * Wrapper (so synchronization can be delegated to the queue)
 * around priority blocking queue that only allows N largest items inside.
 *
 */
public class FilesPriorityQueueWrapper {


    private final int mConstrainedSize;
    private final PriorityBlockingQueue<File> mFilesQueue;

    public FilesPriorityQueueWrapper(int constrainedSize) {
        if(constrainedSize < 0) {
            constrainedSize = 1;
        }
        mConstrainedSize = constrainedSize;
        mFilesQueue = new PriorityBlockingQueue<>(constrainedSize, FILE_SIZE_ORDER_ASCENDING);
    }
    public void add(File file) {
        if(mFilesQueue.size() < mConstrainedSize) {
            mFilesQueue.add(file);
        } else {
            if(file.length() >= mFilesQueue.peek().length()) {
                // If input item is bigger or equal than tip,
                // add it to queue, and release the smallest one (tip).
                // That way we will end up with N largest items ever
                // added to queue.
                // It should be a big faster and much more
                // memory efficient
                mFilesQueue.add(file);
                mFilesQueue.poll();
            }
        }
    }

    public ArrayList<File> toList() {
        final ArrayList<File> list = new ArrayList<>(mConstrainedSize);
        for (int i = 0; i < mConstrainedSize; i++) {
            final File file = mFilesQueue.poll();
            if(file == null) {
                break;
            } else {
                list.add(file);
            }
        }
        // Tip is always the smallest one, so needs to be reversed
        Collections.reverse(list);
        return list;
    }

    public int size() {
        return mFilesQueue.size();
    }

    private static final Comparator<File> FILE_SIZE_ORDER_ASCENDING = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            if (f1.length() == f2.length()) {
                return 0;
            } else if (f1.length() < f2.length()) {
                return -1;
            } else {
                return 1;
            }
        }
    };
}
