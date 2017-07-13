package com.attendance.backend.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.attendance.backend.model.Center;
import com.attendance.backend.model.User;
import com.attendance.ui.authentication.CurrentUser;

@Repository
public class SharingUserRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Center> findCurrentUserCenters() {

		String sql = "";

		sql += "\n select distinct * from";
		sql += "\n (";
		sql += "\n select id ,name, description, owner_id from center where owner_id = " + CurrentUser.getUser().getId();
		sql += "\n union all";
		sql += "\n select c.id ,c.name, c.description, c.owner_id";
		sql += "\n from   sharing s, center c";
		sql += "\n where  s.center_id = c.id";
		sql += "\n and    s.user_id = " + CurrentUser.getUser().getId();
		sql += "\n union all";
		sql += "\n select c.id ,c.name, c.description, c.owner_id";
		sql += "\n from   sharing s, activity a, center c";
		sql += "\n where  s.activity_id = a.id";
		sql += "\n and  a.center_id = c.id";
		sql += "\n and    s.user_id = " + CurrentUser.getUser().getId();
		sql += "\n ) t";
		
		List<Center> list = new ArrayList<Center>();
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> m : result){
			Long id = (Long) m.get("id");
			String name = (String) m.get("name");
			String description = (String) m.get("description");
			Long ownerId = (Long) m.get("owner_id");
			list.add(new Center(id, name, description, new User(ownerId)));
		}

		return list;
	}
}
