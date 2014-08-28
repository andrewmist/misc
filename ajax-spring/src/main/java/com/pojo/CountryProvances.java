package com.pojo;

import java.util.ArrayList;

public class CountryProvances {
	public Country country;
	public ArrayList<Provance> provances = new ArrayList<Provance>();
	
	public CountryProvances (Country c){
		this.country = c;
	}
}
