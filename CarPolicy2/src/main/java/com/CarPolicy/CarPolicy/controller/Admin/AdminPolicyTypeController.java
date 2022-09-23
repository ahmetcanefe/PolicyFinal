package com.CarPolicy.CarPolicy.controller.Admin;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.CarPolicy.CarPolicy.dtos.BuildingDto;
import com.CarPolicy.CarPolicy.dtos.CarDto;
import com.CarPolicy.CarPolicy.dtos.CarPolicyDto;
import com.CarPolicy.CarPolicy.dtos.CustomerDto;
import com.CarPolicy.CarPolicy.dtos.DaskPolicyDto;
import com.CarPolicy.CarPolicy.dtos.PolicyTypeDto;
import com.CarPolicy.CarPolicy.entities.CarPolicy;
import com.CarPolicy.CarPolicy.entities.DaskPolicy;
import com.CarPolicy.CarPolicy.services.BuildingService;
import com.CarPolicy.CarPolicy.services.CarPolicyService;
import com.CarPolicy.CarPolicy.services.CarService;
import com.CarPolicy.CarPolicy.services.CustomerService;
import com.CarPolicy.CarPolicy.services.DaskPolicyService;
import com.CarPolicy.CarPolicy.services.PolicyTypeService;

@Controller
public class AdminPolicyTypeController {

	private PolicyTypeService policyTypeService;
	private CarPolicyService carPolicyService;
	private DaskPolicyService daskPolicyService;
	private ModelMapper modelMapper;
	private CarService carService;
	private CustomerService customerService;
	private BuildingService buildingService;

	public AdminPolicyTypeController(BuildingService buildingService,DaskPolicyService daskPolicyService,PolicyTypeService policyTypeService, CarPolicyService carPolicyService,
			ModelMapper modelMapper, CarService carService, CustomerService customerService) {
		super();
		this.policyTypeService = policyTypeService;
		this.carPolicyService = carPolicyService;
		this.modelMapper = modelMapper;
		this.carService = carService;
		this.customerService = customerService;
		this.daskPolicyService = daskPolicyService;
		this.buildingService = buildingService;
	}



	@GetMapping("/admin/policyTypes/getAll")
	public String getAllPolicyTypes(Model model)
	{
		List<PolicyTypeDto> policyTypeDtos = policyTypeService.getCarPolicyTypes();
		model.addAttribute("policies",policyTypeDtos);
		return "/admin/policyTypes/index";
	}
	
	
	
	@GetMapping("/admin/policyTypes/add")
	public String addPolicyType(Model model)
	{
		PolicyTypeDto policyTypeDto = new PolicyTypeDto();
		
		model.addAttribute("policyType", policyTypeDto);
		return "/admin/policyTypes/add";
	}
	
	@PostMapping("/admin/policyTypes/add")
	public String addPolicyType(@Valid @ModelAttribute("policyType") PolicyTypeDto policyTypeDto,
			                 BindingResult result,
			                 Model model)
	{
		if(!result.hasErrors())
		{
			PolicyTypeDto addedPolicyTypeDto = policyTypeService.addPolicyType(policyTypeDto);
			if(addedPolicyTypeDto!=null)
			{
				return "redirect:/admin/policyTypes/getAll";
			}
		}
		model.addAttribute(policyTypeDto);
		return "/admin/poliyTypes/add";
	}
	
	@GetMapping("/admin/policyTypes/update/{policyTypeId}")
	public String updatePolicyType(@PathVariable int policyTypeId,
			                   Model model)
	{
		PolicyTypeDto policyTypeDto = policyTypeService.getPolicyTypeById(policyTypeId);
		if(policyTypeDto!=null)
		{
			model.addAttribute("policyType",policyTypeDto);
			return "/admin/policyTypes/update";	
		}	
		model.addAttribute("message","PolicyType Not Found For policyTypeId_"+policyTypeId);
	    return "NotFound";
	}
	
	@PostMapping("/admin/policyTypes/update/{policyTypeId}")
	public String updatePolicyType(@PathVariable int policyTypeId,
			                   @Valid @ModelAttribute("policyType") PolicyTypeDto policyTypeDto,
			                   BindingResult result,
			                   Model model)
	{
		if(!result.hasErrors())
		{
			PolicyTypeDto updatedPolicyTypeDto = policyTypeService.updatePolicyType(policyTypeId,policyTypeDto);
			if(updatedPolicyTypeDto!=null)
			{
				return "redirect:/admin/policyTypes/getAll";
			}
		}
		policyTypeDto.setId(policyTypeId);
		model.addAttribute(policyTypeDto);
		return "/admin/policyTypes/update";
	}
	
	@GetMapping("/admin/policyTypes/delete/{policyTypeId}")
	public String deletePolicyType(@PathVariable int policyTypeId)
	{
		policyTypeService.deletePolicyType(policyTypeId);
		return "redirect:/admin/policyTypes/getAll";
	}
	
	@GetMapping("/admin/carPolicyTypes/getById/{policyTypeId}/{pageNumber}")
	public String getPolicyTypeById(@PathVariable("policyTypeId") int policyTypeId,
			                    @PathVariable("pageNumber") int currentPage,
			                    Model model)
	{
		PolicyTypeDto policyTypeDto = policyTypeService.getPolicyTypeById(policyTypeId);
		if(policyTypeDto!=null)
		{
            Page<CarPolicy> policyPage = carPolicyService.getAllPoliciesByPolicyTypeId(policyTypeId,currentPage);
			int totalPages = policyPage.getTotalPages();
            long totalItems = policyPage.getTotalElements();
            List<CarPolicy> policies = policyPage.getContent();
            
            List<CarPolicyDto> policyDtos = policies.stream()
	                   .map((policy) -> modelMapper.map(policy, CarPolicyDto.class))
	                   .collect(Collectors.toList());
            
            
            model.addAttribute("currentPage",currentPage);
            model.addAttribute("totalPages",totalPages);
            model.addAttribute("totalItems",totalItems);
			model.addAttribute("policies",policyDtos);
			model.addAttribute("policyType",policyTypeDto);
			return "/admin/policyTypes/detail";
		}
		model.addAttribute("message","PolicyType Not Found For policyTypeId_"+policyTypeId);
	    return "NotFound";
	}
	
	@GetMapping("admin/carPolicyTypes/getById/{policyId}/search/{pageNumber}")
	public String searchCarsByPolicy(@RequestParam(value="query") String query,@PathVariable("policyId") int policyTypeId,@PathVariable("pageNumber") int pageNumber,
            Model model)
     {
		PolicyTypeDto policyTypeDto = policyTypeService.getPolicyTypeById(policyTypeId);
		if(policyTypeDto!=null)
		{
		Page<CarPolicy> policyPage = carPolicyService.searchPoliciesByPolicyTypeId(policyTypeId, query.toUpperCase(), pageNumber);
	    int totalPages = policyPage.getTotalPages();
	    long totalItems = policyPage.getTotalElements();
        
       
        List<CarPolicy> policies = policyPage.getContent();
        
        List<CarPolicyDto> policyDtos = policies.stream()
                   .map((policy) -> modelMapper.map(policy, CarPolicyDto.class))
                   .collect(Collectors.toList());
        
        
        model.addAttribute("currentPage",pageNumber);
        model.addAttribute("totalPages",totalPages);
        model.addAttribute("totalItems",totalItems);
		model.addAttribute("policies",policyDtos);
		model.addAttribute("policyType",policyTypeDto);
		model.addAttribute("query",query);
		
		return "admin/policyTypes/search";
		}
		model.addAttribute("message","Policy Not Found For policyId_"+policyTypeId);
	    return "NotFound";
     }
	
	

	@GetMapping("admin/customers/{customerId}/cars/{carId}")
	public String getPolicyTypeMoreDetail(@PathVariable("customerId") int customerId, @PathVariable("carId") int carId, Model model)
	{
		CustomerDto customerDto = customerService.getCustomerById(customerId);
		CarDto carDto = carService.getCarById(carId);
		
		model.addAttribute("customer",customerDto);
		model.addAttribute("car",carDto);
		
		return "/admin/policyTypes/moreDetail";
	}
	
	
	
	@GetMapping("/admin/daskPolicyTypes/getAll")
	public String getAllDaskPolicyTypes(Model model)
	{
		List<PolicyTypeDto> policyTypeDtos = policyTypeService.getDaskPolicyTypes();
		model.addAttribute("policies",policyTypeDtos);
		return "/admin/policyTypes/Dask/index";
	}
	
	
	@GetMapping("/admin/daskPolicyTypes/getById/{policyTypeId}/{pageNumber}")
	public String getDaskPolicyTypeById(@PathVariable("policyTypeId") int policyTypeId,
			                    @PathVariable("pageNumber") int currentPage,
			                    Model model)
	{
		PolicyTypeDto policyTypeDto = policyTypeService.getPolicyTypeById(policyTypeId);
		if(policyTypeDto!=null)
		{
            Page<DaskPolicy> policyPage = daskPolicyService.getAllDaskPoliciesByPolicyTypeId(policyTypeId,currentPage);
			int totalPages = policyPage.getTotalPages();
            long totalItems = policyPage.getTotalElements();
            List<DaskPolicy> policies = policyPage.getContent();
            
            List<DaskPolicyDto> daskPolicyDtos = policies.stream()
	                   .map((daskPolicy) -> modelMapper.map(daskPolicy, DaskPolicyDto.class))
	                   .collect(Collectors.toList());
            
            
            model.addAttribute("currentPage",currentPage);
            model.addAttribute("totalPages",totalPages);
            model.addAttribute("totalItems",totalItems);
			model.addAttribute("policies",daskPolicyDtos);
			model.addAttribute("policyType",policyTypeDto);
			return "/admin/policyTypes/Dask/detail";
		}
		model.addAttribute("message","PolicyType Not Found For policyTypeId_"+policyTypeId);
	    return "NotFound";
	}
	
	@GetMapping("/admin/daskPolicyTypes/policies/{policyTypeId}/search/{pageNumber}")
	public String searchBuildingsByPolicy(@RequestParam(value="query") String query,@PathVariable("policyTypeId") int policyTypeId,@PathVariable("pageNumber") int pageNumber,
            Model model)
     {
		PolicyTypeDto policyTypeDto = policyTypeService.getPolicyTypeById(policyTypeId);
		if(policyTypeDto!=null)
		{
		Page<DaskPolicy> policyPage = daskPolicyService.searchDaskPoliciesByPolicyTypeId(policyTypeId, query, pageNumber);
	    int totalPages = policyPage.getTotalPages();
	    long totalItems = policyPage.getTotalElements();
        
       
        List<DaskPolicy> policies = policyPage.getContent();
        
        List<DaskPolicyDto> policyDtos = policies.stream()
                   .map((policy) -> modelMapper.map(policy, DaskPolicyDto.class))
                   .collect(Collectors.toList());
        
        
        model.addAttribute("currentPage",pageNumber);
        model.addAttribute("totalPages",totalPages);
        model.addAttribute("totalItems",totalItems);
		model.addAttribute("policies",policyDtos);
		model.addAttribute("policyType",policyTypeDto);
		model.addAttribute("query",query);
		
		return "/admin/policyTypes/Dask/search";
		}
		model.addAttribute("message","Policy Not Found For policyId_"+policyTypeId);
	    return "NotFound";
     }
	
	
	
	@GetMapping("admin/customers/{customerId}/buildings/{buildingId}")
	public String getDaskPolicyMoreDetail(@PathVariable("customerId") int customerId, @PathVariable("buildingId") int buildingId, Model model)
	{
		CustomerDto customerDto = customerService.getCustomerById(customerId);
		BuildingDto buildingDto = buildingService.getBuildingById(buildingId);
		
		model.addAttribute("customer",customerDto);
		model.addAttribute("building",buildingDto);
		
		return "/admin/policyTypes/Dask/moreDetail";
	}
	
	
	
	
	
	
	
	
}

