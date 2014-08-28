package com.andrew.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.pojo.Country;
import com.pojo.Provance;
import com.pojo.Town;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
//ALTER SCHEMA schema1 RENAME TO HABITATE;

public class CountryProvanceTownDaoImpl_bck implements CountryProvanceTownInterface {
	private static Logger log = Logger.getLogger("CountryProvanceTownDaoImpl");
	private JdbcTemplate jdbcTemplate = null;
	private NamedParameterJdbcTemplate jdbcTemplate2 = null;
	private TransactionTemplate transactionTemplate = null;
	private static String DIV = "_"; //am replace this w. JSON
	
	public void finalize(){
		//am free resources?
	}
	
	public void setDataSource(DataSource ds){
		log.debug("Setting Ds...");
		log.debug("Ds is Set!");
		//TODO: replace jdbcTemplate w. jdbcTemplate2? 
		jdbcTemplate = new JdbcTemplate(ds);
		jdbcTemplate2 = new NamedParameterJdbcTemplate(ds);
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(ds);
		transactionTemplate = new TransactionTemplate(transactionManager);
		
		try{
			log.debug("Datasource: " + ds.getConnection().getMetaData().getURL() + " set OK");
		}catch(Exception e){
			log.error(e);
		}
	}
	
	public List<Country> getAllCountries() {
		String sql = "select * from HABITATE.country";
		List<Country> rslt = new ArrayList<Country>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (@SuppressWarnings("rawtypes") Map row : rows) {
			int id = Integer.parseInt(String.valueOf(row.get("country_id")));
			String name = String.valueOf(row.get("name"));
		    rslt.add(new Country(id, name));
		}
		
		log.debug("# of Countries: " + rslt.size() + " " + ArrayUtils.toString(rslt));
		return rslt;
	}

	public List<Provance> getAllProvances() {
		String sql = "select * from HABITATE.provance order by country_id, provance_id";
		List<Provance> rslt = new ArrayList<Provance>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (@SuppressWarnings("rawtypes") Map row : rows) {
			int id = Integer.parseInt(String.valueOf(row.get("provance_id")));
			String name = String.valueOf(row.get("provance"));
			Provance p = new Provance(id, name); 
			p.country_id = Integer.parseInt(String.valueOf(row.get("country_id")));
		    rslt.add(p);		    
		}
		
		log.debug("# of Provances: " + rslt.size() + " " + ArrayUtils.toString(rslt));
		return rslt;
	}

	public List<Town> getTowns(int countryId, int provanceId) {
		String sql = "select * from HABITATE.town where country_id = ? and provance_id = ? order by town_name";
		Object prms [] = {countryId, provanceId};
		List <Town> rslt = jdbcTemplate.query(sql, prms, new RowMapper<Town>(){
			public Town mapRow(ResultSet rs, int rowNum) throws SQLException {
				Town town = new Town(rs.getString("country_id"), rs.getString("provance_id"), rs.getString("town_id"));
				town.town_name = rs.getString("town_name");
				town.population = rs.getLong("population");
				return town;
			}
		});
		
		log.debug("# of Towns: " + rslt.size() + " by : " + ArrayUtils.toString(prms));
		return rslt;
	}
	
	public Town deleteTown(Town town) {
        String sql = "delete from HABITATE.town where country_id=? and provance_id=? and town_id=?";		
        int i = jdbcTemplate.update(sql, new Object[] { town.country_id, town.provance_id, town.town_id});
        log.debug(i + " Town: " + town + " just deleted");
        return town;
	}

	public Town setTown(Town town) {
		String sql = "select max (town_id) + 1 from HABITATE.town";
		
		if(town.town_id < 1){ //INSERT
			town.town_id = jdbcTemplate.queryForObject(sql, Integer.class);
			sql = "insert into HABITATE.town (town_id, town_name, population, country_id, provance_id) values (?,?,?,?,?)";
			jdbcTemplate.update(sql, new Object[] { town.town_id, town.town_name, town.population, town.country_id, town.provance_id});
			log.info(town + " - INSERTED");
		}else{ //UPDATE
			sql = "update HABITATE.town set town_name = ?, population = ? where country_id=? and provance_id=? and town_id=?";
	        jdbcTemplate.update(sql, new Object[] { town.town_name, town.population, town.country_id, town.provance_id, town.town_id});
			log.info(town + " - UPDATED");
		}
		
		return town;
	}
		
	public List<Provance> getProvances(Country c) {
		String sql = "select * from HABITATE.provance where country_id = " + c.country_id;
		List<Provance> rslt = new ArrayList<Provance>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		log.debug("Provances rows: " + rows.size());
		
		for (@SuppressWarnings("rawtypes") Map row : rows) {
			Provance p = new Provance(Integer.parseInt(String.valueOf(row.get("provance_id"))), String.valueOf(row.get("provance")));
			p.country_id = Integer.parseInt(String.valueOf(row.get("country_id")));
			rslt.add(p);
		}
		
		log.debug("# of Provances: " + rslt.size() + " by Country: " + c);
		return rslt;
	}

	public Country setCountry(Country rslt) {
		String sql = "select max (country_id) + 1 from HABITATE.country";
		log.debug("Asked to manage: " + rslt);
		
		if(rslt.country_id < 1){ //INSERT
			rslt.country_id = jdbcTemplate.queryForObject(sql, Integer.class);
			sql = "insert into HABITATE.country (country_id, name) values (?,?)";
			jdbcTemplate.update(sql, new Object[] { rslt.country_id, rslt.country_name});
			log.info(rslt + " - INSERTED");
		}else{ //UPDATE (never tested)
			sql = "update HABITATE.country set name = ? where country_id = ?";
	        jdbcTemplate.update(sql, new Object[] { rslt.country_id});
			log.info(rslt + " - UPDATED");
		}
		
		return rslt;
	}

	//am for now: insert into TblProvance and link Country w. Provance
	public Provance setProvance(Provance rslt) {
		String sql = "select COALESCE(max (provance_id) + 1, 1) as p_id from HABITATE.provance where country_id = " + rslt.country_id;
		log.debug("Asked to link Provance and Country: " + rslt);
		
		if(rslt.provance_id < 1){ //INSERT PROVANCE
			rslt.provance_id = jdbcTemplate.queryForObject(sql, Integer.class);
			sql = "insert into HABITATE.provance (provance_id, provance, country_id) values (?,?,?)";
			jdbcTemplate.update(sql, new Object[] { rslt.provance_id, rslt.provance, rslt.country_id});
			log.info("Provance: " + rslt + " - INSERTED");
		}else{ //UPDATE (never tested)
			sql = "update HABITATE.provance set provance = ? where provance_id = ? and country_id = ?";
	        jdbcTemplate.update(sql, new Object[] { rslt.provance_id, rslt.country_id});
			log.info("Provance: " + rslt + " - UPDATED");
		}
		
		return rslt;
	}

	public List<Country> deleteCountries(String askingIds[]) {
		log.debug("Asking to Delete Countries: " +  ArrayUtils.toString(askingIds));
		List <String> listOfIds = Arrays.asList(askingIds);		
		List <Country> rslt = new ArrayList<Country>(askingIds.length);

		String sql_1 = "select c.country_id, c.name as country_name, count(p.provance_id) as provances_cnt from HABITATE.country c" 
		+" left outer join HABITATE.provance p on p.country_id = c.country_id"
		+" where country_id in (:ids)"
		+" group by c.country_id, country_name" 
		+" order by country_name";

		List <Country> askingToDelele = jdbcTemplate2.query(sql_1, Collections.singletonMap("ids", listOfIds), new RowMapper<Country>(){
				public Country mapRow(ResultSet rs, int rowNum) throws SQLException {
					Country country = new Country(rs.getInt("country_id"), rs.getString("country_name"));
					country.provances_cnt = rs.getInt("provances_cnt");
					return country;
				}
			});

		log.debug("Asking to DELETE: " + ArrayUtils.toString(askingToDelele));
		
		for (Country country : askingToDelele){
			if (country.provances_cnt == 0){
				rslt.add(country);
			}
		}		
		log.debug("Going to DELETE: " + ArrayUtils.toString(rslt));
		
		String sql_2 = "delete from HABITATE.country where country_id = ?";		
		for (Country country : rslt){
			jdbcTemplate.update(sql_2, new Object[] {country.country_id});
		}		
		
		log.info("DELETED: " + ArrayUtils.toString(rslt));
		return rslt;
	}
	
	//am Deleting Provinces with unlinking Country if no Towns are attached to Province
	//use JSON? As of now - array of triplets: CountryId_ProvanceId_ProvanceName ("_" is a bad choice for divider!)
	public List<Provance> deleteProvinces(String askingCountryProvIds[]) {
		log.debug("Asking to Delete Countries/Provances: " +  ArrayUtils.toString(askingCountryProvIds));
		List <String> listOfTriplets = Arrays.asList(askingCountryProvIds);
		List<Provance> rslt = new ArrayList<Provance>(listOfTriplets.size());

		String sql_1 = "delete from HABITATE.provance where "
				+ "not exists (select * from HABITATE.town where country_id =? and provance_id =?) "
				+ "and country_id =? and provance_id =?";
		
		//am loop through all pairs and delete Provinces w. no Towns
		for (String s: listOfTriplets){
			String ar [] = StringUtils.split(s, DIV);
			
			Provance p = new Provance(Integer.valueOf(ar[1]), ar[2]);
			p.country_id = Integer.valueOf(ar[0]);
			
			int rsltDelete = jdbcTemplate.update(sql_1, new Object[] {p.country_id, p.provance_id, p.country_id, p.provance_id});
			log.debug("DELETE Provice return: " + rsltDelete + " for: " + ArrayUtils.toString(ar));
			if (rsltDelete > 0){
				rslt.add(p);
			}
		}

		return rslt;
	}

	public String setProvinceToCountry(final String curCountryId, final String newCountryId, final String provinceId) {
		log.debug("Asking to reassign Country_Province: " + curCountryId + DIV + provinceId + " to: " + newCountryId + DIV + "?");
		
		final String sql_1 = "select max (provance_id) + 1 from HABITATE.provance where country_id =?";
		final String sql_2 = "update HABITATE.provance set country_id =?, provance_id =? where country_id =? and provance_id =?";
		final String sql_3 = "update HABITATE.town set country_id =?, provance_id =? where country_id =? and provance_id =?";
		
		//am would b nice to avoid gaps in Id
		int newProvanceId = (Integer)transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {
            	int rslt = jdbcTemplate.queryForObject(sql_1, new Object[] {newCountryId}, Integer.class);
        		jdbcTemplate.update(sql_2, new Object[] {newCountryId, rslt, curCountryId, provinceId});
        		log.info("Province: " + provinceId + " from Country: " + curCountryId + " belongs to Country: " + newCountryId + " as Province: " + rslt);
        		jdbcTemplate.update(sql_3, new Object[] {newCountryId, rslt, curCountryId, provinceId});
        		log.info("Towns updated from : " + curCountryId + DIV + provinceId + " to: " + newCountryId + DIV + rslt);
                return rslt;
            }
        });
		
		log.info("Returning New Country_Province: " + newCountryId + DIV + newProvanceId + " from: " + curCountryId + DIV + provinceId);
		return ""+newProvanceId;
	}

	public Town deleteTown(String string) {
		// TODO Auto-generated method stub
		return null;
	}	
}