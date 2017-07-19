package com.attendance.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.Center;
import com.attendance.backend.model.Sharing;
import com.attendance.backend.model.User;

public interface SharingRepository extends JpaRepository<Sharing, Long> {

	List<Sharing> findByUser(User user);

	List<Sharing> findByCenter(Center center);
	
	List<Sharing> findByCenterAndUser(Center currentCenter, User user);
	
	List<Sharing> findByCenterAndActivity(Center currentCenter, Activity activity);
	
	List<Sharing> findByCenterAndUserAndActivity(Center currentCenter, User user, Activity activity);
}
