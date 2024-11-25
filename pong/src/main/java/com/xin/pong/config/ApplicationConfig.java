package com.xin.pong.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories("com.xin.pong.repositories")
class ApplicationConfig extends AbstractReactiveMongoConfiguration {


  @Override
  protected String getDatabaseName() {
    return "pong";
  }
}