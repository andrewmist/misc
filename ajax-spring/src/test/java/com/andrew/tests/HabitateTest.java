package com.andrew.tests;

import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;

import com.andrew.jdbc.CountryProvanceTownInterface;
import com.pojo.Country;
import com.pojo.Provance;
import com.pojo.Town;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:habitateTestContext.xml"})
public class HabitateTest {
	private static Logger log = Logger.getLogger("HabitateTest");

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private CountryProvanceTownInterface dao;
	
	private static boolean isDBset;

	@Test
	public void runTest(){
		log.debug("fakeTEST......");
	}

	@After
	public void freeResource(){
		log.debug("FREE RESOURCES!");
	}
	
	@Test
	public void runAllCountriesTest(){
		log.debug("All Countries: " + ArrayUtils.toString(dao.getAllCountries().toArray()));
	}

	@Test
	public void runAllProvancesTest(){
		log.debug("All Provinces: " + ArrayUtils.toString(dao.getAllProvances().toArray()));
	}

	@Test
	public void runGetProvincesByCountryTest(){
		Country c = new Country(1, "USA");
		log.debug("Provinces by: " + c + " Country : " + ArrayUtils.toString(dao.getProvances(c).toArray()));
	}

	
	@Test
	public void runTownsTest(){
		log.debug("All Towns by Country=1 and Provance=1: " + ArrayUtils.toString(dao.getTowns(1, 1).toArray()));
		
		Town t = new Town("2", "1", "-1"); //New Russian Town for Province:1
		t.population = 234;
		t.town_name = "New Russ Town, Province 1";		
		t = dao.setTown(t);
		log.info("INSERTED TOWN: " + t);
		t = dao.deleteTown(""+t.town_id);
		log.info("DELETED TOWN: " + t);
	}

	@Test
	public void runInsertCountryTest(){
		Country c = new Country(-1, "New Country");
		dao.setCountry(c);
		log.info("Country Inserted: " + c); 
		c.country_name = "Better Counry";
		dao.setCountry(c);
		log.info("Country Updated: " + c);
	}

	@Test
	public void runInsertProvinceTest(){
		Provance p = new Provance(-1, "New State");
		p.country_id = 1;
		dao.setProvance(p);
		log.info("Sate Inserted: " + p); 
		p.provance = "New is now Better state";
		dao.setProvance(p);
		log.info("State Updated: " + p);
	}

	/*
	@Test
	public void runAddProvanceTest(){
		Provance p = new Provance(-1, "New Russ-Oblast");
		p.country_id = 2;
		log.debug("Saving Russian Provance: " + p);
		log.debug("Provance Saved as: " + dao.setProvance(p));
	}
*/
	@Before
	public void setDatabase(){
		String[] files ={
				"classpath:testdb1.HABITATE.schema.1.sql"
				,"classpath:testdb1.HABITATE.schema.2.sql"
		};

		if (!isDBset){
			isDBset = !isDBset;
			for (String  pth: files){
				log.info("Executing: " + pth);
				executeResource(pth);
			}
			log.debug("DB populated!");			
		}else{
			log.debug("DB Scripts executed already, skipping...");
		}
	}
	
	private void executeResource(String resFile){
		log.debug("Loading script: " + resFile);
		DataSource ds = (DataSource)context.getBean("dataSourceHabitate");
		Resource res = context.getResource(resFile);
		DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(res), ds);
		log.debug("Script: " + resFile + " Loaded OK!");
	}
}