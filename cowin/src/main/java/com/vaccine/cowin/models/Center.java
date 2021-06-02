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
public class Center {

	int center_id;
	String name;
	String address;
	String state_name;
	String district_name;
	String block_name;
	float pincode;
	float lat;
	String from;
	String to;
	String fee_type;
	ArrayList<Session> sessions;
}
