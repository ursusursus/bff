package sk.ursus.bigfilesfinder.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FilesBoundedPriorityQueue extends PriorityQueue<File> {

    private final Object sLock = new Object();
    private final int mBoundedSize;

    public FilesBoundedPriorityQueue(int boundedSize) {
        super(boundedSize, FILE_SIZE_ORDER_ASCENDING);
        mBoundedSize = boundedSize;
    }

    @Override
    public boolean add(File file) {
        synchronized (sLock) {
            if (!contains(file)) {
                if (size() < mBoundedSize) {
                    super.add(file);
                    return true;

                } else {
                    if (file.length() >= peek().length()) {
                        // If input item is bigger or equal than tip,
                        // add it to queue, and release the smallest one (tip).
                        // That way we will end up with N largest items ever
                        // added to queue.
                        // It should be a big faster and much more
                        // memory efficient
                        super.add(file);
                        poll();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<File> toList() {
        final ArrayList<File> list = new ArrayList<>(mBoundedSize);
        for (int i = 0; i < mBoundedSize; i++) {
            final File file = poll();
            if (file == null) {
                break;
            } else {
                list.add(file);
            }
        }
        // Tip is always the smallest one, so needs to be reversed
        Collections.reverse(list);
        return list;
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
