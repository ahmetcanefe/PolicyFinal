package com.CarPolicy.CarPolicy.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.CarPolicy.CarPolicy.dtos.ApplyDaskPolicyDto;
import com.CarPolicy.CarPolicy.dtos.DaskPolicyDto;
import com.CarPolicy.CarPolicy.entities.Building;
import com.CarPolicy.CarPolicy.entities.Car;
import com.CarPolicy.CarPolicy.entities.Customer;
import com.CarPolicy.CarPolicy.entities.DaskPolicy;
import com.CarPolicy.CarPolicy.entities.PolicyType;

import com.CarPolicy.CarPolicy.repositories.BuildingRepository;
import com.CarPolicy.CarPolicy.repositories.CarRepository;
import com.CarPolicy.CarPolicy.repositories.CustomerRepository;
import com.CarPolicy.CarPolicy.repositories.DaskPolicyRepository;
import com.CarPolicy.CarPolicy.repositories.PolicyTypeRepository;
import com.CarPolicy.CarPolicy.utility.SecurityUtils;

@Service
public class DaskPolicyService {

	private BuildingRepository buildingRepository;
	private CustomerRepository customerRepository;
	private DaskPolicyRepository daskPolicyRepository;
	private PolicyTypeRepository policyTypeRepository;
	private ModelMapper modelMapper;

	public DaskPolicyService(DaskPolicyRepository daskPolicyRepository,BuildingRepository buildingRepository,PolicyTypeRepository policyTypeRepository,ModelMapper modelMapper,CustomerRepository customerRepository) {
		super();
		this.buildingRepository = buildingRepository;
		this.modelMapper = modelMapper;
		this.customerRepository = customerRepository;
		this.daskPolicyRepository = daskPolicyRepository;
		this.policyTypeRepository = policyTypeRepository;
	}
	
	
	public ApplyDaskPolicyDto addToDaskPolicy(ApplyDaskPolicyDto applyDaskPolicyDto)
	{
		Customer customer = customerRepository.findByNationalIdentity(applyDaskPolicyDto.getNationalIdentity());		
		Building building = buildingRepository.findByAdressAndCustomerId(applyDaskPolicyDto.getAdress(),customer.getId());
		Optional<PolicyType> policyType = policyTypeRepository.findById(applyDaskPolicyDto.getPolicyTypeId());
		DaskPolicy daskPolicy = daskPolicyRepository.findByBuilding_CustomerIdAndBuildingIdAndPolicyType(customer.getId(), building.getId(),policyType.get());
		
		if(building!=null && building.getCustomer() == customer && policyType.isPresent() && daskPolicy==null)
		{	
			java.util.Date currentDate = new java.util.Date(); 
		    
		    long startDateMinusCurrentDate = applyDaskPolicyDto.getStartDate().getTime()-currentDate.getTime();
		    long resultOfStartDateMinusCurrentDate = TimeUnit.DAYS.convert(startDateMinusCurrentDate, TimeUnit.MILLISECONDS);
		    
		    long currentDateMinusBirthDate = currentDate.getTime()-customer.getBirthDate().getTime();
		    long resultOfCurrentDateMinusBirthDate = TimeUnit.DAYS.convert(currentDateMinusBirthDate, TimeUnit.MILLISECONDS);
		    
		    long endDateMinusStartDate = applyDaskPolicyDto.getEndDate().getTime() - applyDaskPolicyDto.getStartDate().getTime();
		    long resultOfEndDateMinusStartDate = TimeUnit.DAYS.convert(endDateMinusStartDate, TimeUnit.MILLISECONDS);
				 
		    if(checkCustomersBirthDate(resultOfCurrentDateMinusBirthDate))
		    {
		    	if(checkStartDateIsAppropriate(resultOfStartDateMinusCurrentDate))
		    	{
		    		if(checkEndDateMinusStartDateAppropriate(resultOfEndDateMinusStartDate))
		    		{			    
		    			double price = 1;
                        long day = resultOfEndDateMinusStartDate;
                        int buildingYear = building.getConstructionYear();
					    double policyTypePrice = policyType.get().getMinPrice();			    
		                double rate = building.getCustomer().getCity().getRate();    
		                
		                price = calculatePrice(buildingYear,day,rate,policyTypePrice);                		                
		           
		                if(price > policyType.get().getMinPrice())
		                {
		                	DaskPolicy newDaskPolicy = new DaskPolicy();
		                	newDaskPolicy.setBuilding(building);
		                	newDaskPolicy.setPolicyType(policyType.get());
		                	newDaskPolicy.setStartDate(applyDaskPolicyDto.getStartDate());
		                	newDaskPolicy.setEndDate(applyDaskPolicyDto.getEndDate());
		                	newDaskPolicy.setPrice(price);
		                	newDaskPolicy.setActive(false);
		                
		        			DaskPolicy addedDaskPolicy = daskPolicyRepository.save(newDaskPolicy);
		        			if(addedDaskPolicy!=null)
		        			{	        				
		        				applyDaskPolicyDto.setPrice(price);
		        				applyDaskPolicyDto.setDaskPolicyId(addedDaskPolicy.getId());
			    				return applyDaskPolicyDto;
		        			}
		        			
		                }
				    }		
		    	}
		    }
		}
			   
		return null;
	}
	
	public DaskPolicyDto checkoutToDaskPolicy(ApplyDaskPolicyDto applyDaskPolicyDto)
	{
		Optional<DaskPolicy> daskPolicy = daskPolicyRepository.findById(applyDaskPolicyDto.getDaskPolicyId());
		if(daskPolicy!=null)
		{
			daskPolicy.get().setActive(true);
		    DaskPolicy updatedDaskPolicy = daskPolicyRepository.save(daskPolicy.get());
			return modelMapper.map(updatedDaskPolicy, DaskPolicyDto.class);
		}
		return null;
		
    }
	
	public List<ApplyDaskPolicyDto> getAllDaskPolicyHistory()
	{
		String email = SecurityUtils.getCurrentUser().getUsername();
		Customer customer = customerRepository.findByEmail(email);
		
		List<DaskPolicy> daskPolicies = daskPolicyRepository.findByBuilding_Customer_IdAndIsActive(customer.getId(),false);
		List<DaskPolicyDto> daskPolicyDtos = daskPolicies.stream().map(daskPolicy -> modelMapper.map(daskPolicy, DaskPolicyDto.class)).collect(Collectors.toList());
		
        List<ApplyDaskPolicyDto> applyDaskPolicyDtos = new ArrayList<>();
		
		for(DaskPolicyDto daskPolicyDto : daskPolicyDtos)
        {
            ApplyDaskPolicyDto applyDaskPolicyDto = new ApplyDaskPolicyDto();
            applyDaskPolicyDto.setAdress(daskPolicyDto.getBuilding().getAdress());
            applyDaskPolicyDto.setNationalIdentity(daskPolicyDto.getBuilding().getCustomer().getNationalIdentity());
            applyDaskPolicyDto.setStartDate(daskPolicyDto.getStartDate());
            applyDaskPolicyDto.setEndDate(daskPolicyDto.getEndDate());
            applyDaskPolicyDto.setPrice(daskPolicyDto.getPrice());
            applyDaskPolicyDto.setPolicyTypeId(daskPolicyDto.getPolicyType().getId());
            applyDaskPolicyDto.setDaskPolicyId(daskPolicyDto.getId());
            
            applyDaskPolicyDtos.add(applyDaskPolicyDto);
        }
		
		return applyDaskPolicyDtos;
	}
	
	public DaskPolicyDto getDaskPolicyById(int daskPolicyId)
	{
		Optional<DaskPolicy> daskPolicy = daskPolicyRepository.findById(daskPolicyId);
		if(daskPolicy.isPresent())
		{
			return modelMapper.map(daskPolicy, DaskPolicyDto.class);
		}
		return null;
	}
	
	public void deleteDaskPolicy(int daskPolicyId)
	{
		Optional<DaskPolicy> daskPolicy = daskPolicyRepository.findById(daskPolicyId);
		if(daskPolicy.isPresent())
		{
			daskPolicyRepository.delete(daskPolicy.get());
		}
		else 
		{
			throw new RuntimeException("Not Found daskPolicyId_"+daskPolicyId);
		}
		
	}
	
	public Page<DaskPolicy> getAllDaskPoliciesByPolicyTypeId(int policyTypeId, int pageNumber)
	{
		Pageable pageable = PageRequest.of(pageNumber-1, 5);
		Page<DaskPolicy> policyPage = daskPolicyRepository.findByPolicyTypeId(policyTypeId, pageable);
        
		return policyPage;
	}
	
	public List<DaskPolicyDto> getAllByBuildingId(int buildingId)
	{
		List<DaskPolicy> daskPolicies = daskPolicyRepository.findByBuildingId(buildingId);
		List<DaskPolicyDto> daskPolicyDtos = daskPolicies.stream().map(daskPolicy -> modelMapper.map(daskPolicy, DaskPolicyDto.class)).collect(Collectors.toList());
		return daskPolicyDtos;
	}
	
	public Page<DaskPolicy> searchDaskPoliciesByPolicyTypeId(int policyTypeId, String query, int pageNumber)
	{
		Pageable pageable = PageRequest.of(pageNumber-1, 5);
        Page<DaskPolicy> pageDaskPolicies = daskPolicyRepository.findByPolicyTypeIdAndBuilding_AdressContaining(policyTypeId,query,pageable);
        return pageDaskPolicies;
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
