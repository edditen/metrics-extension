package com.tenchael.demo.provider;

import com.tenchael.demo.api.DemoService;

import java.util.Random;

public class DemoServiceImpl implements DemoService {

	private final Random random = new Random();


	@Override
	public String sayHello(String msg) {
		return "Hello " + msg;
	}

	@Override
	public String echo(String msg) {
		if (next() > 90) {
			throw new RuntimeException("oops!");
		}
		return msg;
	}

	private int next() {
		return random.nextInt(100);
	}
}
