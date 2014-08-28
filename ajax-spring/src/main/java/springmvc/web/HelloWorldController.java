package springmvc.web;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class HelloWorldController implements Controller, ServletContextAware{
	@Autowired
	private ServletContext servletContext;
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException{
		String aMessage = "Hello World MVC! (where is my 'Get' method?)";
		System.out.println("........................... HERE .............................");
 
		ModelAndView modelAndView = new ModelAndView("hello_world");
		modelAndView.addObject("message", aMessage);		
		modelAndView.addObject("readme","ModelAndView is created in Controllder as View 'hello_world' and returned back to Spring 'Resolver'");
		
		try{
			String pth = servletContext.getRealPath("/");
			pth += "WEB-INF\\web.xml";
			System.out.println("PATH: " + pth);
			
			StringBuilder sb = new StringBuilder();
		    BufferedReader br = new BufferedReader(new FileReader(pth));

		    String s = null;
		    while ((s = br.readLine()) != null) {
	            sb.append(s);
	            sb.append("<br>");
		    }
		    br.close();
			System.out.println("XML: " + sb.toString());
			modelAndView.addObject("webxml", sb.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return modelAndView;
	}

	public void setServletContext(ServletContext arg0) {
		this.servletContext = arg0;
	}
}