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
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
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
import java.util.Optional;
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
            if (message!=null && message.hasText()) {
                try {
                    handleIncomingMessage(message,update);
                } catch (InvalidObjectException e) {
                    BotLogger.severe(LOGTAG, e);
                }
            } else if (update.hasCallbackQuery()) {
              handleCallBachQuery(update,update.getCallbackQuery());
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    private  void  handleCallBachQuery(Update update,CallbackQuery callBack) {

        CallbackQuery callbackquery = update.getCallbackQuery();
        String[] data = callbackquery.getData().split(":");

        switch (data[0]) {
            case "type": {
                showAllGoodsByType(data[1],callBack);
                break;
            }
            case "good": {
                showAllInformationAboutGood(callBack,data[1]);
                break;}
            default: {
                showMessageError(update);
                break;
            }

        }
    }

    public void showMessageError(Update update) {
        String messageText="Упс чтото пошло не так. Сорян(";
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();

        SendMessage message = new SendMessage()// Create a message object object
                .setChatId(chat_id)
                .setText(messageText);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


    public void   showAllInformationAboutGood(CallbackQuery callback,String idGood) {
        Optional<Goods>  el= rep.findById(Long.parseLong(idGood));
        if (el.isPresent()) {
            Goods good=el.get();
            String messageText="Подробная информация о товаре \n"+good.getName()+" \nЦена "+good.getPrice()+" \nКоличество на скаладе "+good.getCounts()+
                    " \nКраткое описание товара \n"+good.getDescription();
            long chat_id = callback.getMessage().getChatId();
            SendMessage message = new SendMessage()// Create a message object object
                    .setChatId(chat_id)
                    .setText(messageText);
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAllGoodsByType(String type,CallbackQuery callback) {

        SendMessage message = new SendMessage()// Create a message object object
                .setChatId(callback.getMessage().getChatId().toString())
                .setText("Список товаров по типу "+type);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<Goods> listGoodByType= rep.findByType(type);
        for (Goods good:listGoodByType) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton().setText(good.getName()).setCallbackData("good:"+good.getId().toString()));
            // Set the keyboard to the markup
            rowsInline.add(rowInline);
        }
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) { e.printStackTrace();
        }

    }




    private void handleIncomingMessage(Message message,Update update) throws InvalidObjectException {
            switch (message.getText()) {
                case "/start":
                    sendMsg(message, "Это команда старт!");

                    break;
                case "Список всех товаров":
                    setInlineListAllGoods(update);

                    break;
                case "Типы товаров":

                    setInline(update);

                    break;
                case "О магазине":
                    sendMsg(message, "Наш магазин 'Mr. Gadget' привествует вас. У нас широкий выбор электроники");

                     System.out.println(message.getText());
                    break;
                default:

                    showMessageError(update);
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
            rowInline.add(new InlineKeyboardButton().setText(good.getName()).setCallbackData("good:"+good.getId().toString()));
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
                rowInline.add(new InlineKeyboardButton().setText(type).setCallbackData("type:"+type));
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

    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return TOKEN;
    }



    protected Bot(String botUserName ,String botToken,DefaultBotOptions botOptions) {
        // super(botToken, botUsername, botOptions);
        super(botOptions);
        this.BOT_USERNAME=botUserName;
        this.TOKEN=botToken;

    }

}
