package com.maven.Rapido.serviceImp;

import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maven.Rapido.payload.request.notification.PushNotificationRequest;
import com.maven.Rapido.service.FCMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class FCMServiceImp implements FCMService {

    @Async
    @Override
    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            Message message = getPreconfiguredMessageToToken(request);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(message);
            String response = sendAndGetResponse(message);
            log.info("Sent message to token. Device token: " + request.getToken() + ", " + response+ " msg "+jsonOutput);
        } catch (Exception e) {
            log.error("Error sending FCM notification: ", e);
        }
    }


    private String sendAndGetResponse(Message message) {
        try {
            return FirebaseMessaging.getInstance().sendAsync(message).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            log.error("FCM send interrupted: ", e);
        } catch (Exception e) {
            log.error("FCM send failed during execution: ", e);
        }
        return "error"; // or return null / custom error string
    }



    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setTag(topic).build()).build();
    }
    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }
    private Message getPreconfiguredMessageToToken(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getToken())
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getMessage());
        ApnsConfig apnsConfig = getApnsConfig(request.getMessage());
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getMessage())
                .build();
        return Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(notification);
    }
}
