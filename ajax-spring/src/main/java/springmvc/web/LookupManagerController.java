package springmvc.web;

import java.lang.reflect.Type;
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
import com.google.gson.reflect.TypeToken;
import com.pojo.Country;
import com.pojo.Provance;

//am manages States and Countries
@Controller
public class LookupManagerController {
	private static Logger log = Logger.getLogger("LookupManagerController");

	private static CountryProvanceTownInterface jdbcDAO;
	static {
		log.debug("Loading Spring Context...");
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		log.debug("applicationContext LOADED");
		jdbcDAO = (CountryProvanceTownInterface) context.getBean("countryProvanceTownDAO");
		context.close();		
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getCountriesJson() {
		log.debug("lookupManager.html - called!");
		String gson = getCountries();
		return new ModelAndView("lookupManager", "countries", gson);
	}

	/** @deprecated **/
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

	@RequestMapping(value = "/insertCountry", method = RequestMethod.GET)
	public @ResponseBody String insertCountry(@RequestParam String countryName) {
		log.debug("insertCountry.html - called!..............." + countryName);
		Country c = new Country(-1, countryName);
		log.debug("settingCountry: " + c) ;
		jdbcDAO.setCountry(c);
		log.debug("Country: " + c + " just inserted to DB");
		return new Gson().toJson(c);
	}

	@RequestMapping(value = "/deleteCountries", method = RequestMethod.GET)
	public @ResponseBody String deleteCountries(@RequestParam String askingToDelete) {
		log.debug("deleteCountries.html: " + askingToDelete);
		String ar [] = StringUtils.split(askingToDelete,",");
		log.debug("ARRAY OD STRINGS: " + ArrayUtils.toString(ar));
		List <Country> rslt = jdbcDAO.deleteCountries(ar);
		log.debug("Countries asked to delete: " + askingToDelete + ", really deleted: " + ArrayUtils.toString(rslt));
		Type listOfCountries = new TypeToken<List<Country>>(){}.getType();

		log.debug("RETURNING: " + ArrayUtils.toString(rslt));
		return new Gson().toJson(rslt, listOfCountries);
	}

	@RequestMapping(value = "/getCountries", method = RequestMethod.GET)
	public @ResponseBody String getCountries() {
		log.debug("getCountries.html - called!");
		
		List<Country> rslt = jdbcDAO.getAllCountries();
		Type listOfCountries = new TypeToken<List<Country>>(){}.getType();

		log.debug("RETURNING: " + ArrayUtils.toString(rslt));
		return new Gson().toJson(rslt, listOfCountries);
	}

	@RequestMapping(value = "/getProvances", method = RequestMethod.GET)
	public @ResponseBody String getProvances() {
		log.debug("getProvances.html - called!");
		
		List<Provance> rslt = jdbcDAO.getAllProvances();
		Type listOfProvances = new TypeToken<List<Provance>>(){}.getType();

		log.debug("RETURNING: " + ArrayUtils.toString(rslt));
		return new Gson().toJson(rslt, listOfProvances);
	}
	
	@RequestMapping(value = "/insertProvance", method = RequestMethod.GET)
	public @ResponseBody String insertProvance(@RequestParam String provanceName, @RequestParam String countryId) {
		log.debug("insertProvance.html, Provance: "+ provanceName + ", Country: "  + countryId);
		Provance p = new Provance(-1, provanceName);
		p.country_id = Integer.valueOf(countryId);
		jdbcDAO.setProvance(p);
		log.debug("Province: " + p + " just inserted to DB");
		return new Gson().toJson(p);
	}
	
	//am as of now askingToDelete is []: of CountryId_ProvanceId_ProvanceName
	@RequestMapping(value = "/deleteProvinces", method = RequestMethod.GET)
	public @ResponseBody String deleteProvinces(@RequestParam String askingToDelete) {
		log.debug("deleteProvinces.html: " + askingToDelete);
		String triplets [] = StringUtils.split(askingToDelete,",");
		List <Provance> rslt = jdbcDAO.deleteProvinces(triplets);
		log.debug("Provinces asked to delete: " + ArrayUtils.toString(triplets) + ", really deleted: " + ArrayUtils.toString(rslt));
		Type listOfProvances = new TypeToken<List<Provance>>(){}.getType();
		return new Gson().toJson(rslt, listOfProvances);
	}

	@RequestMapping(value = "/setProvinceToCountry", method = RequestMethod.GET)
	public @ResponseBody String setProvinceToCountry(@RequestParam String curCountryId, @RequestParam String newCountryId, @RequestParam String provinceId) {
		log.info("setProvinceToCountry.html - called: " + curCountryId + ":" + newCountryId + ":" + provinceId);
		String rslt = jdbcDAO.setProvinceToCountry(curCountryId, newCountryId, provinceId);
		log.debug("Province moved to another country!");
		return rslt;
	}
}