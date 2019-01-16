package com.attendance.backend.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.attendance.backend.model.User;
import com.attendance.ui.about.AboutView;
import com.attendance.ui.util.NotificationUtil;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

@Component
public class AttendanceEmailService {

    @Autowired private EmailService sendGridEmailService;

	public String sendEmailVerification(User user) {
		
		String accessToken = System.currentTimeMillis() + "_" + user.getId(); 
		
		String urlVerification = Page.getCurrent().getLocation().toString();
		urlVerification = StringUtils.replace(urlVerification, Page.getCurrent().getUriFragment(), "");
		urlVerification += "!" + AboutView.VIEW_NAME + "/Activation/" + accessToken;
		
		String body = "Olá " + user.getName() + ",";
		body += "</br></br> Precisamos verificar seu endereço de e-mail da conta para concluir sua inscrição!";
		body += "</br></br> <a href="+urlVerification+">Verificar seu e-mail</a>";
		body += "</br></br> Divirta-se com o Attendance!";
		
		try {
			this.sendGridEmailService.sendHTML("contato@bumerangue.adm.br", user.getEmail(), "Attendance: Verifique seu endereço de e-mail", body);
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