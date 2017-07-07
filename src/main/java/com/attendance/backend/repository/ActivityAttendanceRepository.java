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
		sql += "\n select activity_id, date, title, count(*) as totalPresent";
		sql += "\n from attendance";
		sql += "\n where activity_id = " + activity.getId();
		sql += "\n group by activity_id, date, title";
		sql += "\n order by date desc";
		
		List<ActivityAttendance> list = new ArrayList<ActivityAttendance>();
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> m : result){
			Long id = (Long) m.get("activity_id");
			Date date = (Date) m.get("date");
			String title = (String) m.get("title");
			Long totalPresent = (Long) m.get("totalPresent");
			list.add(new ActivityAttendance(id, date, title, totalPresent));
		}

		return list;
	}

	public List<Person> findByLastAttendances(Activity activity) {

		Integer personSugestionByEvents = activity.getPersonSuggestionByEvents();
		Integer personSugestionByDays = activity.getPersonSuggestionByDays();

		boolean byEnvents = true;
		
		if(personSugestionByEvents == null && personSugestionByDays != null && personSugestionByDays > 0)
			byEnvents = false;
		
		if(personSugestionByEvents == null || personSugestionByDays <= 0)
			personSugestionByEvents = 6;
		
		String sqlByEnvents = "";
		sqlByEnvents += "\n          select date";
		sqlByEnvents += "\n          from   attendance";
		sqlByEnvents += "\n          where  activity_id = " + activity.getId();
		sqlByEnvents += "\n          group by activity_id, date";
		sqlByEnvents += "\n          order by date desc";
		sqlByEnvents += "\n          limit " + personSugestionByEvents;
		
		String sqlByDays = "";
		sqlByDays += "\n          select date";
		sqlByDays += "\n          from   attendance";
		sqlByDays += "\n          where  activity_id = " + activity.getId();
		sqlByDays += "\n          and    date >= DATE_SUB(NOW(), INTERVAL " + personSugestionByDays + " DAY)";

		String innerSelect = byEnvents ? sqlByEnvents : sqlByDays; 
		
		String sql = "";
		sql += "\n select distinct person_id, name";
		sql += "\n from   attendance a,";
		sql += "\n        person p,";
		sql += "\n        (";
		sql += innerSelect;
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
	
	public ActivityAttendance findOneLastByActivity(Activity activity) {

		String sql = "";
		sql += "\n select activity_id, date, title, count(*) as totalPresent";
		sql += "\n from attendance";
		sql += "\n where activity_id = " + activity.getId();
		sql += "\n and date = (select max(date) from attendance where activity_id = " + activity.getId() + ")";
		
		List<ActivityAttendance> list = new ArrayList<ActivityAttendance>();
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> m : result){
			Long id = (Long) m.get("activity_id");
			Date date = (Date) m.get("date");
			String title = (String) m.get("title");
			Long totalPresent = (Long) m.get("totalPresent");
			list.add(new ActivityAttendance(id, date, title, totalPresent));
		}

		return list.isEmpty() ? null : list.get(0);
	}

	

}
