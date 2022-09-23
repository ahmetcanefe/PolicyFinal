package com.CarPolicy.CarPolicy.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.CarPolicy.CarPolicy.dtos.BuildingDto;
import com.CarPolicy.CarPolicy.dtos.CarDto;
import com.CarPolicy.CarPolicy.dtos.PolicyTypeDto;
import com.CarPolicy.CarPolicy.entities.Building;
import com.CarPolicy.CarPolicy.entities.Car;
import com.CarPolicy.CarPolicy.entities.City;
import com.CarPolicy.CarPolicy.entities.Customer;
import com.CarPolicy.CarPolicy.entities.DaskPolicy;
import com.CarPolicy.CarPolicy.repositories.BuildingRepository;
import com.CarPolicy.CarPolicy.repositories.CityRepository;
import com.CarPolicy.CarPolicy.repositories.CustomerRepository;
import com.CarPolicy.CarPolicy.repositories.DaskPolicyRepository;
import com.CarPolicy.CarPolicy.utility.SecurityUtils;

@Service
public class BuildingService {

	private BuildingRepository buildingRepository;
	private CustomerRepository customerRepository;
	private CityRepository cityRepository;
	private DaskPolicyRepository daskPolicyRepository;
	private ModelMapper modelMapper;

	public BuildingService(DaskPolicyRepository daskPolicyRepository,BuildingRepository buildingRepository,CityRepository cityRepository, CustomerRepository customerRepository,ModelMapper modelMapper) {
		super();
		this.buildingRepository = buildingRepository;
		this.customerRepository = customerRepository;
		this.modelMapper = modelMapper;
		this.cityRepository = cityRepository;
		this.daskPolicyRepository = daskPolicyRepository;
	}
	
	public List<BuildingDto> getAllBuildingsByCustomer()
	{
		String email = SecurityUtils.getCurrentUser().getUsername();
		Customer customer = customerRepository.findByEmail(email);
		
		List<Building> buildings = buildingRepository.findByCustomerId(customer.getId());
		List<BuildingDto> buldingDtos = buildings.stream().map(building -> modelMapper.map(building, BuildingDto.class)).collect(Collectors.toList());
		
		return buldingDtos;
	}
	
	public List<BuildingDto> getAllBuildingsByAdmin()
	{	
		List<Building> buildings = buildingRepository.findAll();
		List<BuildingDto> buldingDtos = buildings.stream().map(building -> modelMapper.map(building, BuildingDto.class)).collect(Collectors.toList());
		
		return buldingDtos;
	}
	
	public BuildingDto getBuildingById(int buildingId)
	{
		Optional<Building> building = buildingRepository.findById(buildingId);
		if(building.isPresent())
		{
			return modelMapper.map(building.get(), BuildingDto.class);
		}
		return null;
	}
	
	public BuildingDto addBuilding(BuildingDto buildingDto)
	{
		String email = SecurityUtils.getCurrentUser().getUsername();
		Customer customer = customerRepository.findByEmail(email);
		if(customer!=null)
		{
			City city = cityRepository.getById(buildingDto.getCityId());
			
			Building building = modelMapper.map(buildingDto, Building.class);
			building.setCity(city);
			building.setCustomer(customer);
			
			Building addedBuilding = buildingRepository.save(building);
			
			return modelMapper.map(addedBuilding, BuildingDto.class);
		}
		return null;
	}
	
	
	public BuildingDto updateBuilding(int buildingId, BuildingDto buildingDto)
	{
		Optional<Building> building = buildingRepository.findById(buildingId);
		if(building.isPresent())
		{
			City city = cityRepository.getById(buildingDto.getCityId());
			
			Building foundBuilding = building.get();
			foundBuilding.setAdress(buildingDto.getAdress());
			foundBuilding.setArea(buildingDto.getArea());
			foundBuilding.setCity(city);
			foundBuilding.setConstructionYear(buildingDto.getConstructionYear());
			foundBuilding.setNumberOfFloors(buildingDto.getNumberOfFloors());
			foundBuilding.setUsage(buildingDto.getUsage());
			
			Building updatedBuilding = buildingRepository.save(foundBuilding);
			
			return modelMapper.map(updatedBuilding, BuildingDto.class);
		}
		return null;
	}
	
	public void deleteBuilding(int buildingId)
	{
		Optional<Building> building = buildingRepository.findById(buildingId);
		if(building.isPresent())
		{
			List<DaskPolicy> daskPolicies = daskPolicyRepository.findByBuildingId(buildingId);
			daskPolicyRepository.deleteAll(daskPolicies);
			
			buildingRepository.deleteById(buildingId);
		}
		else
		{
			throw new RuntimeException("NotFound buildingId_"+buildingId);
		}		
	}
	
//	public List<BuildingDto> getAvailableBuildingsForInsurance(PolicyTypeDto policyTypeDto)
//	{
//		String email = SecurityUtils.getCurrentUser().getUsername();
//		Customer customer = customerRepository.findByEmail(email);
//		
//		List<Building> buildings = buildingRepository.findByCustomerId(customer.getId());
//		List<Building> availableBuildings = new ArrayList<>();
//		for(Building building : buildings)
//		{
//			int a = 1;
//			List<DaskPolicy> daskPolicies = daskPolicyRepository.findByBuildingId(building.getId());
//			for(DaskPolicy daskPolicy : daskPolicies)
//			{
//				if(daskPolicy.getPolicyType().getCategory().getName().contentEquals(policyTypeDto.getCategory().getName()))
//				{
//					a=0;
//				}
//			}
//			if(a==1)
//			{
//				availableBuildings.add(building);
//			}
//		}	
//		
//		List<BuildingDto> buildingDtos = availableBuildings.stream().map(availableBuilding -> modelMapper.map(availableBuilding, BuildingDto.class)).collect(Collectors.toList());
//		return buildingDtos;
//	}
	
	public List<BuildingDto> getAvailableBuildingsForInsurance(PolicyTypeDto policyTypeDto)
	{
		String email = SecurityUtils.getCurrentUser().getUsername();
		Customer customer = customerRepository.findByEmail(email);
		
		List<Building> buildings = buildingRepository.findByCustomerId(customer.getId());
		List<Building> availableBuildings = new ArrayList<>();
		for(Building building : buildings)
		{
			if(!buildingRepository.existsBuildingByDaskPolicies_PolicyType_NameAndId(policyTypeDto.getName(), building.getId()))
			{
				availableBuildings.add(building);
			}
		}	
		
		List<BuildingDto> buildingDtos = availableBuildings.stream().map(availableBuilding -> modelMapper.map(availableBuilding, BuildingDto.class)).collect(Collectors.toList());
		return buildingDtos;
	}
	
	public Building getByAdressAndCustomerId(String address)
	{
		String email = SecurityUtils.getCurrentUser().getUsername();
		Customer customer = customerRepository.findByEmail(email);
		
		Building building = buildingRepository.findByAdressAndCustomerId(address, customer.getId());
		if(building!=null)
		{
			return building;
		}
		return null;
	}
	
	public Page<Building> getBuildingsByPageNumber(int pageNumber)
	{
		Pageable pageable = PageRequest.of(pageNumber-1, 5);
		Page<Building> pageBuildings = buildingRepository.findAll(pageable);
		
		return pageBuildings;
	}
	
	public Page<Building> searchBuilding(String query, int pageSize, int pageNumber, String sortBy, String sortDir)
	{
		Sort sort = null;
		if(sortDir.equalsIgnoreCase("asc"))
		{
			sort=Sort.by(sortBy).ascending();
		}
		else
		{
			sort = Sort.by(sortBy).descending();
		}
		
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize, sort);
		Page<Building> pageBuildings = buildingRepository.findAll(query, pageable);
		
		if(pageBuildings!=null)
		{
			return pageBuildings;
		}
		return null;
	}
	
	
}
