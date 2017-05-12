package com.attendance.ui.exportview;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.attendance.backend.model.OutputView;
import com.attendance.backend.repository.DatabaseSchemaRepository;
import com.attendance.backend.repository.DropboxRepository;
import com.attendance.backend.repository.OutputViewRepository;

@Controller
public class ExportViewController {

	@Autowired
	private OutputViewRepository outputViewRepository;

	@Autowired
	DatabaseSchemaRepository databaseSchemaRepository;

	@Autowired
	DropboxRepository dropboxRepository;

	@RequestMapping("/export-view")
	@ResponseBody
	public String exportView(String name) throws Exception {
		Iterable<OutputView> outputViews = null;
		if(name == null)
			outputViews = outputViewRepository.findAll();
		else
			outputViews = outputViewRepository.findByName(name);

		for(OutputView outputView : outputViews){
			outputView.setHeaderColumns(databaseSchemaRepository.getViewHeaderColumns(outputView.getName()));
			outputView.setDbData(databaseSchemaRepository.getViewRows(outputView.getName()));
			
			dropboxRepository.uploadOutputView(outputView);

			outputView.setExecutionTime(new Timestamp(System.currentTimeMillis()));
			outputView.setExecutionStatus(outputView.getDbData().size() + " Records");
			outputViewRepository.save(outputView);
		}
		
		return this.exportViewStatus(name);
	}
	
	@RequestMapping("/export-view-status")
	@ResponseBody
	public String exportViewStatus(String name){
		Iterable<OutputView> outputViews = null;
		if(name == null)
			outputViews = outputViewRepository.findAll();
		else
			outputViews = outputViewRepository.findByName(name);

		String outputViewStatus = "<table>";
		int total = 0;
		for(OutputView v : outputViews){
			outputViewStatus += "<tr>";
			outputViewStatus += "<td>" + v.getName() + "</td>";
			outputViewStatus += "<td>" + v.getExecutionTime() + "</td>";
			outputViewStatus += "<td>" + v.getExecutionStatus() + "</td>";
			outputViewStatus += "</tr>";
			total++;
		}
		outputViewStatus += "<tr><td collspan = 3>" + total + " Views" + (name != null ? ", filter = " + name : "") + "</td></tr>";
		
		outputViewStatus += "</table>";
		
		return outputViewStatus;
	}
}