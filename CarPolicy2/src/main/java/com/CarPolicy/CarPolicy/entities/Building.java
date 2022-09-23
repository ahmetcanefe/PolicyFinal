package com.CarPolicy.CarPolicy.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="buildings")
@Getter
@Setter
public class Building {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(nullable=false)
	private String adress;
	
	@Column(nullable=false)
	private int constructionYear;
	
	@Column(nullable=false)
	private int numberOfFloors;
	
	@Column(nullable=false)
	private int area;
	
	@Column(nullable=false)
	private Usage usage;
	
	@OneToOne(cascade=CascadeType.DETACH)
    @JoinColumn(name = "city_id",nullable=false,referencedColumnName = "id")
    private City city;
	
	@ManyToOne
	@JoinColumn(name="customer_id")
	private Customer customer;
	
	@OneToMany(mappedBy="building", fetch = FetchType.LAZY)
	private List<DaskPolicy> daskPolicies = new ArrayList<>();
}
