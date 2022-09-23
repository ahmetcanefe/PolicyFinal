package com.CarPolicy.CarPolicy.controller.Admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.CarPolicy.CarPolicy.dtos.CarPolicyDto;
import com.CarPolicy.CarPolicy.dtos.DaskPolicyDto;
import com.CarPolicy.CarPolicy.services.CarPolicyService;
import com.CarPolicy.CarPolicy.services.DaskPolicyService;

@Controller
public class AdminPolicyController {

	private CarPolicyService carPolicyService;
	private DaskPolicyService daskPolicyService;
	
	public AdminPolicyController(DaskPolicyService daskPolicyService,CarPolicyService carPolicyService) {
		super();
		this.carPolicyService = carPolicyService;
		this.daskPolicyService = daskPolicyService;
	}


	@GetMapping("admin/policies/deleteCarPolicy/{carPolicyId}")
	public String deleteCarPolicy(@PathVariable int carPolicyId,Model model)	                       
	{
		CarPolicyDto policyDto = carPolicyService.getPolicyById(carPolicyId);
		if(policyDto!=null)
		{
			carPolicyService.deletePolicy(carPolicyId);
			
			model.addAttribute("policy.id",carPolicyId);
			return "redirect:/admin/carPolicyTypes/getById/"+policyDto.getPolicyType().getId()+"/1";
		}
		return "NotFound";
	}
	
	@GetMapping("admin/policies/deleteDaskPolicy/{daskPolicyId}")
	public String deleteDaskPolicy(@PathVariable int daskPolicyId,Model model)	                       
	{
		DaskPolicyDto daskPolicyDto = daskPolicyService.getDaskPolicyById(daskPolicyId);
		if(daskPolicyDto!=null)
		{
			daskPolicyService.deleteDaskPolicy(daskPolicyId);
			
			model.addAttribute("policy.id",daskPolicyId);
			return "redirect:/admin/daskPolicyTypes/getById/"+daskPolicyDto.getPolicyType().getId()+"/1";
		}
		return "NotFound";
	}
	

	@GetMapping("/admin/cars/{carId}/policies")
	public String getpoliciesByCarId(@PathVariable int carId, Model model)
	{
		List<CarPolicyDto> policyDtos = carPolicyService.getAllByCarId(carId);
	    model.addAttribute("policies",policyDtos);
	    return "admin/policies/detail";
	}
	
	@GetMapping("/admin/buildings/{buildingId}/policies")
	public String getPoliciesByBuildingId(@PathVariable int buildingId, Model model)
	{
		List<DaskPolicyDto> daskPolicyDtos = daskPolicyService.getAllByBuildingId(buildingId);
		model.addAttribute("policies", daskPolicyDtos);
		return "admin/policies/Dask/detail";
	}
	
	
	
	
	
	
	
	
	
	
}
