package com.CarPolicy.CarPolicy.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.CarPolicy.CarPolicy.entities.Car;
import com.CarPolicy.CarPolicy.entities.CarPolicy;
import com.CarPolicy.CarPolicy.entities.PolicyType;

public interface CarPolicyRepository extends JpaRepository<CarPolicy,Integer>{

	CarPolicy findByCar_CustomerIdAndCarIdAndPolicyType(int customerId, int carId, PolicyType policyType);
	
	
	Page<CarPolicy> findByPolicyTypeId(int policyTypeId, Pageable pageable);
	
	List<CarPolicy> findByCarId(int carId);
	
	List<CarPolicy> findByCarIdAndIsActive(int carId, boolean isActive);
	
	Page<CarPolicy> findByPolicyTypeIdAndCar_LicensePlateContaining(int policyTypeId, String query, Pageable pageable);
	
	boolean existsByCarIdAndPolicyType_Id(int carId,int policyTypeId);
	
	List<CarPolicy> findByCar_Customer_IdAndIsActive(int customerId, boolean active);
	
	void deleteAllByCarIn(List<Car> cars);
}

