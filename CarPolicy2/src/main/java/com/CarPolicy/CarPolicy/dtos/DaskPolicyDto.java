package com.CarPolicy.CarPolicy.dtos;

import java.util.Date;

import com.CarPolicy.CarPolicy.entities.PolicyType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaskPolicyDto {

    private int id;
	
    private double price;

	private Date startDate;
	
	private Date endDate;

    private boolean isActive = true;
	
	private PolicyType policyType;

	private BuildingDto building;
}
