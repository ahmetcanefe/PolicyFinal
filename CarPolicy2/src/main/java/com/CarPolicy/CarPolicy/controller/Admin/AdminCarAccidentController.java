package com.CarPolicy.CarPolicy.controller.Admin;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.CarPolicy.CarPolicy.dtos.CarAccidentDto;
import com.CarPolicy.CarPolicy.dtos.CarDto;
import com.CarPolicy.CarPolicy.services.CarAccidentService;
import com.CarPolicy.CarPolicy.services.CarService;

@Controller
public class AdminCarAccidentController {

	private CarAccidentService carAccidentService;
	private CarService carService;
	
	public AdminCarAccidentController(CarAccidentService carAccidentService, CarService carService) {
		super();
		this.carAccidentService = carAccidentService;
		this.carService = carService;
	}
	
	
	@GetMapping("/admin/{carId}/carAccidents/get")
	public String getCarAccidents(@PathVariable int carId ,Model model)
	{
		List<CarAccidentDto> carAccidentDtos = carAccidentService.getAllByCarId(carId);
	    model.addAttribute("carAccidents",carAccidentDtos);
	    model.addAttribute("carId",carId);
	    return "admin/carAccidents/index"; 
	}
	
	@GetMapping("/admin/carAccidents/add/{carId}")
	public String addCarAccident(@PathVariable int carId, Model model)
	{
		CarDto carDto = carService.getCarById(carId);
		if(carDto!=null)
		{
			CarAccidentDto carAccidentDto = new CarAccidentDto();
			carAccidentDto.carId=carId;
			model.addAttribute("carAccident", carAccidentDto);
			return "admin/carAccidents/add";
		}
		model.addAttribute("message","Car Not Found for carId"+carId);
		return "NotFound";
		
	}
	
	@PostMapping("/admin/carAccidents/add/{carId}")
	public String addCarAccident(@PathVariable int carId,
			                    @Valid @ModelAttribute("carAccident") CarAccidentDto carAccidentDto,
			                    BindingResult result,
			                    Model model)
	{
		if(!result.hasErrors())
		{
			carAccidentService.addCarAccidentByAdmin(carId,carAccidentDto);
			return "redirect:/admin/"+carId+"/carAccidents/get";
		}
		
		carAccidentDto.carId=carId;
		model.addAttribute("carAccident", carAccidentDto);
		return "admin/carAccidents/add";
	}
	
	@GetMapping("/admin/{carId}/carAccidents/update/{carAccidentId}")
	public String updateCarAccident(@PathVariable("carId") int carId,@PathVariable("carAccidentId") int carAccidentId ,Model model)
	{
		CarAccidentDto carAccidentDto = carAccidentService.getById(carId, carAccidentId);
		if(carAccidentDto!=null)
		{
			carAccidentDto.setCarId(carId);
			model.addAttribute("carAccident",carAccidentDto);
			return "admin/carAccidents/update";
		}
		model.addAttribute("message","carAccident Not Found For carAccidentId"+carAccidentId);
		return "NotFound";
	}
	
	@PostMapping("/admin/{carId}/carAccidents/update/{carAccidentId}")
	public String updateCarAccident(@PathVariable("carId") int carId,@PathVariable("carAccidentId") int carAccidentId,
			                        @Valid @ModelAttribute("carAccident") CarAccidentDto carAccidentDto,
			                        BindingResult result,
			                        Model model)
	{
		if(!result.hasErrors())
		{
			CarAccidentDto updatedCarAccidentDto = carAccidentService.updateCarAccident(carId, carAccidentId, carAccidentDto);
			if(updatedCarAccidentDto!=null)
			{
				return "redirect:/admin/"+carId+"/carAccidents/get";
			}
		}
		carAccidentDto.setCarId(carId);
		carAccidentDto.setId(carAccidentId);
		model.addAttribute("carAccident",carAccidentDto);
		return "admin/carAccidents/update";
	}
	
	@GetMapping("/admin/{carId}/carAccidents/delete/{carAccidentId}")
	public String deleteCarAccident(@PathVariable("carId") int carId, @PathVariable("carAccidentId") int carAccidentId)
	{
		carAccidentService.deleteCarAccident(carId, carAccidentId);
		return "redirect:/admin/"+carId+"/carAccidents/get";
	}

	@GetMapping("/admin/history")
	public String getAdminHistory(Model model)
	{
        List<CarAccidentDto> carAccidentDtos = carAccidentService.getAllCarAccidentByNonActive(); 
		
	    model.addAttribute("carAccidents",carAccidentDtos);
		return "admin/carAccidents/history";
	}
	
	@GetMapping("/admin/approve/{carAccidentId}")
	public String approveCar(@PathVariable int carAccidentId)
	{
		CarAccidentDto carAccidentDto = carAccidentService.approveCar(carAccidentId);
	    return "redirect:/admin/history";
	}
	
	@GetMapping("/admin/disapprove/{carAccidentId}")
	public String disapproveCar(@PathVariable int carAccidentId)
	{
		carAccidentService.disApproveCar(carAccidentId);
		return "redirect:/admin/history";
	}
	
	
	
	
}
