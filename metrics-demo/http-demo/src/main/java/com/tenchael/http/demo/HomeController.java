package com.tenchael.http.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("homeController")
@RequestMapping("/")
public class HomeController {

	@RequestMapping("/")
	public String index(ModelMap model) {
		model.addAttribute("message", "Hello web!");
		return "index";
	}
}
