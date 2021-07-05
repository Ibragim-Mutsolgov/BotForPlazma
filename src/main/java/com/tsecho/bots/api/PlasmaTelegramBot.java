package com.tsecho.bots.api;

import com.tsecho.bots.model.bill.ClientService;
import com.tsecho.bots.model.common.Customer;
import com.tsecho.bots.model.common.Message;
import com.tsecho.bots.repository.bill.ClientServiceRepository;
import com.tsecho.bots.service.impl.CustomerServiceImpl;
import com.tsecho.bots.service.impl.MessageServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
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

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        SendMessage replyMsg = new SendMessage();
       replyMsg.enableWebPagePreview();

        // Смотрим обрабатывали мы такое сообщение или нет и пропускаем если было
        if (update.getUpdateId() <= lastUpdateId)
            return replyMsg;
        this.lastUpdateId = update.getUpdateId();
        // Ищем юзверя
        Customer customer = customerServiceImpl.requestUser(update);

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
        // У нас есть номер пользователя, необходимо получить услуги
        String phone = customer.getPhone().replace("+", "");
        if (update.hasMessage() && update.getMessage().getText() != null) {
            messageServiceImpl.addMessage(new Message(update.getMessage().getFrom().getId(), update.getMessage().getText()));
        }

        // Здесь мы возвращаем меню

        List<ClientService> asList = clientServiceService.findAllUserByMobileOrPhone(phone, phone);
        // Получили услуги, отдаем их
        if (asList == null || asList.isEmpty()) {
            replyMsg.setText("Ваш номер телефона не привязан к услуге. ");
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
                replyMsg.setText("Не удалось загрузить");
            }
        }
        replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
        return replyMsg;
    }

    @Override
    public String getBotPath() {

        return null;
    }


}
