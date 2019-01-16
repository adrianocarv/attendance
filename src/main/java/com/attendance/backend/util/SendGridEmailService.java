package com.attendance.backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.attendance.ui.util.NotificationUtil;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.vaadin.ui.Notification;

@Component
public class SendGridEmailService implements EmailService {

	@Value("${sendgrid.api.key}")
	private String API_KEY;

	private SendGrid sendGridClient;
    
    @Override
    public void sendText(String from, String to, String subject, String body) throws Exception {
        Response response = sendEmail(from, to, subject, new Content("text/plain", body));
        System.out.println("Status Code: " + response.getStatusCode() + ", Body: " + response.getBody() + ", Headers: " + response.getHeaders());
    }
    
    @Override
    public void sendHTML(String from, String to, String subject, String body) throws Exception {
        Response response = sendEmail(from, to, subject, new Content("text/html", body));
        System.out.println("Status Code: " + response.getStatusCode() + ", Body: " + response.getBody() + ", Headers: " + response.getHeaders());
    }
    
	private Response sendEmail(String from, String to, String subject, Content content) {

		//Instanciate with API_KEY
		if(this.sendGridClient == null){
			this.sendGridClient = new SendGrid(API_KEY); 		
		}
		
		Mail mail = new Mail(new Email(from), subject, new Email(to), content);
        mail.setReplyTo(new Email(from));
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response = this.sendGridClient.api(request);
        } catch (Exception e) {
			e.printStackTrace();
			NotificationUtil.show(new Notification("Problema no envio do e-mail", e.getMessage() , Notification.Type.ERROR_MESSAGE));
        }
        return response;
    }

}