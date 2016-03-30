package com.svs.web.app.b2bClientPortal;

import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ClientPortalController {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Resource(name = "b2bClientPortal")
	private Properties pom;
	
	@Autowired
	private HttpServletRequest req;

	
	@ModelAttribute("MV")
	public void setMV() {
		if (req != null){
			req.setAttribute("MV", pom == null ? new Properties().put("artifactId", "ThisApp") : pom);
			log.info("Artifact: {}-{}", (pom == null ? "N/A" : pom.getProperty("artifactId")), (pom == null ? "N/A" : pom.getProperty("version","N/A")));
		}
	}
	
	@ResponseBody 
	@RequestMapping(value="/test")
	public String testMe(){
		String rslt = "HERE!";
		String s = pom == null ? "N/A" : pom.getProperty("artifactId") + ":" + pom.getProperty("version");
		rslt = String.format("%s, Artifact: %s", rslt, s);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> {} <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", rslt);
		return rslt;
	}
	
	@RequestMapping(value="/testt")
	public ModelAndView testTiles(){
		String msg = "This is a tiles based View from Parent Controller!";
		String tmpl = "b2bClientPortal.tiles.base";		               
		ModelAndView rslt = new ModelAndView(tmpl);
		rslt.addObject("msg", msg);		
		rslt.addObject("MV", pom);		
		log.info("Inserting msg: {} to template: {}, version: {}", msg, tmpl, pom.getProperty("version"));
		return rslt;
	}		
}