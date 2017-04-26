package com.attendance.backend.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.Attendance;

public interface AttendanceRepository extends CrudRepository<Attendance, Long> {

	List<Attendance> findByDateAndActivity(Date date, Activity activity);

}
