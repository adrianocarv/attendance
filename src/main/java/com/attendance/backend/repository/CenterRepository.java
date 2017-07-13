package com.attendance.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendance.backend.model.Center;

public interface CenterRepository extends JpaRepository<Center, Long> {

}
