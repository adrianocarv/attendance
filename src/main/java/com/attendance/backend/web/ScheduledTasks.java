package com.attendance.backend.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private ExportViewController exportViewController;
    
    @Scheduled(fixedRate = 60000)
    public void exportViewScheduled() throws Exception {
        log.info("exportViewScheduled {}", dateFormat.format(new Date()));
    	exportViewController.exportView(null);
    }
}