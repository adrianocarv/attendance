package com.attendance.backend.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseSchemaRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public String[] getViewHeaderColumns(String name) {

		List<Map<String, Object>> schema = jdbcTemplate.queryForList("SELECT * FROM information_schema.columns WHERE table_name = '" + name + "'");
		List<String> columns = new ArrayList<String>();
		for (Map<String, Object> m : schema)
			columns.add(m.get("COLUMN_NAME") + "");

		return columns.toArray(new String[0]);
	}

	public List<Map<String, Object>> getViewRows(String viewName){

		List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from " + viewName);

		return rows;
	}
}
