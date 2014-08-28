package com.andrew.tests;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import com.andrew.jdbc.CountryProvanceTownInterface;
import com.pojo.Country;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationTestContext.xml"})
public class MyServiceTest2 implements ApplicationContextAware {
	private static Logger log = Logger.getLogger("MyServiceTest2");
	private ApplicationContext context;
	private static DataSource dds;
	private static boolean isDBset = false;
	
	public void setApplicationContext(ApplicationContext ctx)throws BeansException {
		context = ctx;
	}

	@Before
	public void setTestDatabase(){
		String script = "classpath:demo.HABITATE.sql";
		log.debug("Using script: " + script);

		if (!isDBset){
			dds = (DataSource)context.getBean("dataSource");
			Resource res = context.getResource(script);
			//am works, but deprecated
			//JdbcTemplate tmp = new JdbcTemplate(dds);
			//JdbcTestUtils.executeSqlScript(tmp, res, false);
			DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(res), dds);
			isDBset = !isDBset;
			log.debug("DB populated! " + dds);			
		}else{
			log.debug("Script " + script + " executed already, skipping...");
		}
	}
	
	@After
	public void freeResource(){
		log.debug("FREE RESOURCES!");
	}

	@Test
	public void runTest_4(){
		CountryProvanceTownInterface dao = (CountryProvanceTownInterface) context.getBean("countryProvanceTownDAO"); 
		log.debug("Countries: " + ArrayUtils.toString(dao.getAllCountries().toArray()));
		log.debug("....................................");
	}

	@Test
	public void runTest_5(){
		CountryProvanceTownInterface dao = (CountryProvanceTownInterface) context.getBean("countryProvanceTownDAO"); 
		log.debug("Provances: " + ArrayUtils.toString(dao.getAllProvances().toArray()));
		log.debug("....................................");
	}

	//am to see Countries using local template
	@Test
	public void runTest_2(){
		String sql = "select * from HABITATE.country";

		try{
			JdbcTemplate tmp = new JdbcTemplate(dds);
			List<Country> rslt = new ArrayList<Country>();
			List<Map<String, Object>> rows2 = tmp.queryForList(sql);
			for (@SuppressWarnings("rawtypes") Map row : rows2) {
				int id = Integer.parseInt(String.valueOf(row.get("country_id")));
				String name = String.valueOf(row.get("name"));
			    rslt.add(new Country(id, name));
			}
			log.debug("Plain Countries: " + ArrayUtils.toString(rslt.toArray()));
		}catch(Exception e){
			e.printStackTrace();
		}
		log.debug("TESTO ENDO..");
	}

	
	//am to see if INFORMATION_SCHEMA is accessible.
	@Test
	public void runTest_3(){
		String sql = "select * from INFORMATION_SCHEMA.SYSTEM_USERS";
		try{
			JdbcTemplate tmp = new JdbcTemplate(dds);

			List<Map<String, Object>> rows = tmp.queryForList(sql);
			log.debug("Users rows: " + rows.size());
			
			for (@SuppressWarnings("rawtypes")Map row : rows) {
				log.debug("ROW VALUES: " + ArrayUtils.toString(row.values().toArray()) 
						+ " Keys: " + ArrayUtils.toString(row.keySet().toArray())
						+ " Entries: " + ArrayUtils.toString(row.entrySet().toArray())
						);
				log.debug("UserName: " + String.valueOf(row.get("USER")) + ", isAdmin: " + String.valueOf(row.get("ADMIN")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}