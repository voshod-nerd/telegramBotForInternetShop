package com.voshodnerd;

import com.voshodnerd.repository.GoodsRepository;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@SpringBootApplication
public class TelegramApplication {

  @Autowired
  Bot bot;



    public static void main(String[] args) {
        SpringApplication.run(TelegramApplication.class, args);

    }
}
