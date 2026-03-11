package com.hacom.orderprocessing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebfluxConfig {

    @Value("${apiPort}")
    private int apiPort;

    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> serverPortCustomizer() {
        return factory -> factory.setPort(apiPort);
    }
}
