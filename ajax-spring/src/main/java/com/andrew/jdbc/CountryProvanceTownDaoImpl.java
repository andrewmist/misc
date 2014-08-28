package com.andrew.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
//ALTER SCHEMA schema1 RENAME TO HABITATE;

public class CountryProvanceTownDaoImpl implements CountryProvanceTownInterface {
	private static Logger log = Logger.getLogger("CountryProvanceTownDaoImpl");
	private JdbcTemplate jdbcTemplate = null;
	private NamedParameterJdbcTemplate jdbcTemplate2 = null;
	private TransactionTemplate transactionTemplate = null;
	private static String DIV = "_"; //am replace this w. JSON
	private DataSource ds;
	
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
			this.ds = ds;
		}catch(Exception e){
			log.error(e);
		}
	}
	
	//NEW
	public List<Country> getAllCountries() {
		String sql = "select * from HABITATE.country order by country_id";
		List<Country> rslt = new ArrayList<Country>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (@SuppressWarnings("rawtypes") Map row : rows) {
			int id = Integer.parseInt(String.valueOf(row.get("country_id")));
			String name = String.valueOf(row.get("country_name"));
		    rslt.add(new Country(id, name));
		}
		
		log.debug("# of Countries: " + rslt.size() + " " + ArrayUtils.toString(rslt));
		return rslt;
	}

	//NEW (getAllProvances uses same sql as agetProvances by Country)
	public List<Provance> getAllProvances() {
		final String sql = "select cp.id, p.*, c.* from HABITATE.country_province cp" 
				+ " join HABITATE.province p on p.province_id = cp.province_id" 
				+ " join HABITATE.country c on c.country_id = cp.country_id"
				+ " order by c.country_id, p.province_id";

		List<Provance> rslt = new ArrayList<Provance>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (@SuppressWarnings("rawtypes") Map row : rows) {
			int id = Integer.parseInt(String.valueOf(row.get("province_id")));
			String name = String.valueOf(row.get("province_name"));
			Provance p = new Provance(id, name); 
			p.country_id = Integer.parseInt(String.valueOf(row.get("country_id")));
		    rslt.add(p);		    
		}
		
		log.debug("# of Provances: " + rslt.size() + " " + ArrayUtils.toString(rslt));
		return rslt;
	}

	public List<Town> getTowns(final int countryId, final int provanceId) {
		String sql = "select cp.country_id, pt.province_id, t.* from HABITATE.town t"
				+ " join HABITATE.province_town pt on pt.town_id = t.town_id" 
				+ " join HABITATE.country_province cp on cp.province_id = pt.province_id" 
				+ " where cp.country_id = ? and cp.province_id =?"
				+ " order by t.town_name";
		
		Object prms [] = {countryId, provanceId};
		List <Town> rslt = jdbcTemplate.query(sql, prms, new RowMapper<Town>(){
			public Town mapRow(ResultSet rs, int rowNum) throws SQLException {
				//Town town = new Town(rs.getString("country_id"), rs.getString("province_id"), rs.getString("town_id"));
				Town town = new Town(""+countryId, ""+provanceId, rs.getString("town_id"));
				town.town_name = rs.getString("town_name");
				town.population = rs.getLong("population");
				return town;
			}
		});
		
		log.debug("# of Towns: " + rslt.size() + " by : " + ArrayUtils.toString(prms));
		return rslt;
	}
	
	public Town deleteTown(final String townId) {
		log.debug("Asked to delete Town by Id: " + townId);
		
		final String sql_1 = "select COALESCE(cp.country_id, -1) as country_id, COALESCE(pt.province_id, -1) as province_id, t.* from HABITATE.town t"
				+ " left outer join HABITATE.province_town pt on pt.town_id = t.town_id"
				+ " left outer join HABITATE.country_province cp on cp.province_id = pt.province_id"
				+ " where t.town_id = ?";

		final String sql_2 = "delete from HABITATE.town where town_id=?";
		final String sql_3 = "delete from HABITATE.province_town where town_id=?";

		
		return (Town)transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {
        		List <Town> lst = jdbcTemplate.query(sql_1, new Object[] {townId}, new RowMapper<Town>(){
        			public Town mapRow(ResultSet rs, int rowNum) throws SQLException {
        				final Town town = new Town(
        						rs.getString("country_id"), 
        						rs.getString("province_id"), 
        						rs.getString("town_id"));
        				town.town_name = rs.getString("town_name");
        				town.population = rs.getLong("population");
        				log.debug("Town for deletion: " + town);
        				return town;
        			}
        		});
        		
                jdbcTemplate.update(sql_2, new Object[] {townId});
                log.debug("Town: " + lst.get(0) + " just deleted!");
                
                jdbcTemplate.update(sql_3, new Object[] {townId});
                log.debug("TownLink: " + lst.get(0)+ " just deleted!");
                
                return lst.get(0);
            }
        });		
	}

	public Town setTown(final Town town) {
		final String sql_2 = "update HABITATE.province_town set province_id=? set last_modify = current_timestamp where town_id=?";
		final String sql_3 = "insert into HABITATE.province_town (province_id, town_id, last_modify) values (?,?,current_timestamp)";
		
		return (Town)transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {
        		
            	if(town.town_id < 1){ //INSERT
        		    SimpleJdbcInsert insertRec = new SimpleJdbcInsert(ds)
        		    	.withTableName("HABITATE.town")
        		    	.usingGeneratedKeyColumns("town_id")
        		    	.usingColumns("town_name", "population");
        		    
        		    Map <String, Object>prms = new HashMap<String, Object>(2);
        		    prms.put("town_name", town.town_name);
        		    prms.put("population", town.population);
        		    Number n  = insertRec.executeAndReturnKey(prms);
        		    town.town_id = n.intValue();
        			log.info(town + " - INSERTED");
        		}else{ //UPDATE
        			String sql_1 = "update HABITATE.town set town_name = ?, population = ? where town_id=?";
        	        jdbcTemplate.update(sql_1, new Object[] { town.town_name, town.population, town.town_id});	        
        			log.info(town + " - UPDATED");
        		}
        		
        		//am check if Link exists
        		int currentlyAttachedToProvanceId = -1;
        		List<Integer> tmpList = jdbcTemplate.queryForList("select province_id from HABITATE.province_town where town_id=?", new Object[]{town.town_id}, Integer.class);
        		if (!tmpList.isEmpty()){
        			currentlyAttachedToProvanceId = tmpList.get(0);
        		}
        		
        		//am if not - INSERT else MODIFY
        		if (currentlyAttachedToProvanceId < 1){
        			jdbcTemplate.update(sql_3, new Object[] {town.provance_id, town.town_id});			
        			log.info("ProvanceLink: " +  town + " - INSERTED");
        		}else if(currentlyAttachedToProvanceId != town.provance_id){
        			jdbcTemplate.update(sql_2, new Object[] {town.provance_id, town.town_id});			 
        			log.info("ProvanceLink: " +  town + " - UPDATED");
        		}else{
        			log.info("ProvanceLink: NotUpdate b/c " +  town + " still attached to the same Province");
        		}
        		
        		return town;
            }
        });
	}

	public List<Provance> getProvances(Country c) {
		String sql = "select cp.id, p.*, c.* from HABITATE.country_province cp" 
				+ " left outer join HABITATE.province p on p.province_id = cp.province_id" 
				+ " left outer join HABITATE.country c on c.country_id = cp.country_id" 
				+ " where cp.country_id = ?"
				+ " order by c.country_id, p.province_id";

		List<Provance> rslt = new ArrayList<Provance>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[]{c.country_id});
		log.debug("Provances rows: " + rows.size());
		
		for (@SuppressWarnings("rawtypes") Map row : rows) {
			Provance p = new Provance(Integer.parseInt(String.valueOf(row.get("province_id"))), String.valueOf(row.get("province_name")));
			p.country_id = c.country_id;
			rslt.add(p);
		}
		
		log.info("# of Provinces: " + rslt.size() + " by Country: " + c);
		log.debug("Provinces: " + ArrayUtils.toString(rslt) + " by Country: " + c);
		return rslt;
	}

	public Country setCountry(Country rslt) {
		log.debug("Asked to manage: " + rslt);
		
		if(rslt.country_id < 1){ //INSERT
		    SimpleJdbcInsert insertRec = new SimpleJdbcInsert(ds)
		    	.withTableName("HABITATE.country")
		    	.usingGeneratedKeyColumns("country_id")
		    	.usingColumns("country_name");
		    
		    Map <String, Object>prms = new HashMap<String, Object>(1);
		    prms.put("country_name", rslt.country_name);
		    Number n = insertRec.executeAndReturnKey(prms);
		    rslt.country_id = n.intValue();
			log.info(rslt + " - INSERTED");
		}else{ //UPDATE (never tested)
			final String sql = "update HABITATE.country set country_name = ? where country_id = ?";
	        jdbcTemplate.update(sql, new Object[] { rslt.country_name, rslt.country_id});
			log.info(rslt + " - UPDATED");
		}
		
		return rslt;
	}
	
	public Provance setProvance(final Provance rslt) {
		log.debug("Asked to link Province to Country: " + rslt);

		return (Provance)transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {
        		//Province Insert/Update
        		if(rslt.provance_id < 1){ //INSERT PROVANCE
        		    SimpleJdbcInsert insertRec = new SimpleJdbcInsert(ds)
        	    		.withTableName("HABITATE.province")
        	    		.usingGeneratedKeyColumns("province_id")
        	    		.usingColumns("province_name");
        	    
        		    Map <String, Object>prms = new HashMap<String, Object>(1);
        		    prms.put("province_name", rslt.provance);
        		    Number n = insertRec.executeAndReturnKey(prms);
        		    rslt.provance_id = n.intValue();
        		    log.info("Province: " + rslt + " - INSERTED");
        		}else{ //UPDATE (never tested)
        			final String sql = "update HABITATE.province set province_name = ? where province_id = ?";
        	        jdbcTemplate.update(sql, new Object[] { rslt.provance_id});
        			log.info("Province: " + rslt + " - UPDATED");
        		}
        		
        		//Linking (Insert/update)
        		int currentlyAttachedToCountryId = -1;
        		List<Integer> tmpList = jdbcTemplate.queryForList("select country_id from HABITATE.country_province where province_id=?", new Object[]{rslt.provance_id}, Integer.class);
        		if (!tmpList.isEmpty()){
        			currentlyAttachedToCountryId = tmpList.get(0);
        		}
        		
        		log.debug("Province: " + rslt + ", currentlyAttachedToCountryId: " + currentlyAttachedToCountryId);
        		
        		//am if not - INSERT else MODIFY
        		if (rslt.country_id != 0){
        			if (currentlyAttachedToCountryId < 1){
        				jdbcTemplate.update("insert into HABITATE.country_province (country_id, province_id, last_modify) values(?,?, current_timestamp)", new Object[] {rslt.country_id, rslt.provance_id});
        				log.info("CountryProvinceLink: " + rslt + " - INSERTED");
        			}else if(currentlyAttachedToCountryId != rslt.country_id){
        				jdbcTemplate.update("update HABITATE.country_province set country_id = ?, last_modify = current_timestamp where province_id = ?", new Object[] {rslt.country_id, rslt.provance_id});			 
        				log.info("ProvanceLink: " +  rslt + " - UPDATED");
        			}else{
        				log.info("CountryProvanceLink: NotUpdate b/c " +  rslt + " Country-Provance are still the same");
        			}
        		}else{
        			log.info("CountryProvanceLink: NotSet b/c " +  rslt + " Country is NOT set");
        		}
        		
                return rslt;
            }
        });		
	}		
	
	public List<Country> deleteCountries(String askingIds[]) {
		log.debug("Asking to Delete Countries: " +  ArrayUtils.toString(askingIds));
		List <String> listOfIds = Arrays.asList(askingIds);		
		List <Country> rslt = new ArrayList<Country>(askingIds.length);

		final String sql_1 = "select c.country_id, c.country_name as country_name, count(p.province_id) as provances_cnt" 
				+ " from HABITATE.country c"
				+ " left outer join HABITATE.country_province cp on cp.country_id = c.country_id"
				+ " left outer join HABITATE.province p on p.province_id = cp.province_id"
				+ " where c.country_id in (:ids)"
				+ " group by c.country_id"; 

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
	//return JSON? As of now - array of triplets: CountryId_ProvanceId_ProvanceName ("_" is a bad choice for divider!)
	public List<Provance> deleteProvinces(String askingCountryProvIds[]) {
		log.debug("Asking to Delete/Unlink Countries/Provinces: " +  ArrayUtils.toString(askingCountryProvIds));
		List <String> listOfTriplets = Arrays.asList(askingCountryProvIds);
		final List<Provance> rslt = new ArrayList<Provance>(listOfTriplets.size());

		final String sql_1 = "delete from HABITATE.province "
				+ " where not exists (select * from HABITATE.province_town where province_id =?)"
				+ " and province_id =?";

		final String sql_2 = "delete from HABITATE.country_province where country_id = ? and province_id = ?";

		//am loop through all pairs and delete Provinces w. no Towns
		for (final String s: listOfTriplets){
			transactionTemplate.execute(new TransactionCallback<Object>() {
	            public Object doInTransaction(TransactionStatus status) {
	    			String ar [] = StringUtils.split(s, DIV);
	    			
	    			Provance p = new Provance(Integer.valueOf(ar[1]), ar[2]);
	    			p.country_id = Integer.valueOf(ar[0]);
	    			
	    			int rsltDelete = jdbcTemplate.update(sql_1, new Object[] {p.provance_id, p.provance_id});
	    			log.debug("DELETE Province return: " + rsltDelete + " for: " + ArrayUtils.toString(ar));
	    			if (rsltDelete > 0){
	    				log.debug("Unlinking..." + ArrayUtils.toString(ar));
	    				int rsltUnlink = jdbcTemplate.update(sql_2, new Object[] {p.country_id, p.provance_id});	    				
	    				log.debug("UNLINK Country return: " + rsltUnlink + " for: " + ArrayUtils.toString(ar));
	    				rslt.add(p);
	    			}else{
	    				log.info("NOT UNLINKED: " + ArrayUtils.toString(ar));
	    			}
	            	
	                return null;
	            }
	        });		
		}

		return rslt;
	}
	
	public String setProvinceToCountry(final String curCountryId, final String newCountryId, final String provinceId) {
		log.debug("Asking to reassign Country_Province: " + curCountryId + DIV + provinceId + " to: " + newCountryId + DIV + provinceId);

		return (String)transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {
        		int currentlyAttachedToCountryId = -1;

        		List<Integer> tmpList = jdbcTemplate.queryForList("select id from HABITATE.country_province where country_id = ? and province_id = ?", new Object[]{curCountryId, provinceId}, Integer.class);
        		if (!tmpList.isEmpty()){
        			currentlyAttachedToCountryId = tmpList.get(0);
        		}
        		
        		log.debug("currentlyAttachedToCountryId: " + currentlyAttachedToCountryId);
        		if (currentlyAttachedToCountryId < 1){ //INSERT (should not happend here)
        			jdbcTemplate.update("insert into HABITATE.country_province (country_id, province_id, last_modify) values (?,?,current_timestamp)", new Object[]{newCountryId, provinceId});
        			log.debug("CountryProvinceLink: " + newCountryId + DIV + provinceId + " INSERTED");
        		}else{ //UPDATE
        			jdbcTemplate.update("update HABITATE.country_province set country_id = ?, province_id=?, last_modify = current_timestamp where id=?", new Object[]{newCountryId, provinceId, currentlyAttachedToCountryId});
        			log.debug("CountryProvinceLink: " + newCountryId + DIV + provinceId + " UPDATED, oldCountry: " + curCountryId);
        		}
        		
        		log.debug("New Country_Province: "  + newCountryId + DIV + provinceId);
                return provinceId;
            }
        });
	}	
}