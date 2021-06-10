package com.tsecho.bots.api;

import com.tsecho.bots.model.bill.ClientService;
import com.tsecho.bots.model.common.User;
import com.tsecho.bots.repository.bill.ClientServiceRepository;
import com.tsecho.bots.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.tsecho.bots.api.Tools.getSalutations;

@Service
@Getter
@Setter
public class PlasmaTelegramBot extends TelegramWebhookBot {
    String token;
    String name;
    String webhook;
    HashMap<Long, User> users = new HashMap<Long, User>();

    @Autowired
    Keyboard kb;

    @Autowired
    UserService userService;

    @Autowired
    ClientServiceRepository clientServiceService;

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        //
        SendMessage replyMsg = new SendMessage();
        // Если сообщение не пустое
        if (update.getMessage() != null) {
            // Необходимо идентифицировать пользователя и поэтому ищем пользователя по ID
            User usr = getUser(update);
            if (usr == null) { // не нашли, значит он впервые и нам нужно запросить у него номер
                if (update.getMessage().getContact() == null) {
                    replyMsg.setReplyMarkup(kb.getRkmGetPhoneAndLocation());
                    replyMsg.setText("Для идентификации необходим Ваш номер телефона, нажмите соответствующую кнопку ниже.");
                    return replyMsg;
                } else {
                    usr = addUser(update);
                }
            }
            // У нас есть номер пользователя, необходимо получить услуги
            List<ClientService> asList = clientServiceService.findAllUserByMobileOrPhone(usr.getPhone(), usr.getPhone());
            // Получили услуги, отдаем их
            if (asList == null || asList.isEmpty()) {
                replyMsg.setText("Ваш номер телефона не привязан к услуге. ");
            } else {
                asList.forEach((cs) -> {
                    String str = "Договор: " + cs.getNumber() + '\n';
                    switch (cs.getBlocked()) { //Текущее состояние блокировки:
                        case 0: // 0-уч. запись активна,
                            str = str + "Услуга активна" + '\n';
                            break;
                        case 1, 4: // 1-заблокирована по балансу, 4-по балансу(активная блокировка),
                            str = str + "Услуга заблокирована, необходимо внести оплату" + '\n';
                            break;
                        case 2:  // 2-пользователем,
                            str = str + "Услуга заблокирована пользователем" + '\n';
                            break;
                        default: // 3-администратором, 10-уч. запись отключена
                            str = str + "Услуга отключена по нестандартной причине, Вам необходимо связаться с ТП по номеру: +78732225505" + '\n';
                    }
                    ;
                    replyMsg.setText(str);
                });
            }
            String replyText = getSalutations() + update.getMessage().getFrom().getUserName() + "! Your language code - " + update.getMessage().getFrom().getLanguageCode() + " Your phone - ";
            replyMsg.setChatId(update.getMessage().getChatId().toString());
            replyMsg.setText(replyText);
            replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
        }
        else {
            if (update.getCallbackQuery() != null){
                User usr = getUser(update);
                if (usr == null) { // не нашли, значит он впервые и нам нужно запросить у него номер
                    if (update.getMessage().getContact() == null) {
                        replyMsg.setReplyMarkup(kb.getRkmGetPhoneAndLocation());
                        replyMsg.setText("Для идентификации необходим Ваш номер телефона, нажмите соответствующую кнопку ниже.");
                        return replyMsg;
                    } else {

                        usr = addUser(update);
                    }
                }
            }
        }
        return replyMsg;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    private User getUser(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        User usr = users.get(userId);
        if (usr != null) { // Не нашли, значит ищем в базе
            usr = userService.getUser(userId);
        }
        return usr;
    }

    private User addUser(Update update) {
        // Контакты получены, значит добавляем в базу и возвращаем состояние
        User usr = new User();
        usr.setId(update.getMessage().getContact().getUserId());
        usr.setUsername(update.getMessage().getFrom().getUserName());
        usr.setFirstname(update.getMessage().getContact().getFirstName());
        usr.setLastname(update.getMessage().getContact().getLastName());
        usr.setPhone(update.getMessage().getContact().getPhoneNumber());
        usr.setUuid(UUID.randomUUID());
        users.put(update.getMessage().getContact().getUserId(), usr);
        userService.addUser(usr);
        return usr;
    }
}
