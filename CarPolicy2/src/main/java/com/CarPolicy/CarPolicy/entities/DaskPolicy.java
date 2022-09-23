package com.CarPolicy.CarPolicy.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="daskPolicies")
public class DaskPolicy {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(nullable=false)
    private double price;
    
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
	private Date startDate;
	
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
	private Date endDate;
    
    private boolean isActive = true;
	
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="policyType_id",nullable=false)
	private PolicyType policyType;

    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="building_id",nullable=false)
	private Building building;
}

