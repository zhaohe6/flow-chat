package com.robod.flowchat.config;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.eclipse.jetty.util.thread.ThreadPool;

import java.util.Arrays;

@Configuration
public class JettyConfig {
//    @Bean
//    public JettyServletWebServerFactory jettyServletWebServerFactory() {
//        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
//
//        factory.addServerCustomizers(server -> {
//            // 获取或创建线程池
//            QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
//            if (threadPool == null) {
//                threadPool = new QueuedThreadPool();
//                server.addBean(threadPool);
//            }
//
//            // 配置线程池参数
//            threadPool.setMinThreads(4);
//            threadPool.setMaxThreads(16);
//            threadPool.setIdleTimeout(60000);
//
//            // 连接器配置
//            Arrays.stream(server.getConnectors())
//                    .filter(connector -> connector instanceof ServerConnector)
//                    .map(connector -> (ServerConnector) connector)
//                    .forEach(connector -> {
//                        connector.setAcceptQueueSize(65535);
//                        connector.setIdleTimeout(300000);
//                    });
//        });
//
//        return factory;
//    }
}
