package com.attendance.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendance.backend.model.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

}
