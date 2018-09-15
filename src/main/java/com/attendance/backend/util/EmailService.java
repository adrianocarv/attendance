package com.attendance.backend.util;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.attendance.backend.model.User;
import com.attendance.ui.about.AboutView;
import com.attendance.ui.util.NotificationUtil;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

/**
 * Reference: http://www.baeldung.com/spring-email
 */

@Component
public class EmailService {

	@Autowired
	public JavaMailSender emailSender;

	public void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Attendance: " + subject);
		message.setText(text);
		emailSender.send(message);
	}

	public String sendEmailVerification(User user) {

		String accessToken = System.currentTimeMillis() + "_" + user.getId(); 
		
		String urlVerification = Page.getCurrent().getLocation().toString();
		urlVerification = StringUtils.replace(urlVerification, Page.getCurrent().getUriFragment(), "");
		urlVerification += "!" + AboutView.VIEW_NAME + "/Activation/" + accessToken;
		
		String body = "Olá " + user.getName() + ",";
		body += "</br></br> Precisamos verificar seu endereço de e-mail da conta para concluir sua inscrição!";
		body += "</br></br> <a href="+urlVerification+">Verificar seu e-mail</a>";
		body += "</br></br> Divirta-se com o Attendance!";
		
		MimeMessage message = emailSender.createMimeMessage();
		
		try {
			// use the true flag to indicate you need a multipart message
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
			messageHelper.setTo(user.getEmail());
			messageHelper.setSubject("Attendance: Verifique seu endereço de e-mail");
			// use the true flag to indicate the text included is HTML
			messageHelper.setText(body, true);		
			emailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			NotificationUtil.show(new Notification("Problema no envio do e-mail", e.getMessage() , Notification.Type.ERROR_MESSAGE));
			return null;
		}
		
		String msg = "Enviamos um e-mail para <b>" + user.getEmail() + "</b>. Por favor acesse a caixa postal, encontre a mensagem enviada e clique no link de ativação.";
		NotificationUtil.show(new Notification("E-mail enviado", msg , Notification.Type.TRAY_NOTIFICATION, true));

		return accessToken;
	}
	
}