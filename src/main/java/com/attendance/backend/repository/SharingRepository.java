package com.attendance.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendance.backend.model.Sharing;
import com.attendance.backend.model.User;

public interface SharingRepository extends JpaRepository<Sharing, Long> {

	List<Sharing> findByUser(User user);
}
