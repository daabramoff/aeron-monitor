package io.aeron.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AeronMonitoringApplication {

    public static void main(final String[] args) {
//        System.err.close();
//        System.setErr(System.out);
        SpringApplication.run(AeronMonitoringApplication.class, args);
    }
}
