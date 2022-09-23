package com.CarPolicy.CarPolicy.controller.Admin;

import java.util.ArrayList;
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
import com.CarPolicy.CarPolicy.dtos.CustomerDto;
import com.CarPolicy.CarPolicy.entities.City;
import com.CarPolicy.CarPolicy.entities.Customer;
import com.CarPolicy.CarPolicy.entities.Role;
import com.CarPolicy.CarPolicy.services.CarService;
import com.CarPolicy.CarPolicy.services.CityService;
import com.CarPolicy.CarPolicy.services.CustomerService;
import com.CarPolicy.CarPolicy.services.RoleService;

@Controller
public class AdminCustomerController {

	private CustomerService customerService;
	private CarService carService;
	private ModelMapper modelMapper;
	private CityService cityService;	
	private RoleService roleService;

	public AdminCustomerController(RoleService roleService,CustomerService customerService, CarService carService, ModelMapper modelMapper,
			CityService cityService) {
		super();
		this.customerService = customerService;
		this.carService = carService;
		this.modelMapper = modelMapper;
		this.cityService = cityService;
		this.roleService = roleService;
	}

	
	@GetMapping("/admin/costumers/getAll/{currentPage}")
	public String getAllCostumers(@PathVariable int currentPage, Model model)
	{
		Page<Customer> pageCustomers = customerService.getAllCustomersByPageNumber(currentPage);
	    int totalPages = pageCustomers.getTotalPages();
	    long totalItems = pageCustomers.getTotalElements();
	    List<Customer> customers = pageCustomers.getContent();
		
		List<CustomerDto> customerDtos = customers.stream()
				                             .map(customer -> modelMapper.map(customer, CustomerDto.class))
				                             .collect(Collectors.toList());
		
		model.addAttribute("customers",customerDtos);
		model.addAttribute("currentPage",currentPage);
	    model.addAttribute("totalPages",totalPages);
	    model.addAttribute("totalItems",totalItems);
	
	    return "/admin/customers/index";
	}
	
	@GetMapping("/admin/costumers/getAll/search/{currentPage}")
	public String searchCustomers(@RequestParam(value="pageSize", defaultValue="5", required=false) int pageSize,
            @RequestParam(value="sortBy", defaultValue="id", required=false) String sortBy,
            @RequestParam(value="sortDir", defaultValue="asc", required=false) String sortDir,
            @RequestParam("query") String query,
            Model model,
            @PathVariable int currentPage)
	{
		Page<Customer> pageCustomers = customerService.searchCustomer( query,  pageSize,  currentPage,  sortBy,  sortDir);
	    int totalPages = pageCustomers.getTotalPages();
	    long totalItems = pageCustomers.getTotalElements();
	    
	    Role role = roleService.getByName("ROLE_ADMIN");
	    
	    List<Customer> availableCustomers = new ArrayList<>();
	    List<Customer> customers = pageCustomers.getContent();
	    
	    for(Customer customer : customers)
	    {
	    	if(!customer.getRoles().contains(role))
	    	{
	    		availableCustomers.add(customer);
	    	}
	    }
		
		List<CustomerDto> customerDtos = availableCustomers.stream()
				                             .map(customer -> modelMapper.map(customer, CustomerDto.class))
				                             .collect(Collectors.toList());
		
		model.addAttribute("customers",customerDtos);
		model.addAttribute("currentPage",currentPage);
	    model.addAttribute("totalPages",totalPages);
	    model.addAttribute("totalItems",totalItems);
	    model.addAttribute("query", query);
	    
	    model.addAttribute("pageSize",pageSize);
	    model.addAttribute("sortBy",sortBy);
	    model.addAttribute("sortDir",sortDir);
	
	    return "/admin/customers/search";
	}
	
	
	
	@GetMapping("/admin/customers/update/{customerId}")
	public String updateCustomer(@PathVariable int customerId,
			                      Model model)
	{
		CustomerDto customerDto = customerService.getCustomerById(customerId);
		if(customerDto!=null)
		{
			List<City> cities = cityService.getAllCities();
			model.addAttribute("cities",cities);
			model.addAttribute("customer",customerDto);
			return "/admin/customers/update";
		}
		model.addAttribute("message","Customer Not Found for customerId_"+customerId);
		return "NotFound";
	}
	
	@PostMapping("admin/customers/update/{customerId}")
	public String updateCustomer(@PathVariable int customerId,
			                     @Valid @ModelAttribute("customer") CustomerDto customerDto,
			                     BindingResult result,
			                     Model model
			                     )
	{
		if(!result.hasErrors())
		{
			CustomerDto updatedCustomer = customerService.updateCustomer(customerId, customerDto);
			if(updatedCustomer!=null)
			{
				return "redirect:/admin/costumers/getAll/1";
			}		
		}
		customerDto.setId(customerId);
		List<City> cities = cityService.getAllCities();
		model.addAttribute("cities",cities);
		model.addAttribute(customerDto);
		return "/admin/customers/update";
	}
	
	@GetMapping("admin/customers/delete/{customerId}")
	public String deleteCustomer(@PathVariable int customerId)
	{
		customerService.deleteCustomer(customerId);
		return "redirect:/admin/costumers/getAll/1";
	}
	
	@GetMapping("/admin/customers/getById/{customerId}")
	public String getCustomerById(@PathVariable int customerId,
			                       Model model)
	{
		CustomerDto customerDto = customerService.getCustomerById(customerId);
		model.addAttribute("customer",customerDto);
		return "/admin/customers/detail";
	}
	
	
}
