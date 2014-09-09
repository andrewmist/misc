package com.springapp.web;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.springapp.service.ProductManager;

public class InventoryController implements Controller{
	protected final Log logger = LogFactory.getLog(getClass());
	private ProductManager productManager;
	private SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	@Override
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		String now = df.format(new java.util.Date());
	    logger.info("returning hello view with " + now);

	    Map<String, Object> myModel = new HashMap<String, Object>();
	    myModel.put("now", now);
	    myModel.put("products", this.productManager.getProducts());

	    return new ModelAndView("hello", "model", myModel);
	}

	public void setProductManager(ProductManager productManager) {
        this.productManager = productManager;
    }

}
