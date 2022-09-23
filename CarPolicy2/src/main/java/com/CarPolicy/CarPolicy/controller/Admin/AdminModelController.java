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

import com.CarPolicy.CarPolicy.dtos.ModelDto;
import com.CarPolicy.CarPolicy.services.ModelService;

@Controller
public class AdminModelController {

	private ModelService modelService;

	public AdminModelController(ModelService modelService) {
		super();
		this.modelService = modelService;
	}

	
	@GetMapping("admin/models/getAll")
    public String getAllModel(Model model)
    {
    	List<ModelDto> modelDtos = modelService.getAllModels();
    	model.addAttribute("models",modelDtos);
    	return "admin/models/index";
    }

    @GetMapping("admin/models/add")
    public String addModel(Model model)
    {
    	com.CarPolicy.CarPolicy.entities.Model newModel = new com.CarPolicy.CarPolicy.entities.Model();
		model.addAttribute("model",newModel);
		return "admin/models/add";
    } 
	
    @PostMapping("/admin/models/add")
	public String addModel(@Valid @ModelAttribute("model") ModelDto modelDto,
			                 BindingResult result,
			                 Model model)
	{
		if(!result.hasErrors())
		{
			ModelDto addedModelDto = modelService.addModel(modelDto);
			if(addedModelDto!=null)
			{
				return "redirect:/admin/models/getAll";
			}
		}
		model.addAttribute(modelDto);
		return "admin/models/add";
	}
	
    @GetMapping("/admin/models/update/{modelId}")
	public String updateModel(@PathVariable int modelId,
			                      Model model)
	{
		ModelDto modelDto = modelService.getById(modelId);
		if(modelDto!=null)
		{
			model.addAttribute("model",modelDto);
			return "/admin/models/update";
		}
		model.addAttribute("message","Model Not Found for modeId_"+modelId);
		return "NotFound";
	}
	
	@PostMapping("admin/models/update/{modelId}")
	public String updateModel(@PathVariable int modelId,
			                     @Valid @ModelAttribute("model") ModelDto modelDto,
			                     BindingResult result,
			                     Model model
			                     )
	{
		if(!result.hasErrors())
		{
			ModelDto updatedModel = modelService.updateModel(modelId,modelDto);
			if(updatedModel!=null)
			{
				return "redirect:/admin/models/getAll";
			}		
		}
		modelDto.setId(modelId);
		model.addAttribute(modelDto);
		return "/admin/models/update";
	}
}
