package com.voshodnerd;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class InitBot {
    private static String PROXY_HOST = "153.149.168.40" /* proxy host */;
    private static Integer PROXY_PORT = 3128 /* proxy port */;

  public static void  run() {
      try {

          ApiContextInitializer.init();

          // Create the TelegramBotsApi object to register your bots
          TelegramBotsApi botsApi = new TelegramBotsApi();

          // Set up Http proxy
          DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

          HttpHost httpHost = new HttpHost(PROXY_HOST, PROXY_PORT);

          RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).setAuthenticationEnabled(false).build();
          botOptions.setRequestConfig(requestConfig);
          botOptions.setHttpProxy(httpHost);

          // Register your newly created AbilityBot
          Bot bot = new Bot(botOptions);

          botsApi.registerBot(bot);

      } catch (TelegramApiException e) {
          e.printStackTrace();
      }


  }

}
