package com.example.chatroomsmall.fcm;

import androidx.annotation.NonNull;

import com.example.chatroomsmall.model.NotificationModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification() != null) {
            handleNotification(message.getNotification());
        } else if (message.getData() != null) {
            // loại data message đc lưu trữ dưới dạng key- value
            // - nên dùng map để lưu trữ
            Map<String, String> data = message.getData();
            handleDataMessage(data);
        }
    }

    private void handleNotification(RemoteMessage.Notification notification) {
        String title = notification.getTitle();
        String message = notification.getBody();

        NotificationModel notificationModel = new NotificationModel(title, message);

        NotificationUtils notificationUtils = new NotificationUtils(this);
        notificationUtils.displayNotification(notificationModel);



    }

    private void handleDataMessage(Map<String, String> data) {
    }
}
