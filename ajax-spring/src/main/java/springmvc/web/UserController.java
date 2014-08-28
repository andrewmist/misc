package springmvc.web;

import org.springframework.stereotype.Controller;
import java.util.Date;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


//am this Controller responds to /hello and /time "gets"
@Controller
public class UserController {

  @RequestMapping("/hello")
  public ModelAndView helloWorld() {
	System.out.println("hello.html - called!...............");  
    return new ModelAndView("hello", "message", "Spring MVC Demo");
  }

  @RequestMapping(value = "/time", method = RequestMethod.GET)
  public @ResponseBody String getTime(@RequestParam String name) {
	System.out.println("time.html - called!...............");
    String result = "'" + name + "' asked for Time, here it is: " + new Date().toString();
    return result;
  }
}