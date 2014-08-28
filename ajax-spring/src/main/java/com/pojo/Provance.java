package com.pojo;

public class Provance {
	public int country_id ;
	public int provance_id ;
	public String provance ;

	public Provance (int provance_id, String provance){
		this.provance_id = provance_id;
		this.provance = provance;
	}
	
	public String toString(){
		return "P: " + provance + " [" + provance_id + "] belongs to C: " + country_id; 
	}	
}
