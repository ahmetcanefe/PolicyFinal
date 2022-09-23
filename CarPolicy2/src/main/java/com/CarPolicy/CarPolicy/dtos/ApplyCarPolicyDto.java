package com.CarPolicy.CarPolicy.dtos;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyCarPolicyDto {

	@NotEmpty()
	@Size(min=7, max=15)
	private String licensePlate;
	
	@NotEmpty()
	private String nationalIdentity;
	
	@DateTimeFormat(pattern = "yyy-MM-dd")
	private Date startDate;
	
    @DateTimeFormat(pattern = "yyy-MM-dd")
	private Date endDate;
    
    private double price;
    
    private int policyTypeId;
    
    private int policyId;
    
}
