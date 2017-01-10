package com.attendance.backend.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.attendance.backend.model.OutputView;
import com.attendance.backend.repository.DropboxRepository;
import com.attendance.backend.repository.OutputViewRepository;

@Controller
public class ExportViewController {

	@Autowired
	private OutputViewRepository outputViewRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	DropboxRepository dropboxRepository;

	@Value("${dropbox.accessToken}")
	private String ACCESS_TOKEN;
	
	@RequestMapping("/export-view")
	@ResponseBody
	public String exportView(String name) throws Exception {
		
		System.out.println("\n\n============================================================================================================");
		System.out.println("================== ACCESS_TOKEN: " + ACCESS_TOKEN);
		
		Iterable<OutputView> outputViews = null;
		if(name == null)
			outputViews = outputViewRepository.findAll();
		else
			outputViews = outputViewRepository.findByName(name);

		for(OutputView outputView : outputViews){
			outputView.setHeaderColumns(this.getViewHeaderColumns(outputView.getName()));
			outputView.setDbData(this.getViewRows(outputView.getName()));
			
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
	
	private String[] getViewHeaderColumns(String name) {

		List<Map<String, Object>> schema = jdbcTemplate.queryForList("SELECT * FROM information_schema.columns WHERE table_name = '" + name + "'");
		List<String> columns = new ArrayList<String>();
		for (Map<String, Object> m : schema)
			columns.add(m.get("COLUMN_NAME") + "");

		return columns.toArray(new String[0]);
	}

	private List<Map<String, Object>> getViewRows(String viewName){

		List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from " + viewName);

		return rows;
	}
}