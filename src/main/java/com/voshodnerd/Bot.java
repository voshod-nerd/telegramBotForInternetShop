package com.voshodnerd;

import com.voshodnerd.model.Goods;
import com.voshodnerd.repository.GoodsRepository;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import javax.annotation.PostConstruct;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 *
 * @author User
 */

public class Bot extends TelegramLongPollingBot {
    private static final String LOGTAG = "CHANNELHANDLERS";
    private  final String BOT_USERNAME;
    private  final String TOKEN;

    private final ConcurrentHashMap<Integer, Integer> userState = new ConcurrentHashMap<>();
    private    GoodsRepository rep;


    public void setGoodsRepository(GoodsRepository rep) { this.rep=rep;}

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message message = update.getMessage();
            if (message != null && message.hasText()) {
                try {
                    handleIncomingMessage(message,update);
                } catch (InvalidObjectException e) {
                    BotLogger.severe(LOGTAG, e);
                }
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }


        // We check if the update has a message and the message has text
       /* if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if (update.getMessage().getText().equals("/start")) {


                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText("You send /start");
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Update message text").setCallbackData("update_msg_text"));
                // Set the keyboard to the markup
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                message.setReplyMarkup(markupInline);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {

            }

        } else if (update.hasCallbackQuery()) {}

        // We check if the update has a message and the message has text
       /* if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(update.getMessage().getText());
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        */
    }

    private void handleIncomingMessage(Message message,Update update) throws InvalidObjectException {


            switch (message.getText()) {
                case "/start":
                    sendMsg(message, "Это команда старт!");
                    //System.out.println(message.getText());
                    break;
                case "Список всех товаров":
                    setInlineListAllGoods(update);
                   //  sendMsg(message, "Список всех товаров");
                    //System.out.println(message.getText());
                    break;
                case "Типы товаров":
                    //sendMsg(message, "Типы товаров");
                    setInline(update);
                  //  System.out.println(message.getText());
                    break;
                case "О магазине":
                    sendMsg(message, "Наш магазин Alser привествует вас. У нас широкий выбор электроники");
                    //setInline(update);

                     System.out.println(message.getText());
                    break;
                default:
                    sendMsg(message, "О магазине");
                    //System.out.println(message.getText());
                    break;
            }





    }

    public  void setInlineListAllGoods(Update update) {
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();
        List<Goods> list = rep.findAllGoods();
        SendMessage message = new SendMessage()// Create a message object object
                .setChatId(chat_id)
                .setText("Список товаров нашего магазина");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Goods good:list) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton().setText(good.getName()).setCallbackData(good.getId().toString()));
            // Set the keyboard to the markup
            rowsInline.add(rowInline);
        }
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void setInline(Update update) {
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();
        List<String> list = rep.findTypes();
            SendMessage message = new SendMessage()// Create a message object object
                    .setChatId(chat_id)
                    .setText("Все виды товаров нашего магазина");
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            for (String type:list) {
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText(type).setCallbackData(type));
                // Set the keyboard to the markup
                rowsInline.add(rowInline);
            }
            // Add it to the message
            markupInline.setKeyboard(rowsInline);
            message.setReplyMarkup(markupInline);
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }


    }


    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Список всех товаров");
        keyboardFirstRow.add("Типы товаров");

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add("О магазине");


        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendMessage.setChatId(message.getChatId().toString());
       // sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }








    @Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        return BOT_USERNAME;
       // return "VoshodNerdBot";
    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return TOKEN;
        //return "583709432:AAEW7A1xYf9MKhJ5XBiQjzgTMUbHbUfBKKg";
    }



    protected Bot(String botUserName ,String botToken,DefaultBotOptions botOptions) {
        // super(botToken, botUsername, botOptions);
        super(botOptions);
        this.BOT_USERNAME=botUserName;
        this.TOKEN=botToken;

    }

}
