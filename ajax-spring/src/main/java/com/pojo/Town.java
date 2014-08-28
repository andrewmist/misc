package com.pojo;

import org.apache.commons.lang3.StringUtils;

public class Town {
	public int country_id;
	public int provance_id;
	public int town_id;
	public String town_name;
	public long population;
	
	public Town(String countryId, String provanceId, String townId){
		super();
		this.country_id = Integer.parseInt(countryId);
		this.provance_id = Integer.parseInt(provanceId);
		this.town_id = StringUtils.isEmpty(townId) ? -1 : Integer.parseInt(townId);
	}
	
	public int getCountry_id() {
		return country_id;
	}
	
	public int getProvance_id() {
		return provance_id;
	}

	public int getTown_id() {
		return town_id;
	}

	public String getTown_name() {
		return town_name;
	}

	public long getPopulation(){
		return population;
	}
	
	public String toString(){
		return "T: " + town_name + " [" + town_id + "], part of C: " +  country_id + ", P:" + provance_id + ", PPL: " + population; 
	}
	
}
