package com.tsecho.bots.api;

import com.tsecho.bots.model.bill.ClientService;
import com.tsecho.bots.model.common.Customer;
import com.tsecho.bots.model.common.Messag;
import com.tsecho.bots.repository.bill.ClientServiceRepository;
import com.tsecho.bots.service.impl.CustomerServiceImpl;
import com.tsecho.bots.service.impl.MessageServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.tsecho.bots.api.Tools.createImage;

@Service
@Getter
@Setter
@ConfigurationProperties("telegram.bot")
public class PlasmaTelegramBot extends TelegramWebhookBot {

    String token;

    String name;

    String webhook;

    String phone;

    xmlRequest xml = new xmlRequest();


    @Autowired
    Keyboard kb;

    @Autowired
    CustomerServiceImpl customerServiceImpl;

    @Autowired
    ClientServiceRepository clientServiceService;

    @Autowired
    MessageServiceImpl messageServiceImpl;

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    SendPhoto sp = new SendPhoto();

    Integer lastUpdateId = 0;

    Keyboard k = new Keyboard();

    @SneakyThrows
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        SendMessage replyMsg = new SendMessage();

        replyMsg.enableWebPagePreview();

        Customer customer = customerServiceImpl.requestUser(update);

        // Смотрим обрабатывали мы такое сообщение или нет и пропускаем если было
        if (update.getUpdateId() <= lastUpdateId) return replyMsg;

        this.lastUpdateId = update.getUpdateId();

        // Ищем юзверя
        if (customer.getPhone() == null && update.hasMessage()) {
            if (update.getMessage().hasContact()) {
                customer.setPhone(update.getMessage().getContact().getPhoneNumber());
                customerServiceImpl.setCustomers(customer);
                customerServiceImpl.add(customer);
            } else {
                replyMsg.setReplyMarkup(kb.getRkmGetPhoneAndLocation());
                replyMsg.setText("Для идентификации необходим Ваш номер телефона, нажмите соответствующую кнопку ниже.");
                replyMsg.setChatId(customer.getChatid());
                return replyMsg;
            }
        }


        replyMsg.setChatId(customer.getChatid());
        setPhone(customer.getPhone().replace("+", ""));
        //Обработка Текста
        if (update.hasMessage()) {
            replyMsg.setText("Выберете услугу.");
            replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
            replyMsg.setChatId(update.getMessage().getFrom().getId().toString());
            return replyMsg;
        }

        //Обработка кнопки Обещанный платеж
        if (update.getCallbackQuery().getData().equals("credit")) {
            xml.setPhone(phone);
            xml.xmlLoginAndPass();
            replyMsg.setText(xml.getUser());
            if (xml.getS().length > 1) {//Для случая, когда количества договоров более одного
                replyMsg.setReplyMarkup(setButtons(xml.getS()));
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                ;
                return replyMsg;
            } else {
                return replyMsg;
            }
        } else if (update.getCallbackQuery().getData().startsWith("agr-")) {
            xml.setBool(true);
            xml.setNumberAgreements(update.getCallbackQuery().getData().replace("agr-",""));
            xml.getUser();
            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            return replyMsg;
        }




        if(update.getCallbackQuery().getData().equals("backPromise")){
            replyMsg.setText("Выберите услугу.");
            replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            return replyMsg;
        }

        if(update.getCallbackQuery().getData().equals("new")){
            replyMsg.setText("Перейдите на сайт для заказа услуги.");
            replyMsg.setReplyMarkup(kb.getIkmGetUrl());
            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            return replyMsg;
        }

        if(update.getCallbackQuery().getData().equals("back")){
            replyMsg.setText("Выберите услугу.");
            replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            return replyMsg;
        }

        if(update.getCallbackQuery().getData().equals("support")){
            replyMsg.setText("Нажата кнопка открыть чат тп.");
            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            return replyMsg;
        }

        // У нас есть номер пользователя, необходимо получить услуги



        if (update.hasMessage() && update.getMessage().getText() != null) {
            messageServiceImpl.addMessage(new Messag(update.getMessage().getFrom()
                    .getId(), update.getMessage().getText()));
        }

        // Здесь мы возвращаем меню

        List<ClientService> asList = clientServiceService.findAllUserByMobileOrPhone(phone, phone);

        // Получили услуги, отдаем их

        //Условие для кнопки статус услуги

        if (update.getCallbackQuery().getData().equals("status")) {
            if (asList == null || asList.isEmpty()) {
                replyMsg.setText("Ваш номер телефона не привязан к услуге. Выберите одну" +
                        " из ниже перечисленных.");
                replyMsg.setChatId(customer.getChatid());
            } else {
                try {
                    sp.setPhoto(createImage(asList));
                    sp.setChatId(customer.getChatid());
                    sp.setReplyMarkup(kb.getIkmGetServiceInfo());
                    execute(sp);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TelegramApiException tae) {
                    replyMsg.setText("Не удалось загрузить.");
                }
            }
            replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());

            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());

            return replyMsg;
        }

        replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());

        return replyMsg;
    }

    //кнопки для договоров
    public InlineKeyboardMarkup setButtons(String[] string){
        PlasmaKeyboard inlineKeyboardMarkup = new PlasmaKeyboard();
        inlineKeyboardMarkup.setType(2); // 2 - меню для выбора договора
        List<List<InlineKeyboardButton>> lst2 = new ArrayList<>();
        for(int i=0; i<string.length; i++) {
            List<InlineKeyboardButton> lst = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(string[i]);
            button.setCallbackData("agr-"+string[i]);
            lst.add(button);
            lst2.add(lst);
        }
        List<List<InlineKeyboardButton>> lst3 = new ArrayList<>();
        List<InlineKeyboardButton> lst1 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Назад");
        button2.setCallbackData("backPromise");
        lst1.add(button2);
        lst2.add(lst1);
        inlineKeyboardMarkup.setKeyboard(lst2);
        return inlineKeyboardMarkup;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getBotPath() {
        return null;
    }

}
