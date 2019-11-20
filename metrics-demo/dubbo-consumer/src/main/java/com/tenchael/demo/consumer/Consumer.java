package com.tenchael.demo.consumer;

import com.tenchael.demo.api.DemoService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Consumer {

    public static void main(String[] args) throws IOException, InterruptedException {
        ReferenceConfig<DemoService> reference = new ReferenceConfig<>();
        reference.setApplication(new ApplicationConfig("demo-consumer"));
        reference.setRegistry(new RegistryConfig("nacos://127.0.0.1:8848"));
        reference.setInterface(DemoService.class);
        reference.setVersion("1.0.0");
        reference.setFilter("dubboMetrics");
        DemoService service = reference.get();
        String message = service.sayHello("dubbo");
        System.out.println(message);

        Random random = new Random();
        while (true) {
            service.echo("welcome");
            TimeUnit.MILLISECONDS.sleep(10 + random.nextInt(100));
        }
    }

}
