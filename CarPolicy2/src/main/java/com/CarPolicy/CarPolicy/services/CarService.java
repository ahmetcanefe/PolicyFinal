package com.CarPolicy.CarPolicy.services;

import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.joda.time.Days;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.CarPolicy.CarPolicy.dtos.CarAccidentDto;
import com.CarPolicy.CarPolicy.dtos.CarDto;
import com.CarPolicy.CarPolicy.dtos.PolicyTypeDto;
import com.CarPolicy.CarPolicy.entities.Car;
import com.CarPolicy.CarPolicy.entities.CarAccident;
import com.CarPolicy.CarPolicy.entities.CarPolicy;
import com.CarPolicy.CarPolicy.entities.Customer;
import com.CarPolicy.CarPolicy.entities.Model;
import com.CarPolicy.CarPolicy.repositories.CarPolicyRepository;
import com.CarPolicy.CarPolicy.repositories.CarRepository;
import com.CarPolicy.CarPolicy.repositories.CustomerRepository;
import com.CarPolicy.CarPolicy.repositories.ModelRepository;
import com.CarPolicy.CarPolicy.utility.DataResult;
import com.CarPolicy.CarPolicy.utility.ErrorDataResult;
import com.CarPolicy.CarPolicy.utility.ROLE;
import com.CarPolicy.CarPolicy.utility.SecurityUtils;
import com.CarPolicy.CarPolicy.utility.SuccessDataResult;


@Service
public class CarService {

	private CarRepository carRepository;
	private CustomerRepository customerRepository;
	private CarPolicyRepository carPolicyRepository;
	private ModelMapper modelMapper;
	private ModelRepository modelRepository;

	public CarService(CarRepository carRepository,ModelRepository modelRepository,ModelMapper modelMapper,CustomerRepository customerRepository,CarPolicyRepository carPolicyRepository) {
		super();
		this.carRepository = carRepository;
		this.modelMapper = modelMapper;
		this.customerRepository = customerRepository;
		this.carPolicyRepository = carPolicyRepository;
		this.modelRepository = modelRepository;
	}
	
	public List<CarDto> getAllCars()
	{
		String role = SecurityUtils.getRole();
		List<Car> cars = null;
		
		if(ROLE.ROLE_ADMIN.name().equals(role))
		{
			cars = carRepository.findAll();
		}	   
		else
		{
			String email = SecurityUtils.getCurrentUser().getUsername();
			Customer customer = customerRepository.findByEmail(email);
			
			cars = carRepository.findByCustomerId(customer.getId());
		}
		
		List<CarDto> carDtos = cars.stream()
                .map((car) -> modelMapper.map(car, CarDto.class))
                .collect(Collectors.toList());

        return carDtos;
	}

	
	public Page<Car> getAllCarsByPageNumber(int pageNumber, int pageSize)
	{
		
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		
		Page<Car> pageCars = carRepository.findAll(pageable);
		
        return pageCars;
	}
	
	public CarDto getCarById(int carId)
	{
		Optional<Car> car = carRepository.findById(carId);
		if(car.isPresent())
		{
			return modelMapper.map(car.get(), CarDto.class);
		}		
	    return null;	
	}
	
	public Car getByLicensePlate(String licensePlate)
	{
		Car car = carRepository.findByLicensePlate(licensePlate);
		if(car!=null)
		{
			return car;
		}
		return null;
	}
	
	public CarDto addCar(CarDto carDto)
	{
		String email = SecurityUtils.getCurrentUser().getUsername();
		Customer customer = customerRepository.findByEmail(email);
		
		if(customer!=null)
		{
			Model model = modelRepository.getById(carDto.getModelId());
			
			Car car = new Car();
			car.setModel(model);
			car.setCustomer(customer);
			car.setLicensePlate(carDto.getLicensePlate().toUpperCase());
			car.setColor(carDto.getColor().toUpperCase());
			car.setManufacturingYear(carDto.getManufacturingYear());
			car.setMileage(carDto.getMileage());
			
			Car addedCar = carRepository.save(car);
			return modelMapper.map(addedCar, CarDto.class);
		}
		return null;
		
	}
	
	public CarDto updateCar(int carId, CarDto carDto)
	{
		Optional<Car> car = carRepository.findById(carId);
		if(car.isPresent())
		{
			String email = SecurityUtils.getCurrentUser().getUsername();
			Customer customer = customerRepository.findByEmail(email);
			
			if(customer!=null)
			{
			   Model model = modelRepository.getById(carDto.getModelId());
				
			   Car foundCar = car.get();
			   foundCar.setLicensePlate(carDto.getLicensePlate().toUpperCase());
			   foundCar.setManufacturingYear(carDto.getManufacturingYear());
			   foundCar.setMileage(carDto.getMileage());
			   foundCar.setModel(model);
			   foundCar.setColor(carDto.getColor().toUpperCase());
		       foundCar.setCustomer(customer);
		    
			   Car updatedCar = carRepository.save(foundCar);
			
			   return modelMapper.map(updatedCar, CarDto.class);
			}
		}
		return null;
	}
	
	public void deleteCar(int carId)
	{
		Optional<Car> car = carRepository.findById(carId);
		if(car.isPresent())
		{
			List<CarPolicy> policies = carPolicyRepository.findByCarId(carId);
			carPolicyRepository.deleteAll(policies);
			
			carRepository.deleteById(carId);
		}
	}
	
	public void deleteFromPolicy(int carId)
	{
		Optional<Car> car = carRepository.findById(carId);
		if(car.isPresent())
		{
			Car foundCar = car.get();

			carRepository.delete(foundCar);
		}
		else {
			throw new RuntimeException("araba yok");	
		}
	}
	
	public Page<Car> searchCar(String query, int pageSize, int pageNumber, String sortBy, String sortDir)
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
		Page<Car> pageCars = null;
        
		pageCars = carRepository.findAll(query, pageable);
       
		
		if(pageCars!=null)
		{
			return pageCars;
		}
		return null;
	}
	
	
	public Car getByLicensePlateAndCustomerId(String licensePlate)
	{
		String email = SecurityUtils.getCurrentUser().getUsername();
		Customer customer = customerRepository.findByEmail(email);
		
		Car car = carRepository.findByLicensePlateAndCustomerId(licensePlate, customer.getId());
		if(car!=null)
		{
			return car;
		}
		return null;
	}

	
	public List<CarDto> getAvailableCarsForInsurance(PolicyTypeDto policyTypeDto)
	{
		String email = SecurityUtils.getCurrentUser().getUsername();
		Customer customer = customerRepository.findByEmail(email);
		
		List<Car> cars = carRepository.findByCustomerId(customer.getId());
		List<Car> availableCars = new ArrayList<>();
		for(Car car : cars)
		{
			if(!carRepository.existsCarByCarPolicies_PolicyType_NameAndId(policyTypeDto.getName(), car.getId()))
			{
				availableCars.add(car);
			}
		}
		List<CarDto> carDtos = availableCars.stream().map(availableCar -> modelMapper.map(availableCar, CarDto.class)).collect(Collectors.toList());
		return carDtos;
	}
	
	
}
