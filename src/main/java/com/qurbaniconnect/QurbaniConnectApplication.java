package com.qurbaniconnect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Slf4j
public class QurbaniConnectApplication {
    public static void main(String[] args) {
        log.info("Starting QurbaniConnect Application...");
        SpringApplication.run(QurbaniConnectApplication.class, args);
        log.info("QurbaniConnect Application main method completed");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("========================================");
        log.info("QurbaniConnect Application is ready!");
        log.info("========================================");
    }
}
