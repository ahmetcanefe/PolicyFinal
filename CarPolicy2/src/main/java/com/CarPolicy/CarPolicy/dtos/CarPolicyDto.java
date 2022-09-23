package com.CarPolicy.CarPolicy.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarPolicyDto {
	
	    private int id;	
	   
	    private double price;
		
		private Date startDate;
		
		private Date endDate; 
		
		private boolean isActive = true;
	    
		private CarDto car;
		
		private PolicyTypeDto policyType;
}
