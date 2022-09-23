package com.CarPolicy.CarPolicy.services;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.joda.time.Years;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.CarPolicy.CarPolicy.dtos.ApplyCarPolicyDto;
import com.CarPolicy.CarPolicy.dtos.CarDto;
import com.CarPolicy.CarPolicy.dtos.CarPolicyDto;
import com.CarPolicy.CarPolicy.entities.Car;
import com.CarPolicy.CarPolicy.entities.CarAccident;
import com.CarPolicy.CarPolicy.entities.CarPolicy;
import com.CarPolicy.CarPolicy.entities.Customer;
import com.CarPolicy.CarPolicy.entities.PolicyType;
import com.CarPolicy.CarPolicy.repositories.CarPolicyRepository;
import com.CarPolicy.CarPolicy.repositories.CarRepository;
import com.CarPolicy.CarPolicy.repositories.CustomerRepository;
import com.CarPolicy.CarPolicy.repositories.PolicyTypeRepository;
import com.CarPolicy.CarPolicy.utility.SecurityUtils;

@Service
public class CarPolicyService {

	private CarRepository carRepository;
	private CustomerRepository customerRepository;
	private CarPolicyRepository carPolicyRepository;
	private PolicyTypeRepository policyTypeRepository;
	private ModelMapper modelMapper;

	public CarPolicyService(CarRepository carRepository,PolicyTypeRepository policyTypeRepository,ModelMapper modelMapper,CustomerRepository customerRepository,CarPolicyRepository carPolicyRepository) {
		super();
		this.carRepository = carRepository;
		this.modelMapper = modelMapper;
		this.customerRepository = customerRepository;
		this.carPolicyRepository = carPolicyRepository;
		this.policyTypeRepository = policyTypeRepository;
	}
	

	
	public ApplyCarPolicyDto addToPolicy(ApplyCarPolicyDto applyPolicyDto)
	{
		Customer customer = customerRepository.findByNationalIdentity(applyPolicyDto.getNationalIdentity());		
		Car car = carRepository.findByLicensePlateAndCustomerId(applyPolicyDto.getLicensePlate(),customer.getId());
		Optional<PolicyType> policyType = policyTypeRepository.findById(applyPolicyDto.getPolicyTypeId());
		CarPolicy carPolicy = carPolicyRepository.findByCar_CustomerIdAndCarIdAndPolicyType(customer.getId(), car.getId(),policyType.get());
		
		if(car!=null && car.getCustomer() == customer && policyType.isPresent() && carPolicy==null)
		{	
			    java.util.Date currentDate = new java.util.Date(); 
			    
			    long startDateMinusCurrentDate = applyPolicyDto.getStartDate().getTime()-currentDate.getTime();
			    long resultOfStartDateMinusCurrentDate = TimeUnit.DAYS.convert(startDateMinusCurrentDate, TimeUnit.MILLISECONDS);
			    
			    long currentDateMinusBirthDate = currentDate.getTime()-customer.getBirthDate().getTime();
			    long resultOfCurrentDateMinusBirthDate = TimeUnit.DAYS.convert(currentDateMinusBirthDate, TimeUnit.MILLISECONDS);
			    
			    long endDateMinusStartDate = applyPolicyDto.getEndDate().getTime() - applyPolicyDto.getStartDate().getTime();
			    long resultOfEndDateMinusStartDate = TimeUnit.DAYS.convert(endDateMinusStartDate, TimeUnit.MILLISECONDS);
			    
				    if(checkCustomersBirthDate(resultOfCurrentDateMinusBirthDate))
				    {
				    	if(checkStartDateIsAppropriate(resultOfStartDateMinusCurrentDate))
				    	{
				    		if(checkEndDateMinusStartDateAppropriate(resultOfEndDateMinusStartDate))
				    		{
                            double price = 1;
                            long day = resultOfEndDateMinusStartDate;
                            int carYear = car.getManufacturingYear();
						    double policyTypePrice = policyType.get().getMinPrice();			    
			                double rate = car.getCustomer().getCity().getRate();     
			                
			                price = calculatePrice(carYear,day,rate,policyTypePrice);			               
			           
			                if(price > policyType.get().getMinPrice())
			                {
			                	CarPolicy newCarPolicy = new CarPolicy();
			                	newCarPolicy.setCar(car);
			                	newCarPolicy.setPolicyType(policyType.get());
			                	newCarPolicy.setStartDate(applyPolicyDto.getStartDate());
			                	newCarPolicy.setEndDate(applyPolicyDto.getEndDate());
			                	newCarPolicy.setPrice(price);
			                	newCarPolicy.setActive(false);
			                
			        			CarPolicy addedPolicy = carPolicyRepository.save(newCarPolicy);
			        			if(addedPolicy!=null)
			        			{	        				
			        				applyPolicyDto.setPrice(price);
			        				applyPolicyDto.setPolicyId(addedPolicy.getId());
				    				return applyPolicyDto;
			        			}
			        			
			                }
					     }			
			    	}
			    	}
			    }
		return null;
	}
     
	public CarPolicyDto checkoutToPolicy(ApplyCarPolicyDto applyPolicyDto)
	{
		Optional<CarPolicy> policy = carPolicyRepository.findById(applyPolicyDto.getPolicyId());
		if(policy!=null)
		{
			policy.get().setActive(true);
		    CarPolicy updatedPolicy = carPolicyRepository.save(policy.get());
			return modelMapper.map(updatedPolicy, CarPolicyDto.class);
		}
		return null;
		
    }
	
	public void deletePolicy(int policyId)
	{
		Optional<CarPolicy> policy = carPolicyRepository.findById(policyId);
		if(policy.isPresent())
		{
			carPolicyRepository.delete(policy.get());
		}
		else
		{
			throw new RuntimeException("böyle bir kayıt yok");
		}
	}
	
	public CarPolicyDto getPolicyById(int policyId)
	{
		Optional<CarPolicy> policy = carPolicyRepository.findById(policyId);
		if(policy!=null)
		{
			return modelMapper.map(policy, CarPolicyDto.class);
		}
		return null;
	}
	
	public Page<CarPolicy> getAllPoliciesByPolicyTypeId(int policyTypeId, int pageNumber)
	{
		Pageable pageable = PageRequest.of(pageNumber-1, 5);
		Page<CarPolicy> policyPage = carPolicyRepository.findByPolicyTypeId(policyTypeId, pageable);
        
		return policyPage;
	}
	
	public List<CarPolicyDto> getAllByCarId(int carId)
	{
		List<CarPolicy> policies = carPolicyRepository.findByCarIdAndIsActive(carId, true);
		
		List<CarPolicyDto> policyDtos = policies.stream().map((policy) -> modelMapper.map(policy, CarPolicyDto.class)).collect(Collectors.toList());
	    if(policyDtos!=null)
		return policyDtos;
	    
	    return null;
	}
	
	public Page<CarPolicy> searchPoliciesByPolicyTypeId(int policyTypeId, String query, int pageNumber)
	{
		Pageable pageable = PageRequest.of(pageNumber-1, 5);
        Page<CarPolicy> pagePolicies = carPolicyRepository.findByPolicyTypeIdAndCar_LicensePlateContaining(policyTypeId,query,pageable);
        return pagePolicies;
	}
	
	public List<ApplyCarPolicyDto> getAllCarPolicyHistory()
	{
		String email = SecurityUtils.getCurrentUser().getUsername();
		Customer customer = customerRepository.findByEmail(email);
		
		List<CarPolicy> policies = carPolicyRepository.findByCar_Customer_IdAndIsActive(customer.getId(),false);
		List<CarPolicyDto> policyDtos = policies.stream().map(policy -> modelMapper.map(policy, CarPolicyDto.class)).collect(Collectors.toList());
	
		List<ApplyCarPolicyDto> applyPolicyDtos = new ArrayList<>();
		
		for(CarPolicyDto policyDto : policyDtos)
        {
			ApplyCarPolicyDto applyPolicyDto = new ApplyCarPolicyDto();
            applyPolicyDto.setLicensePlate(policyDto.getCar().getLicensePlate());
            applyPolicyDto.setNationalIdentity(policyDto.getCar().getCustomer().getNationalIdentity());
            applyPolicyDto.setStartDate(policyDto.getStartDate());
            applyPolicyDto.setEndDate(policyDto.getEndDate());
            applyPolicyDto.setPrice(policyDto.getPrice());
            applyPolicyDto.setPolicyTypeId(policyDto.getPolicyType().getId());
            applyPolicyDto.setPolicyId(policyDto.getId());
            
            applyPolicyDtos.add(applyPolicyDto);
        }
		
		
		return applyPolicyDtos;
	}
	
	private boolean checkCustomersBirthDate(long date)
	{
		return date > 6570 ? true : false;
	}
	
	private boolean checkStartDateIsAppropriate(long date)
	{
		return (date >= 0 & date <= 30) ? true : false;
	}
	
	private boolean checkEndDateMinusStartDateAppropriate(long date)
	{
		return (date >= 30) ? true : false;
	}
	
	private double calculatePrice(int carYear,long day,double rate,double policyTypePrice)
	{
		 double price = 1;
		 
		 if(carYear>1950 && carYear<=1970)
         {
         	price = policyTypePrice*day*3*rate;
         }
         else if(carYear>1970 && carYear<=1990)
         {
         	price = policyTypePrice*day*3.5*rate;
         }
         else if(carYear>1990 && carYear<=2000)
         {
         	price = policyTypePrice*day*4*rate;
         }
         else if(carYear>2000 && carYear<=2005)
         {
         	price = policyTypePrice*day*4.5*rate;
         }
         else if(carYear>2005 && carYear<=2010)
         {
         	price = policyTypePrice*day*5*rate;
         }
         else if(carYear>2010 && carYear<=2015)
         {
         	price = policyTypePrice*day*5.5*rate;
         }
         else if(carYear>2015 && carYear<=2020)
         {
         	price = policyTypePrice*day*6*rate;
         }
         else if(carYear>2020)
         {
         	price = policyTypePrice*day*6.5*rate;
         }
		 
		 return price;
	}
}

