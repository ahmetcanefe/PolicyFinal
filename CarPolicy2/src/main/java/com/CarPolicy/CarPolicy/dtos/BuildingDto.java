package com.CarPolicy.CarPolicy.dtos;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.CarPolicy.CarPolicy.entities.City;
import com.CarPolicy.CarPolicy.entities.Usage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildingDto {

    private int id;
	
    @NotEmpty()
    @Size(min=7, max=55)
	private String adress;
	
	@NotNull()
    @Min(value = 1930)
    @Max(value = 2022)
	private int constructionYear;
	
	@NotNull()
    @Min(value = 1)
    @Max(value = 100)
	private int numberOfFloors;
	
	@NotNull()
    @Min(value = 5)
    @Max(value = 500)
	private int area;
	
	private Usage usage;
	
    private City city;
    
    private int cityId;
	
	private CustomerDto customer;
	
	private List<DaskPolicyDto> daskPolicies = new ArrayList<>();
}
