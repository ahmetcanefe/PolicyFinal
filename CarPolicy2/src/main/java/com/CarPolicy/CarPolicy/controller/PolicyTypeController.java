package com.CarPolicy.CarPolicy.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.CarPolicy.CarPolicy.dtos.ApplyCarPolicyDto;
import com.CarPolicy.CarPolicy.dtos.ApplyDaskPolicyDto;
import com.CarPolicy.CarPolicy.dtos.BuildingDto;
import com.CarPolicy.CarPolicy.dtos.CarDto;
import com.CarPolicy.CarPolicy.dtos.CarPolicyDto;
import com.CarPolicy.CarPolicy.dtos.CustomerDto;
import com.CarPolicy.CarPolicy.dtos.DaskPolicyDto;
import com.CarPolicy.CarPolicy.dtos.PolicyTypeDto;
import com.CarPolicy.CarPolicy.entities.Car;
import com.CarPolicy.CarPolicy.entities.Customer;
import com.CarPolicy.CarPolicy.repositories.CarRepository;
import com.CarPolicy.CarPolicy.repositories.CustomerRepository;
import com.CarPolicy.CarPolicy.services.BuildingService;
import com.CarPolicy.CarPolicy.services.CarPolicyService;
import com.CarPolicy.CarPolicy.services.CarService;
import com.CarPolicy.CarPolicy.services.DaskPolicyService;
import com.CarPolicy.CarPolicy.services.PolicyTypeService;
import com.CarPolicy.CarPolicy.utility.SecurityUtils;

@Controller
public class PolicyTypeController {

	private PolicyTypeService policyTypeService;
	private CarService carService;
	private CarPolicyService carPolicyService;
	private BuildingService buildingService;
	private ModelMapper modelMapper;	
	private DaskPolicyService daskPolicyService;
	private CustomerRepository customerRepository;
	
	public PolicyTypeController(DaskPolicyService daskPolicyService,PolicyTypeService policyTypeService, CarService carService,CustomerRepository customerRepository, CarPolicyService carPolicyService,
			BuildingService buildingService, ModelMapper modelMapper) {
		super();
		this.policyTypeService = policyTypeService;
		this.carService = carService;
		this.daskPolicyService = daskPolicyService;
		this.carPolicyService = carPolicyService;
		this.customerRepository = customerRepository;
		this.buildingService = buildingService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/carPolicyTypes/getAll")
	public String getAllPolicies(Model model)
	{
		List<PolicyTypeDto> policyTypeDtos = policyTypeService.getCarPolicyTypes();
		if(policyTypeDtos!=null)
		{
			model.addAttribute("policies",policyTypeDtos);
			return "policy/car/index";
		}
		return "NotFound";
	}	
	
	@GetMapping("/carPolicies/apply/{policyTypeId}")
	public String applyToCarPolicy(@PathVariable int policyTypeId, Model model)
	{
		PolicyTypeDto policyTypeDto = policyTypeService.getPolicyTypeById(policyTypeId);
		if(policyTypeDto!=null)
		{	
			String email = SecurityUtils.getCurrentUser().getUsername();
			Customer customer = customerRepository.findByEmail(email);			
			
			ApplyCarPolicyDto applyCarPolicyDto = new ApplyCarPolicyDto();
			applyCarPolicyDto.setPolicyTypeId(policyTypeId);
			applyCarPolicyDto.setNationalIdentity(customer.getNationalIdentity());
            
			List<CarDto> carDtos = carService.getAvailableCarsForInsurance(policyTypeDto);
			
			model.addAttribute("cars", carDtos);
			model.addAttribute("policy", applyCarPolicyDto);
			return "/policy/car/apply";
				
		}
		model.addAttribute("message","PolicyType Not Found For PolicyTypeId_"+policyTypeId);
		return "NotFound";
	}
	
	@PostMapping("/carPolicies/apply")
	public String applyToPolicy(@Valid @ModelAttribute("applyPolicyDto") ApplyCarPolicyDto applyCarPolicyDto,
			                    BindingResult result,
			                    Model model)
	{
		if(!result.hasErrors())
		{
			ApplyCarPolicyDto addedApplyPolicyDto = carPolicyService.addToPolicy(applyCarPolicyDto);
			if(addedApplyPolicyDto!=null)
			{		
				model.addAttribute("policy", addedApplyPolicyDto);
			    return "policy/car/checkout";
			}			
		}
		model.addAttribute("policy", applyCarPolicyDto);
		return "/policy/apply";
	}
	
	@GetMapping("/policies/checkout/{policyId}")
	public String goToCarCheckout(@PathVariable int policyId,Model model)
	{
		CarPolicyDto policyDto = carPolicyService.getPolicyById(policyId);
		if(policyDto!=null)
		{
			ApplyCarPolicyDto applyCarPolicyDto = new ApplyCarPolicyDto();
			applyCarPolicyDto.setLicensePlate(policyDto.getCar().getLicensePlate());
			applyCarPolicyDto.setNationalIdentity(policyDto.getCar().getCustomer().getNationalIdentity());
			applyCarPolicyDto.setStartDate(policyDto.getStartDate());
			applyCarPolicyDto.setEndDate(policyDto.getEndDate());
			applyCarPolicyDto.setPrice(policyDto.getPrice());
			applyCarPolicyDto.setPolicyTypeId(policyDto.getPolicyType().getId());
			applyCarPolicyDto.setPolicyId(policyDto.getId());
					
			model.addAttribute("policy", applyCarPolicyDto);
		    return "policy/car/checkout";					
		}
		model.addAttribute("message","Hatalı İşlem!");
		return "NotFound";
		
	}
	
	@GetMapping("/policies/checkout/delete/{policyId}")
	public String deleteCarPolicyFromCheckout(@PathVariable int policyId,Model model)
	{
		CarPolicyDto carPolicyDto = carPolicyService.getPolicyById(policyId);
		if(carPolicyDto!=null)
		{
			carPolicyService.deletePolicy(policyId);
			return "redirect:/cars/getAll";
		}
		model.addAttribute("message","Hatalı İşlem!");
		return "NotFound";
		
	}
	
	@PostMapping("/carPolicies/checkout")
	public String checkoutToPolicy(@Valid @ModelAttribute("applyPolicyDto") ApplyCarPolicyDto applyCarPolicyDto,
			                    BindingResult result,
			                    Model model)
	{
		if(!result.hasErrors())
		{
			CarPolicyDto checkoutPolicyDto = carPolicyService.checkoutToPolicy(applyCarPolicyDto);
			if(checkoutPolicyDto!=null)
			{			
			    return "redirect:/cars/getAll";
			}
		}
		
		model.addAttribute(applyCarPolicyDto);
		return "/policy/apply";
	}
	
	
	@GetMapping("policies/history")
	public String getAllPoliciesHistory(Model model)
	{
		List<ApplyCarPolicyDto> applyCarPolicyDtos = carPolicyService.getAllCarPolicyHistory();
		
		List<ApplyDaskPolicyDto> applyDaskPolicyDtos = daskPolicyService.getAllDaskPolicyHistory();	
		
		model.addAttribute("carPolicies",applyCarPolicyDtos);
		model.addAttribute("daskPolicies",applyDaskPolicyDtos);
		return "/policy/history";
	}
	
	
	
	@GetMapping("/daskPolicyTypes/getAll")
	public String getAllDaskPolicies(Model model)
	{
		List<PolicyTypeDto> policyTypeDtos = policyTypeService.getDaskPolicyTypes();
		if(policyTypeDtos!=null)
		{
			model.addAttribute("policies",policyTypeDtos);
			return "policy/dask/index";
		}
		return "NotFound";
	}
	
	@GetMapping("/daskPolicies/apply/{policyTypeId}")
	public String applyToDaskPolicy(@PathVariable int policyTypeId, Model model)
	{
		PolicyTypeDto policyTypeDto = policyTypeService.getPolicyTypeById(policyTypeId);
		if(policyTypeDto!=null)
		{	
			String email = SecurityUtils.getCurrentUser().getUsername();
			Customer customer = customerRepository.findByEmail(email);			
			
			ApplyDaskPolicyDto applyDaskPolicyDto = new ApplyDaskPolicyDto();
			applyDaskPolicyDto.setPolicyTypeId(policyTypeId);
			applyDaskPolicyDto.setNationalIdentity(customer.getNationalIdentity());
            
			List<BuildingDto> buildingDtos = buildingService.getAvailableBuildingsForInsurance(policyTypeDto);
			
			model.addAttribute("buildings", buildingDtos);
			model.addAttribute("policy", applyDaskPolicyDto);
			return "/policy/dask/apply";
				
		}
		model.addAttribute("message","PolicyType Not Found For policyTypeId"+policyTypeId);
		return "NotFound";
	}
	
	@PostMapping("/daskPolicies/apply")
	public String applyToDaskPolicy(@Valid @ModelAttribute("applyDaskPolicyDto") ApplyDaskPolicyDto applyDaskPolicyDto,
			                         BindingResult result,
			                         Model model)
	{
		if(!result.hasErrors())
		{
			ApplyDaskPolicyDto addedApplyDaskPolicyDto = daskPolicyService.addToDaskPolicy(applyDaskPolicyDto);
			if(addedApplyDaskPolicyDto!=null)
			{		
				model.addAttribute("policy", addedApplyDaskPolicyDto);
			    return "policy/dask/checkout";
			}			
		}
		model.addAttribute("message","Hatalı İşlem!");
		return "NotFound";
		
	}
	
	@PostMapping("/daskPolicies/checkout")
	public String checkoutToPolicy(@Valid @ModelAttribute("applyDaskPolicyDto") ApplyDaskPolicyDto applyDaskPolicyDto,
			                    BindingResult result,
			                    Model model)
	{
		if(!result.hasErrors())
		{
			DaskPolicyDto checkoutDaskPolicyDto = daskPolicyService.checkoutToDaskPolicy(applyDaskPolicyDto);
			if(checkoutDaskPolicyDto!=null)
			{			
			    return "redirect:/buildings/getAll";
			}
		}
		
		model.addAttribute(applyDaskPolicyDto);
		return "/policy/dask/checkout";
	}
	
	@GetMapping("/daskPolicies/checkout/{daskPolicyId}")
	public String goToDaskCheckout(@PathVariable int daskPolicyId,Model model)
	{
		DaskPolicyDto daskPolicyDto = daskPolicyService.getDaskPolicyById(daskPolicyId);
		if(daskPolicyDto!=null)
		{
			ApplyDaskPolicyDto applyDaskPolicyDto = new ApplyDaskPolicyDto();
			applyDaskPolicyDto.setAdress(daskPolicyDto.getBuilding().getAdress());
			applyDaskPolicyDto.setNationalIdentity(daskPolicyDto.getBuilding().getCustomer().getNationalIdentity());
			applyDaskPolicyDto.setStartDate(daskPolicyDto.getStartDate());
			applyDaskPolicyDto.setEndDate(daskPolicyDto.getEndDate());
			applyDaskPolicyDto.setPrice(daskPolicyDto.getPrice());
			applyDaskPolicyDto.setPolicyTypeId(daskPolicyDto.getPolicyType().getId());
			applyDaskPolicyDto.setDaskPolicyId(daskPolicyDto.getId());
					
			model.addAttribute("policy", applyDaskPolicyDto);
		    return "policy/dask/checkout";					
		}
		model.addAttribute("message","Hatalı İşlem!");
		return "NotFound";
		
	}
	
	@GetMapping("/daskPolicies/checkout/delete/{daskPolicyId}")
	public String deleteDaskPolicyFromCheckout(@PathVariable int daskPolicyId,Model model)
	{
		DaskPolicyDto daskPolicyDto = daskPolicyService.getDaskPolicyById(daskPolicyId);
		if(daskPolicyDto!=null)
		{
			daskPolicyService.deleteDaskPolicy(daskPolicyId);
			return "redirect:/buildings/getAll";
		}
		model.addAttribute("message","Hatalı İşlem!");
		return "NotFound";
		
	}
	
	
	
	
	
}
