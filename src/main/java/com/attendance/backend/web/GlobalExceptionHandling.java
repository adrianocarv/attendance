package com.attendance.backend.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.attendance.backend.Application;
import com.attendance.backend.model.ExceptionJSONInfo;

@ControllerAdvice
public class GlobalExceptionHandling {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public ExceptionJSONInfo handleError(HttpServletRequest req, Exception ex) {
		log.error("Request: " + req.getRequestURL() + " raised " + ex);

		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(req.getRequestURL().toString());
		response.setMessage(ex.getMessage());
		
		return response;
	}
}