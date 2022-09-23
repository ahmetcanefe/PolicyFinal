package com.CarPolicy.CarPolicy.controller;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.CarPolicy.CarPolicy.dtos.BuildingDto;
import com.CarPolicy.CarPolicy.dtos.CarDto;
import com.CarPolicy.CarPolicy.dtos.CityDto;
import com.CarPolicy.CarPolicy.entities.Building;
import com.CarPolicy.CarPolicy.entities.Car;
import com.CarPolicy.CarPolicy.entities.City;
import com.CarPolicy.CarPolicy.services.BuildingService;
import com.CarPolicy.CarPolicy.services.CityService;

@Controller
public class BuildingController {

	private BuildingService buildingService;
	private CityService cityService;

	public BuildingController(BuildingService buildingService, CityService cityService) {
		super();
		this.buildingService = buildingService;
		this.cityService = cityService;
	}
	
	@GetMapping("buildings/getAll")
	public String getAllBuildings(Model model)
	{
	    List<BuildingDto> buildingDtos = buildingService.getAllBuildingsByCustomer();	
	    model.addAttribute("buildings", buildingDtos);
	    return "/buildings/index";
	}
	
	@GetMapping("buildings/add")
	public String addBuilding(Model model)
	{
		BuildingDto buildingDto = new BuildingDto();
		List<City> cities = cityService.getAllCities();
		
		model.addAttribute("building",buildingDto);
		model.addAttribute("cities",cities);
		return "buildings/add";
	}
	
	@PostMapping("buildings/add")
	public String addBuilding(@Valid @ModelAttribute("building") BuildingDto buildingDto,
			                  BindingResult result,
			                  Model model)
	{
		if(!result.hasErrors())
		{
			Building existingBuilding = buildingService.getByAdressAndCustomerId(buildingDto.getAdress());
			if(existingBuilding!=null) {
				result.rejectValue("adress", null, "There is already a building with same address and same User");
			}
			else 
			{
				BuildingDto addedBuildingDto = buildingService.addBuilding(buildingDto);
				if(addedBuildingDto!=null)
				{
					return "redirect:/buildings/getAll";
				}	
			}    
		}
		List<City> cities = cityService.getAllCities();
		
		model.addAttribute("building",buildingDto);
		model.addAttribute("cities",cities);
		return "buildings/add";
	}
	
	@GetMapping("/buildings/update/{buildingId}")
	public String updateBuilding(@PathVariable int buildingId,
			                     Model model)
	{
		BuildingDto buildingDto = buildingService.getBuildingById(buildingId);
		if(buildingDto!=null)
		{
			List<City> cities = cityService.getAllCities();
	        
			model.addAttribute("building",buildingDto);
			model.addAttribute("cities",cities);
			return "buildings/update";
		}
		model.addAttribute("message","Building Not Found For buildingId_"+buildingId);
		return "NotFound";
	}
	
	@PostMapping("/buildings/update/{buildingId}")
	public String updateBuilding(@PathVariable int buildingId,
			                     @Valid @ModelAttribute("building") BuildingDto buildingDto,
			                     BindingResult result,
			                     Model model)
	{
		if(!result.hasErrors())
		{
			BuildingDto updatedBuildingDto = buildingService.updateBuilding(buildingId, buildingDto);
			if(updatedBuildingDto!=null)
			{
				return "redirect:/buildings/getAll";
			}
		}
		List<City> cities = cityService.getAllCities();
		City city = cityService.getCityById(buildingDto.getCityId());
		
		buildingDto.setCity(city);
		model.addAttribute("cities",cities);
		model.addAttribute("building",buildingDto);
		return "buildings/update";
	}
	
	@GetMapping("buildings/delete/{buildingId}")
	public String deleteBuilding(@PathVariable int buildingId)
	{
		buildingService.deleteBuilding(buildingId);
		return "redirect:/buildings/getAll";
	}
	
	@GetMapping("building/getById/{buildingId}")
	public String getBuilding(@PathVariable int buildingId,
			                  Model model)
	{
		BuildingDto buildingDto = buildingService.getBuildingById(buildingId);
		if(buildingDto!=null)
		{
			 model.addAttribute("building", buildingDto);
			 return "buildings/detail";
		}
		return null;
	}
}
