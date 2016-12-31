package com.attendance.backend.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.attendance.backend.model.OutputView;

public interface OutputViewRepository extends CrudRepository<OutputView, String> {

	List<OutputView> findByName(String name);
}