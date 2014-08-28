package com.andrew.jdbc;

import java.util.List;

import com.pojo.Country;
import com.pojo.Town;
import com.pojo.Provance;

public interface CountryProvanceTownInterface {
	public List <Country> getAllCountries(); //lookup
	public List <Provance> getAllProvances();
	public List <Provance> getProvances(Country c);
	public List <Town> getTowns(int countryId, int provanceId);
	public Town setTown(Town town); //insert/update
	
	public Country setCountry(Country rslt); //insert/update
	public Provance setProvance(Provance rslt); //insert/update
	public List<Country> deleteCountries(String askingIds[]);
	public List<Provance> deleteProvinces(String askingCountryProvIds[]);
	public String setProvinceToCountry(String curCountryId, String newCountryId, String provinceId);
	public Town deleteTown(String string);
}
