package com.vaccine.cowin.models;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Session {

	String session_id;
	String date;
	int available_capacity;
	int available_capacity_dose1;
	int available_capacity_dose2;
	int min_age_limit;
	String vaccine;
	ArrayList<String> slots;
	
}

