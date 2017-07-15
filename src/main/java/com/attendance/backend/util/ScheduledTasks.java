package com.attendance.backend.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.ui.exportview.ExportViewController;

//@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private ExportViewController exportViewController;
    
    //@Scheduled(fixedRate = 60000)
    public void exportViewScheduled() throws Exception {
        log.info("exportViewScheduled {}", dateFormat.format(new Date()));
    	exportViewController.exportView(null);
    }
}