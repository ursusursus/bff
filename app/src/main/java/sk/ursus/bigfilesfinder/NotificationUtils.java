package sk.ursus.bigfilesfinder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by ursusursus on 17.8.2015.
 */
public class NotificationUtils {

    private static final int PROGRESS_NOTIF_ID = 1234;
    private static final int FINISHED_NOTIF_ID = 1235;
    public static final int PENDING_INTENT_ID = 0;

    public static void showProgressNotif(Context context, NotificationManager nm) {
        final Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.progress_notif_title))
                .setContentText(context.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(0, 0, true)
                .build();
        nm.notify(PROGRESS_NOTIF_ID, notification);
    }

    public static void cancelProgressNotif(NotificationManager nm) {
        nm.cancel(PROGRESS_NOTIF_ID);
    }

    public static void showFinishedNotif(Context context, NotificationManager nm) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, PENDING_INTENT_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        final Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentTitle(context.getString(R.string.finished_notif_title))
                .setContentText(context.getString(R.string.finished_notif_text))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        nm.notify(FINISHED_NOTIF_ID, notification);
    }
}
