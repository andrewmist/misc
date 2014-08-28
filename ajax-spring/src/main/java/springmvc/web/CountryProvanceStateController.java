package springmvc.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.andrew.jdbc.CountryProvanceTownInterface;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.pojo.Country;
import com.pojo.Provance;
import com.pojo.Town;

//am this Controller responds to misc ajax-calls
// https://sites.google.com/site/gson/gson-user-guide#TOC-Collections-Examples

@Controller
public class CountryProvanceStateController {
	private static Logger log = Logger.getLogger("CountryProvanceStateController");

	private static CountryProvanceTownInterface jdbcDAO;
	static {
		log.debug("Loading Spring Context...");
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		log.debug("applicationContext LOADED");
		jdbcDAO = (CountryProvanceTownInterface) context.getBean("countryProvanceTownDAO");
		context.close();		
	}

	@RequestMapping("/index")
	public ModelAndView getCountriesJson() {
		log.debug("index.html - called!...............");
		List<Country> countries = jdbcDAO.getAllCountries();
		Map<String, String> map = getJsonArray(countries); // am it gotta be astandard routine!
		log.debug("RETURNING COUNTRIES LOOKUP");		
		// Type listOfTestObject = new TypeToken<List<Country>>(){}.getType();
		// return new ModelAndView("index", "countries", new
		// Gson().toJson(countries, listOfTestObject));
		return new ModelAndView("index", "countries", new Gson().toJson(map));
	}
	
	@SuppressWarnings("rawtypes")
	private Map<String, String> getJsonArray(List data) {
		Map<String, String> rslt = new HashMap<String, String>();
		if (!ArrayUtils.isEmpty(data.toArray())) {
			for (Object o : data) {
				if (o instanceof Provance) {
					Provance p = (Provance) o;
					rslt.put("" + p.provance_id, p.provance);
				} else if (o instanceof Country) {
					Country c = (Country) o;
					rslt.put("" + c.country_id, c.country_name);
				} else {
					log.warn("NOT SUPPORTED!!!");
				}

			}
		} else {
			log.warn("No JSON Array: Collection - is Empty!");
		}

		return rslt;
	}

	@RequestMapping(value = "/provances", method = RequestMethod.GET)
	public @ResponseBody String getProvances(@RequestParam String countryId) {
		log.debug("provances.html - called!..............." + countryId);
		Country c = new Country(Integer.parseInt(countryId), "NO-MATTER");
		List<Provance> provances = jdbcDAO.getProvances(c);
		Map<String, String> map = getJsonArray(provances);
		log.debug("RETURNING PROVANCES: " + map.size());
		return new Gson().toJson(map);
	}

	@RequestMapping(value = "/towns", method = RequestMethod.GET)
	public @ResponseBody String getTowns(@RequestParam String countryId, @RequestParam String provanceId) {
		log.debug("towns.html - called!..............." + countryId + ":"+ provanceId);
		List<Town> towns = jdbcDAO.getTowns(Integer.parseInt(countryId),Integer.parseInt(provanceId));
		Gson gson = new Gson();
		JsonElement elm = gson.toJsonTree(towns, new TypeToken<List<Town>>() {}.getType());
		JsonArray jsonArray = elm.getAsJsonArray();
		log.debug("RETURNING towns: " + jsonArray.size());
		// @Response.setContentType("application/json");
		// response.setContentType("application/json");
		// response.getWriter().print(jsonArray);
		// return.getWriter.write(jsonArray.toString());
		return jsonArray.toString();
	}

	@RequestMapping(value = "/deleteTown", method = RequestMethod.GET)
	public @ResponseBody String deleteTown(@RequestParam String countryId,@RequestParam String provanceId, @RequestParam String townId) {
		log.debug("deleteTown.html - called, C:" + countryId + ",P: "+ provanceId + ", T:" + townId);
		Town t = jdbcDAO.deleteTown(townId);
		log.debug("Town: " + t + " deleted from DB");
		return new Gson().toJson(t);
		// JsonElement elm = gson.toJsonTree(towns, new TypeToken<List<Town>>()
		// {}.getType());
		// JsonArray jsonArray = elm.getAsJsonArray();
		// log.debug("RETURNING towns: " + jsonArray.size());
		// return jsonArray.toString();
	}

	@RequestMapping(value = "/setTown", method = RequestMethod.POST)
	public @ResponseBody String setTown(@RequestParam String countryId, @RequestParam String provanceId, @RequestParam String townId, String tName, String tPopulation) {
		log.debug("setTown.html - called, C: " + countryId + ", P: " + provanceId + ",T: " + townId + ": " + tName + ", PPL: " + tPopulation);
		String tId = StringUtils.isEmpty(townId) ? null : townId;
		Town t = new Town(countryId, provanceId, tId);
		t.population = Long.parseLong(tPopulation);
		t.town_name = tName.trim();
		t = jdbcDAO.setTown(t);
		log.debug("Town: " + t + " just SET in DB");
		return new Gson().toJson(t);
	}
}