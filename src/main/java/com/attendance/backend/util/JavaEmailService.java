package com.attendance.backend.util;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Reference: http://www.baeldung.com/spring-email
 */

@Component
public class JavaEmailService implements EmailService {

	@Autowired
	public JavaMailSender emailSender;

    @Override
    public void sendText(String from, String to, String subject, String body) throws Exception {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		emailSender.send(message);
	}

    @Override
	public void sendHTML(String from, String to, String subject, String body) throws Exception {

		MimeMessage message = emailSender.createMimeMessage();
		
		// use the true flag to indicate you need a multipart message
		MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
		messageHelper.setFrom(from);
		messageHelper.setTo(to);
		messageHelper.setSubject(subject);
		// use the true flag to indicate the text included is HTML
		messageHelper.setText(body, true);		
		emailSender.send(message);
	}
	
}