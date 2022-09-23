package com.CarPolicy.CarPolicy.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.CarPolicy.CarPolicy.entities.DaskPolicy;
import com.CarPolicy.CarPolicy.entities.PolicyType;

public interface DaskPolicyRepository extends JpaRepository<DaskPolicy, Integer>{

	DaskPolicy findByBuilding_CustomerIdAndBuildingIdAndPolicyType(int customerId, int buildingId, PolicyType policyType);
	
	List<DaskPolicy> findByBuilding_Customer_IdAndIsActive(int customerId, boolean isActive);
	
	List<DaskPolicy> findByBuildingId(int buildingId);
	
	Page<DaskPolicy> findByPolicyTypeId(int policyTypeId, Pageable pageable);
	
	Page<DaskPolicy> findByPolicyTypeIdAndBuilding_AdressContaining(int policyTypeId, String query, Pageable pageable);
}
