package com.attendance.backend.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.attendance.backend.util.AttendanceException;
import com.attendance.backend.util.FileFormatUtil;

@Entity
@Table(name = "output_view")
public class OutputView {

	@Id
	private String name;
	
	@Column(name="format_excel",nullable=false)
	private boolean formatExcel;

	@Column(name="format_csv",nullable=false)
	private boolean formatCSV;

	@Column(name="execution_time")
	private Timestamp executionTime;

	@Column(name="execution_status")
	private String executionStatus;

	@Transient
	private String[] headerColumns;

	@Transient
	private List<Map<String, Object>> dbData;
	
	//Business
	public Long getEntityId(){
		if (StringUtils.isEmpty(name))
			return null;
		
		if(!name.startsWith("vw_"))
			throw new AttendanceException("View name must starts with 'vw_'");
		
		Long entityId = new Long(StringUtils.substringBetween(name, "_", "_"));
		
		return entityId;
	}

	public Map<String, Object[]> getExcelData(){
		
		if(this.getHeaderColumns() == null || this.getDbData() == null)
			return null;
		
		Map<String, Object[]> excelData = new TreeMap<String, Object[]>();
		
		int line = 1;
		
		//header
		excelData.put((line++)+"", this.getHeaderColumns());

		//lines
		for (Map<String, Object> row : this.getDbData()) {
			List<String> lineData = new ArrayList<String>();
			for (String column : this.getHeaderColumns()) {
				String value = row.get(column) == null ? "" :  row.get(column)+"";
				lineData.add(value);
			}
			excelData.put((line++)+"", lineData.toArray(new String[0]));
		}
		
		return excelData;
	}
	
	public List<String[]> getCSVData(){
		
		if(this.getHeaderColumns() == null || this.getDbData() == null)
			return null;
		
		List<String[]> csvData = new ArrayList<String[]>();
		
		//header
		csvData.add(this.getHeaderColumns());

		//lines
		for (Map<String, Object> row : this.getDbData()) {
			List<String> lineData = new ArrayList<String>();
			for (String column : this.getHeaderColumns()) {
				String value = row.get(column) == null ? "" :  row.get(column)+"";
				lineData.add(value);
			}
			csvData.add(lineData.toArray(new String[0]));
		}
		
		return csvData;
	}
	
	public byte[] getExcelContentFile() throws Exception {
		return FileFormatUtil.getExcelContentFile(this);
	}
	
	public byte[] getCSVContentFile() throws Exception {
		return FileFormatUtil.getCSVContentFile(this);
	}

	//Getts
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFormatExcel() {
		return formatExcel;
	}

	public void setFormatExcel(boolean formatExcel) {
		this.formatExcel = formatExcel;
	}

	public boolean isFormatCSV() {
		return formatCSV;
	}

	public void setFormatCSV(boolean formatCSV) {
		this.formatCSV = formatCSV;
	}

	public Timestamp getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Timestamp executionTime) {
		this.executionTime = executionTime;
	}

	public String getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}

	public List<Map<String, Object>> getDbData() {
		return dbData;
	}

	public void setDbData(List<Map<String, Object>> dbData) {
		this.dbData = dbData;
	}

	public String[] getHeaderColumns() {
		return headerColumns;
	}

	public void setHeaderColumns(String[] headerColumns) {
		this.headerColumns = headerColumns;
	}
}
