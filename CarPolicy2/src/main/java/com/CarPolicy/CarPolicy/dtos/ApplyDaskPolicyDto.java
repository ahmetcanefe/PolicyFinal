package com.CarPolicy.CarPolicy.dtos;

import java.util.Date;

import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;

import com.CarPolicy.CarPolicy.entities.PolicyType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyDaskPolicyDto {

	@NotEmpty()
	private String adress;
	
	@NotEmpty()
	private String nationalIdentity;
	
	@DateTimeFormat(pattern = "yyy-MM-dd")
	private Date startDate;
	
    @DateTimeFormat(pattern = "yyy-MM-dd")
	private Date endDate;
    
    private double price;
    
    private int policyTypeId;
    
    private int daskPolicyId;
}
