package com.springapp.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class HelloController implements Controller{
	protected final Log logger = LogFactory.getLog(getClass());
	private SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	@Override
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		String now = df.format(new Date());
	    logger.info("Returning Hello View as: jsp/hello.jsp @: " + now);
	    //return new ModelAndView("jsp/hello.jsp", "now", now);
        //return new ModelAndView("jsp/hello.jsp");
	    return new ModelAndView("hello", "now", now);
	}
}
