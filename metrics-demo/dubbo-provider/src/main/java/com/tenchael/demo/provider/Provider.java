package com.tenchael.demo.provider;

import com.tenchael.demo.api.DemoService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.io.IOException;

public class Provider {

	public static void main(String[] args) throws IOException {
		ServiceConfig<DemoServiceImpl> service = new ServiceConfig<>();
		service.setApplication(new ApplicationConfig("demo-provider"));
		service.setRegistry(new RegistryConfig("nacos://127.0.0.1:8848"));
		service.setInterface(DemoService.class);
		service.setVersion("1.0.0");
		service.setRef(new DemoServiceImpl());
		service.setFilter("dubboMetrics");
		service.export();
		System.in.read();
	}

}
