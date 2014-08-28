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
import com.pojo.Provance;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationTestContext.xml"})
public class MyServiceTest {
	private static Logger log = Logger.getLogger("MyServiceTest");

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private CountryProvanceTownInterface dao;
	
	private static boolean isDBset;

	
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
		log.debug("All Provances: " + ArrayUtils.toString(dao.getAllProvances().toArray()));
	}

	@Test
	public void runTownsTest(){
		log.debug("All Towns by Country=1 and Provance=1: " + ArrayUtils.toString(dao.getTowns(1,  1).toArray()));
	}
	
	@Test
	public void runAddProvanceTest(){
		Provance p = new Provance(-1, "New Russ-Oblast");
		p.country_id = 2;
		log.debug("Saving Russian Provance: " + p);
		log.debug("Provance Saved as: " + dao.setProvance(p));
	}

	@Before
	public void setDatabase(){
		String script1 = "classpath:demo.HABITATE.schema.sql";
		String script2 = "classpath:demo.HABITATE.datafill.sql";

		if (!isDBset){
			isDBset = !isDBset;
			executeResource(script1);
			executeResource(script2);
			log.debug("DB populated!");			
		}else{
			log.debug("DB Scripts executed already, skipping...");
		}
	}
	
	private void executeResource(String resFile){
		log.debug("Loading script: " + resFile);
		DataSource ds = (DataSource)context.getBean("dataSource");
		Resource res = context.getResource(resFile);
		DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(res), ds);
		log.debug("Script: " + resFile + " Loaded OK!");
	}
}