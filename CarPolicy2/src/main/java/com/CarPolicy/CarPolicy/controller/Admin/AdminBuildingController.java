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
import com.CarPolicy.CarPolicy.dtos.CityDto;
import com.CarPolicy.CarPolicy.dtos.DaskPolicyDto;
import com.CarPolicy.CarPolicy.dtos.PolicyTypeDto;
import com.CarPolicy.CarPolicy.entities.Building;
import com.CarPolicy.CarPolicy.entities.Car;
import com.CarPolicy.CarPolicy.entities.City;
import com.CarPolicy.CarPolicy.entities.DaskPolicy;
import com.CarPolicy.CarPolicy.services.BuildingService;
import com.CarPolicy.CarPolicy.services.CityService;

@Controller
public class AdminBuildingController {

	private BuildingService buildingService;
	private CityService cityService;
	private ModelMapper modelMapper;
	public AdminBuildingController(ModelMapper modelMapper,BuildingService buildingService,CityService cityService) {
		super();
		this.buildingService = buildingService;
		this.cityService = cityService;
		this.modelMapper = modelMapper;
	}
	
	
	@GetMapping("/admin/buildings/getAll/{currentPage}")
	public String getAllBuildings(@PathVariable int currentPage, Model model)
	{
		Page<Building> buildingPage = buildingService.getBuildingsByPageNumber(currentPage);
		int totalPages = buildingPage.getTotalPages();
		long totalItems = buildingPage.getTotalElements();
		List<Building> buildings = buildingPage.getContent();
		
		List<BuildingDto> buildingDtos = buildings.stream()
        		.map(building -> modelMapper.map(building, BuildingDto.class))
        		.collect(Collectors.toList());
		
		
		model.addAttribute("buildings",buildingDtos);
		model.addAttribute("currentPage",currentPage);
		model.addAttribute("totalPages",totalPages);
	    model.addAttribute("totalItems",totalItems);
	    
	     
	   return "/admin/buildings/index";
	}
	
	@GetMapping("/admin/buildings/search/{pageNumber}")
	public String searchBuildingsByPolicy(@RequestParam(value="pageSize", defaultValue="5", required=false) int pageSize,
            @RequestParam(value="sortBy", defaultValue="adress", required=false) String sortBy,
            @RequestParam(value="sortDir", defaultValue="asc", required=false) String sortDir,
            @RequestParam("query") String query,
            Model model,
            @PathVariable int pageNumber)
     {
		Page<Building> buildingPage = buildingService.searchBuilding(query, pageSize, pageNumber, sortBy, sortDir);
	    int totalPages = buildingPage.getTotalPages();
	    long totalItems = buildingPage.getTotalElements();
        
       
        List<Building> buildings = buildingPage.getContent();
        
        List<BuildingDto> buildingDtos = buildings.stream()
        		.map(building -> modelMapper.map(building, BuildingDto.class))
        		.collect(Collectors.toList());
        
        
        model.addAttribute("currentPage",pageNumber);
        model.addAttribute("totalPages",totalPages);
        model.addAttribute("totalItems",totalItems);
		model.addAttribute("buildings",buildingDtos);
		model.addAttribute("query",query);
		
		model.addAttribute("pageSize",pageSize);
	    model.addAttribute("sortBy",sortBy);
	    model.addAttribute("sortDir",sortDir);
		
		return "/admin/buildings/search";
	}

	
	
	
	@GetMapping("/admin/buildings/update/{buildingId}")
	public String updateBuilding(@PathVariable int buildingId,
			                     Model model)
	{
		BuildingDto buildingDto = buildingService.getBuildingById(buildingId);
		if(buildingDto!=null)
		{
			List<City> cities = cityService.getAllCities();
	        
			model.addAttribute("building",buildingDto);
			model.addAttribute("cities",cities);
			return "/admin/buildings/update";
		}
		model.addAttribute("message","Building Not Found For buildingId_"+buildingId);
		return "NotFound";
	}
	
	@PostMapping("/admin/buildings/update/{buildingId}")
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
				return "redirect:/admin/buildings/getAll/1";
			}
		}
		List<City> cities = cityService.getAllCities();
		City city = cityService.getCityById(buildingDto.getCityId());
		
		buildingDto.setCity(city);
		model.addAttribute("cities",cities);
		model.addAttribute("building",buildingDto);
		return "/admin/buildings/update";
	}
	
	@GetMapping("admin/buildings/delete/{buildingId}")
	public String deleteBuilding(@PathVariable int buildingId)
	{
		buildingService.deleteBuilding(buildingId);
		return "redirect:/admin/buildings/getAll/1";
	}
	
	@GetMapping("admin/building/getById/{buildingId}")
	public String getBuilding(@PathVariable int buildingId,
			                  Model model)
	{
		BuildingDto buildingDto = buildingService.getBuildingById(buildingId);
		if(buildingDto!=null)
		{
			 model.addAttribute("building", buildingDto);
			 return "/admin/buildings/detail";
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
}
