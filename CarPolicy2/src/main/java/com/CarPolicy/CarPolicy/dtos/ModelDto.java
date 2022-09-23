package com.CarPolicy.CarPolicy.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelDto {

	private int id;
	
	@NotEmpty()
    @Size(min=3, max=30)
	private String name;
}
