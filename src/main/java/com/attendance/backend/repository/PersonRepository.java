package com.attendance.backend.repository;

import org.springframework.data.repository.CrudRepository;

import com.attendance.backend.model.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {

    Person findByName(String name);
}
