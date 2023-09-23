package com.example.chatroomsmall.fcm;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.chatroomsmall.MainActivity;
import com.example.chatroomsmall.R;
import com.example.chatroomsmall.model.NotificationModel;

import java.util.Random;

public class NotificationUtils {

    private static final CharSequence CHANEL_NAME = "NOTIFICATION";
    private static final String CHANEL_ID = "21" ;
    private Context context;

    public NotificationUtils(Context context) {
        this.context = context;
    }


    public void displayNotification(NotificationModel notificationModel) {

        String title = notificationModel.getTitle();
        String message = notificationModel.getMessage();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(
                context, 0, new Intent[]{intent}, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT
        );
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANEL_ID);
        Notification notification =  builder.setAutoCancel(true)
                .setTicker(title)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                //.setSound(Uri.parse(String.valueOf(NotificationCompat.DEFAULT_SOUND)))
                 .setSound(defaultSoundUri)

                .build();

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(title);
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.setSummaryText(title);
        builder.setStyle(bigTextStyle);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(new Random().nextInt(), notification);

    }
}
