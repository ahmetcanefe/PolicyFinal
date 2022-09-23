package com.CarPolicy.CarPolicy.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.CarPolicy.CarPolicy.entities.City;
import com.CarPolicy.CarPolicy.repositories.CityRepository;

@Service
public class CityService {

	private CityRepository cityRepository;

	public CityService(CityRepository cityRepository) {
		super();
		this.cityRepository = cityRepository;
	}
	
	
	public List<City> getAllCities()
	{
		List<City> cities = cityRepository.findAll();
		return cities;
	}
	
	public City getCityById(int cityId)
	{
		Optional<City> city = cityRepository.findById(cityId);
		if(city.isPresent())
		{
			return city.get();
		}
		return null;
	}

}
