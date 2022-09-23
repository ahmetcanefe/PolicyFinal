package com.CarPolicy.CarPolicy.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.CarPolicy.CarPolicy.dtos.PolicyTypeDto;
import com.CarPolicy.CarPolicy.entities.PolicyType;
import com.CarPolicy.CarPolicy.repositories.CarRepository;
import com.CarPolicy.CarPolicy.repositories.PolicyTypeRepository;

@Service
public class PolicyTypeService {

	private PolicyTypeRepository policyTypeRepository;
	private ModelMapper modelMapper;

	public PolicyTypeService(PolicyTypeRepository policyTypeRepository,ModelMapper modelMapper) {
		super();
		this.policyTypeRepository = policyTypeRepository;
		this.modelMapper = modelMapper;
	}
	
	public List<PolicyTypeDto> getAllPolicyTypes()
	{
		List<PolicyType> policyTypes = policyTypeRepository.findAll();
		if(policyTypes!=null)
		{
			List<PolicyTypeDto> policyTypeDtos = policyTypes.stream()
					.map(policy -> modelMapper.map(policy, PolicyTypeDto.class))
					.collect(Collectors.toList());
			
			return policyTypeDtos;
		}
		throw new RuntimeException("policyTypes bulunamadı");
	}
	
	public List<PolicyTypeDto> getCarPolicyTypes()
	{
		List<PolicyType> policyTypes = policyTypeRepository.findByNameIgnoreCase("Car Policy Type");
		if(policyTypes!=null)
		{
			List<PolicyTypeDto> policyTypeDtos = policyTypes.stream()
					.map(policy -> modelMapper.map(policy, PolicyTypeDto.class))
					.collect(Collectors.toList());
			
			return policyTypeDtos;
		}
		throw new RuntimeException("policyTypes bulunamadı");
	}
	
	public List<PolicyTypeDto> getDaskPolicyTypes()
	{
		List<PolicyType> policyTypes = policyTypeRepository.findByNameIgnoreCase("Dask Policy Type");
		if(policyTypes!=null)
		{
			List<PolicyTypeDto> policyTypeDtos = policyTypes.stream()
					.map(policy -> modelMapper.map(policy, PolicyTypeDto.class))
					.collect(Collectors.toList());
			
			return policyTypeDtos;
		}
		throw new RuntimeException("policyTypes bulunamadı");
	}
	
	public PolicyTypeDto getPolicyTypeById(int policyTypeId)
	{
		Optional<PolicyType> policyType = policyTypeRepository.findById(policyTypeId);
		if(policyType.isPresent())
		{
			PolicyType foundPolicyType = policyType.get();
			return modelMapper.map(foundPolicyType, PolicyTypeDto.class);
		}		
	    return null;
	}
	
	public PolicyTypeDto addPolicyType(PolicyTypeDto policyTypeDto)
	{
		PolicyType policyType = modelMapper.map(policyTypeDto, PolicyType.class);
		
		PolicyType addedPolicyType = policyTypeRepository.save(policyType);
		if(addedPolicyType!=null)
		{
			return modelMapper.map(addedPolicyType, PolicyTypeDto.class);
		}
			
		return null;
	}
	
	public PolicyTypeDto updatePolicyType(int policyTypeId, PolicyTypeDto policyTypeDto)
	{
		Optional<PolicyType> policyType = policyTypeRepository.findById(policyTypeId);
		if(policyType.isPresent())
		{
			PolicyType foundPolicy = policyType.get();
			foundPolicy.setName(policyTypeDto.getName());
			foundPolicy.setDetail(policyTypeDto.getDetail());
			
			PolicyType updatedPolicyType = policyTypeRepository.save(foundPolicy);
			
			return modelMapper.map(updatedPolicyType, PolicyTypeDto.class);
		}
		return null;		
	}
	
	public void deletePolicyType(int policyId)
	{
		Optional<PolicyType> policyType = policyTypeRepository.findById(policyId);
		if(policyType.isPresent())
		{	
			policyTypeRepository.delete(policyType.get());
		}
		else {
			throw new RuntimeException("PolicyType bulunamadı");
		}		
	}
	
	
}
