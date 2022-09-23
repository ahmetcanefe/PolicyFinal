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

import com.CarPolicy.CarPolicy.dtos.CarDto;
import com.CarPolicy.CarPolicy.dtos.ModelDto;
import com.CarPolicy.CarPolicy.entities.Car;
import com.CarPolicy.CarPolicy.services.CarService;
import com.CarPolicy.CarPolicy.services.ModelService;

@Controller
public class AdminCarController {

	private CarService carService;
	private ModelMapper modelMapper;	
	private ModelService modelService;

	public AdminCarController(CarService carService, ModelMapper modelMapper, ModelService modelService) {
		super();
		this.carService = carService;
		this.modelMapper = modelMapper;
		this.modelService = modelService;
	}
	

	@GetMapping("admin/cars/getAll/{currentPage}")
	public String getAllCars(Model model,
			                 @PathVariable int currentPage)
	{
		Page<Car> pageCars = carService.getAllCarsByPageNumber(currentPage, 5);
	    int totalPages = pageCars.getTotalPages();
		long totalItems = pageCars.getTotalElements();
		List<Car> cars = pageCars.getContent();
		
		List<CarDto> carDtos = cars.stream().map(car -> modelMapper.map(car, CarDto.class)).collect(Collectors.toList());
		
		
		model.addAttribute("cars",carDtos);
		model.addAttribute("currentPage",currentPage);
		model.addAttribute("totalPages",totalPages);
	    model.addAttribute("totalItems",totalItems);
	    
	    //return "admin/base";
		return "admin/cars/index";	
	}
	
	@GetMapping("admin/cars/getAll/search/{currentPage}")
	public String searchCars(@RequestParam(value="pageSize", defaultValue="5", required=false) int pageSize,
			                 @RequestParam(value="sortBy", defaultValue="licensePlate", required=false) String sortBy,
			                 @RequestParam(value="sortDir", defaultValue="asc", required=false) String sortDir,
			                 @RequestParam("query") String query,
			                 Model model,
			                 @PathVariable int currentPage)
	{
		Page<Car> pageCars = carService.searchCar(query.toUpperCase(),pageSize, currentPage, sortBy, sortDir);
	    int totalPages = pageCars.getTotalPages();
		long totalItems = pageCars.getTotalElements();
		List<Car> cars = pageCars.getContent();
		
		List<CarDto> carDtos = cars.stream().map(car -> modelMapper.map(car, CarDto.class)).collect(Collectors.toList());
		
		
		model.addAttribute("cars",carDtos);
		model.addAttribute("currentPage",currentPage);
		model.addAttribute("totalPages",totalPages);
	    model.addAttribute("totalItems",totalItems);
	    model.addAttribute("query",query);
	    
	    model.addAttribute("pageSize",pageSize);
	    model.addAttribute("sortBy",sortBy);
	    model.addAttribute("sortDir",sortDir);
	    
		return "/admin/cars/search";	
	}
	
	@GetMapping("/admin/cars/getById/{carId}")
	public String getCarById(@PathVariable int carId, Model model)
	{
		CarDto carDto = carService.getCarById(carId);
		model.addAttribute("car",carDto);
		return "admin/cars/detail";
	}

	
	@GetMapping("admin/cars/update/{carId}")
	public String updateCar(@PathVariable int carId, Model model)
	{
		CarDto carDto = carService.getCarById(carId);
		if(carDto!=null)
		{
			List<ModelDto> modelDtos = modelService.getAllModels();
			
			model.addAttribute("car", carDto);
			model.addAttribute("models",modelDtos);
		    return "admin/cars/update";
		}
		model.addAttribute("message","Car is not found for carId_"+carId);
        return "NotFound";
		
	}
	
	@PostMapping("admin/cars/update/{carId}")
	public String updateCar(@PathVariable int carId,
			                @Valid @ModelAttribute("car") CarDto carDto,
                            BindingResult result,
                            Model model)
	{
		if(!result.hasErrors())
		{
			CarDto updatedCarDto = carService.updateCar(carId, carDto);
			if(updatedCarDto!=null)
			{
				return "redirect:/admin/cars/getAll/1";
			}
		}		
	    carDto.setId(carId);
	    carDto.setModel(modelService.getById(carDto.getModelId()));
	    List<ModelDto> modelDtos = modelService.getAllModels();
	    
	    model.addAttribute(carDto);
	    model.addAttribute("models",modelDtos);
	    return "admin/cars/update";
	}	
	
	@GetMapping("admin/cars/delete/{carId}")
	public String deleteCar(@PathVariable int carId)
	{
		carService.deleteCar(carId);
		return "redirect:/admin/cars/getAll/1";
	}
}
