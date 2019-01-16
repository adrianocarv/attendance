package com.attendance.backend.util;

import org.springframework.stereotype.Service;

/**
 * Reference: http://www.baeldung.com/spring-email
 */

@Service
public interface EmailService {

    void sendText(String from, String to, String subject, String body) throws Exception;

    void sendHTML(String from, String to, String subject, String body) throws Exception;
}