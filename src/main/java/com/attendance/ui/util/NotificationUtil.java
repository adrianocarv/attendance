package com.attendance.ui.util;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

public class NotificationUtil {

    public static void show(Notification notification) {
        // keep the notification visible a little while after moving the
        // mouse, or until clicked
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }
}