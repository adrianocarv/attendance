package com.attendance.backend.repository;

import org.springframework.data.repository.CrudRepository;

import com.attendance.backend.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findOneByUsername(String username);

	User findOneByEmail(String email);
}
