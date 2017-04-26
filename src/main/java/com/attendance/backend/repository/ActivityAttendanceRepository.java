package com.attendance.backend.repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.ActivityAttendance;
import com.attendance.backend.model.Person;

@Repository
public class ActivityAttendanceRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<ActivityAttendance> findByActivity(Activity activity) {

		String sql = "";
		sql += "\n select activity_id, date, count(*) as totalPresent";
		sql += "\n from attendance";
		sql += "\n where activity_id = " + activity.getId();
		sql += "\n group by activity_id, date";
		sql += "\n order by date desc";
		
		List<ActivityAttendance> list = new ArrayList<ActivityAttendance>();
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> m : result){
			Long id = (Long) m.get("activity_id");
			Date date = (Date) m.get("date");
			Long totalPresent = (Long) m.get("totalPresent");
			list.add(new ActivityAttendance(id, date, totalPresent));
		}

		return list;
	}

	public List<Person> findByLastAttendances(Activity activity) {

		String sql = "";
		sql += "\n select distinct person_id, name";
		sql += "\n from   attendance a,";
		sql += "\n        person p,";
		sql += "\n        (";
		sql += "\n          select date";
		sql += "\n          from   attendance";
		sql += "\n          where  activity_id = " + activity.getId();
		sql += "\n          group by activity_id, date";
		sql += "\n          order by date desc";
		sql += "\n          limit 6";
		sql += "\n        ) as d";
		sql += "\n where  a.person_id = p.id";
		sql += "\n and    a.date = d.date";
		sql += "\n and    activity_id = " + activity.getId();
		
		List<Person> list = new ArrayList<Person>();
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> m : result){
			Long id = (Long) m.get("person_id");
			String name = (String) m.get("name");
			list.add(new Person(id, name));
		}

		return list;
	}

}
