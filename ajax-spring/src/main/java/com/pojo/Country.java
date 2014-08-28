package com.pojo;

public class Country {
	public int country_id ;
	public String country_name ;
	//public List<Provance> provances = new ArrayList<Provance>();
	
	public String toString(){
		return "C: "+ country_name + " [" + country_id + "]"; 
	}

	public Country(int id, String name){
		super();
		this.country_id = id;
		this.country_name = name;
	}
	
	/*
	public void addProvance (Provance p){
		this.provances.add(p);
	}
	*/
	public int provances_cnt = 0;
}
