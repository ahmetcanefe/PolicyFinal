package com.CarPolicy.CarPolicy.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.CarPolicy.CarPolicy.entities.Car;

public interface CarRepository extends JpaRepository<Car,Integer>{

	List<Car> findByCustomerId(int userId);
	
	Car findByLicensePlate(String licensePlate);
	
	Car findByLicensePlateAndCustomerId(String licensePlate, int customerId);
	
	boolean existsCarByCarPolicies_PolicyType_NameAndId(String name,int carId);
	
	Page<Car> findByLicensePlateContaining(String query,Pageable pageable);
	
	@Query("SELECT c FROM Car c WHERE " 
	        + "CONCAT(c.licensePlate, c.model.name, c.color, c.manufacturingYear, c.mileage) " 
			+ "LIKE %?1%")
	Page<Car> findAll(String query, Pageable pageable);
	
	//List<Car> findAllDistinctByCarPolicies_PolicyType_Id(int policyTypeId);
	
	
}
