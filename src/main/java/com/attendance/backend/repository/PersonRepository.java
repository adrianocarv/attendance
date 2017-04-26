package com.attendance.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendance.backend.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

	List<Person> findByNameStartsWithIgnoreCase(String name);
	
	
}
