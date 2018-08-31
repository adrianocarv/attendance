package com.attendance.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.Center;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
	
	List<Activity> findByCenter(Center center);
	
	List<Activity> findByCenterOrderByName(Center center);
}
