package com.maven.Rapido.service;

import com.maven.Rapido.payload.request.notification.PushNotificationRequest;
import org.hibernate.sql.exec.ExecutionException;

public interface FCMService {
    void sendPushNotificationToToken(PushNotificationRequest request);
}
