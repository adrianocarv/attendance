package com.attendance.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendance.backend.model.Center;
import com.attendance.backend.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

	List<Person> findByNameStartsWithIgnoreCaseAndCenterOrderByNameAsc(String name, Center center);

	List<Person> findByNameIgnoreCaseAndCenter(String name, Center center);
}
