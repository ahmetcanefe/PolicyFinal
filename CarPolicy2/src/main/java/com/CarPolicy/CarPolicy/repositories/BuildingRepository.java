package com.CarPolicy.CarPolicy.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.CarPolicy.CarPolicy.entities.Building;
import com.CarPolicy.CarPolicy.entities.Car;

public interface BuildingRepository extends JpaRepository<Building,Integer>{

	Building findByAdressAndCustomerId(String adress, int customerId);
	List<Building> findByCustomerId(int customerId);
	
	@Query("SELECT b FROM Building b WHERE " 
	        + "CONCAT(b.adress, b.constructionYear, b.city.name, b.numberOfFloors, b.area, b.usage) " 
			+ "LIKE %?1%")
	Page<Building> findAll(String query, Pageable pageable);
	
	boolean existsBuildingByDaskPolicies_PolicyType_NameAndId(String name, int buildingId);
}
