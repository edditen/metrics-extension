package com.tenchael.http.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("helloController")
@RequestMapping("/hello")
public class HelloController {

	@RequestMapping("/")
	@ResponseBody
	public String hello() {
		return "ok";
	}
}
