package com.CarPolicy.CarPolicy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CarPolicy.CarPolicy.entities.PolicyType;

public interface PolicyTypeRepository extends JpaRepository<PolicyType, Integer>{

	List<PolicyType> findByNameIgnoreCase(String name);

}
