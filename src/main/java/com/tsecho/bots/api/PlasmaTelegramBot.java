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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Getter
@Setter
@ConfigurationProperties("telegram.bot")
public class PlasmaTelegramBot extends TelegramWebhookBot {

    String token;

    String name;

    String webhook;

    String phone;



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

    Integer lastUpdateId = 0;

    Keyboard k = new Keyboard();

    @SneakyThrows
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        SendMessage replyMsg = new SendMessage();

        replyMsg.enableWebPagePreview();

        Customer customer = customerServiceImpl.requestUser(update);

        // Смотрим: обрабатывали мы такое сообщение или нет и пропускаем если было
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
            try{DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(replyMsg.getChatId());
                deleteMessage.setMessageId(update.getMessage().getMessageId());
                execute(deleteMessage);}
            catch(TelegramApiRequestException e){
                replyMsg.setText("Выберите действие.");
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
            replyMsg.setText("Выберите действие.");
            replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
            replyMsg.setChatId(update.getMessage().getFrom().getId().toString());
            return replyMsg;
        }

        //Обработка кнопки Обещанный платеж
        if (update.getCallbackQuery().getData().equals("credit")) {
            try{DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(replyMsg.getChatId());
                deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                execute(deleteMessage);}
            catch(TelegramApiRequestException e){
                replyMsg.setText("Выберите действие.");
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
            xmlRequest xml = new xmlRequest();
            xml.setPhone(phone);
            xml.xmlLoginAndPass();
            if(!xml.getNumbersAgreements().isEmpty()){
                if (xml.getNumbersAgreements().size() > 0) {//Для случая, когда количества договоров более одного
                    replyMsg.setText(xml.getUsersMessage());
                    replyMsg.setReplyMarkup(setButtons(xml.getNumbersAgreements()));
                    replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                    xml.getNumbersAgreements().clear();
                    return replyMsg;
                }
            }else {
                replyMsg.setText(xml.getUsersMessage());
                if(xml.getUsersMessage() == "Для данного договора услуга \"Обещанный платеж\" не предусмотрена. Обратитесь в техническую поддержку." |
                    xml.getUsersMessage() == "Ваш договор не оформлен на физическое лицо. Обратитесь в техническую поддержку." |
                    xml.getUsersMessage() == "Ваш договор оформлен на юридическое лицо. Обратитесь в техническую поддержку." |
                    xml.getUsersMessage() == "Телеграмм-бот временно неисправен. Обратитесь в техническую поддержку."){
                    replyMsg.setReplyMarkup(tpButtons());
                }else{
                    replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                }
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
        }else if (update.getCallbackQuery().getData().startsWith("agr-")) {
            try{DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(replyMsg.getChatId());
                deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                execute(deleteMessage);}
            catch(TelegramApiRequestException e){
                replyMsg.setText("Выберите действие.");
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
            xmlRequest xml = new xmlRequest();
            xml.setPhone(phone);
            xml.setAgreement(update.getCallbackQuery().getData().replace("agr-",""));
            replyMsg.setText(xml.clickButtonsNumberAgreements());
            if(xml.clickButtonsNumberAgreements() == "Для данного договора услуга \"Обещанный платеж\" не предусмотрена. Обратитесь в техническую поддержку." |
                xml.clickButtonsNumberAgreements() == "Ваш договор не оформлен на физическое лицо. Обратитесь в техническую поддержку." |
                xml.clickButtonsNumberAgreements() == "Ваш договор оформлен на юридическое лицо. Обратитесь в техническую поддержку." |
                xml.clickButtonsNumberAgreements() == "Телеграмм-бот временно неисправен. Обратитесь в техническую поддержку."){
                replyMsg.setReplyMarkup(tpButtons());
            }else{
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
            }
            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            xml.setAgreement(null);
            return replyMsg;
        }

        if(update.getCallbackQuery().getData().equals("menu")){
            try{DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(replyMsg.getChatId());
                deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                execute(deleteMessage);}
            catch(TelegramApiRequestException e){
                replyMsg.setText("Выберите действие.");
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
            replyMsg.setText("Выберите действие.");
            replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            return replyMsg;
        }

        if(update.getCallbackQuery().getData().equals("backPromise")){
            try{DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(replyMsg.getChatId());
                deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                execute(deleteMessage);}
            catch(TelegramApiRequestException e){
                replyMsg.setText("Выберите действие.");
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
            replyMsg.setText("Выберите действие.");
            replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
            replyMsg.setChatId((update.getCallbackQuery().getFrom().getId().toString()));
            return replyMsg;
        }

        if(update.getCallbackQuery().getData().equals("new")){
            try{DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(replyMsg.getChatId());
                deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                execute(deleteMessage);}
            catch(TelegramApiRequestException e){
                replyMsg.setText("Выберите действие.");
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            return replyMsg;
        }

        if(update.getCallbackQuery().getData().equals("support")){
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

        if(update.getCallbackQuery().getData().startsWith("tarif-")){
            try{DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(replyMsg.getChatId());
                deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                execute(deleteMessage);}
            catch(TelegramApiRequestException e){
                replyMsg.setText("Выберите действие.");
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
            Tarif tarif = new Tarif();
            tarif.setPhone(phone);
            tarif.setAgreement(update.getCallbackQuery().getData().replace("tarif-",""));
            tarif.setNumber(update.getCallbackQuery().getMessage().getReplyMarkup().getKeyboard().get(1).get(0).getText());
            String s = new String();
            s = tarif.getClickTarif();
            if(s == "Телеграмм-бот временно не исправен. Обратитесь в техническую поддержку."){
                replyMsg.setReplyMarkup(tpButtons());
            }else if(s == "Для этого договора услуги не определены."){
                replyMsg.setText(s);
                replyMsg.setReplyMarkup(kb.getIkmGetService());
            }else if(s == "Ваш номер телефона не привязан к услуге. Нажмите на кнопку \"Заказать услугу\"."){
                replyMsg.setText(s);
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
            }else{
                replyMsg.setText(s);
                replyMsg.setReplyMarkup(kb.getIkmGetService());
            }
            tarif.setAgreement(null);
            tarif.setNumber(null);
            replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            return replyMsg;
        }

        // Получили услуги, отдаем их

        //Условие для кнопки статус услуги
        if (update.getCallbackQuery().getData().equals("status")) {
            try{DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(replyMsg.getChatId());
            deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            execute(deleteMessage);}
            catch(TelegramApiRequestException e){
                replyMsg.setText("Выберите действие.");
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
            if (asList == null || asList.isEmpty()) {
                replyMsg.setText("Ваш номер телефона не привязан к услуге. Нажмите на кнопку \"Заказать услугу\".");
                replyMsg.setChatId(customer.getChatid());
                replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
            } else {
                Tarif tarif = new Tarif();
                tarif.getTarif();
                tarif.setPhone(phone);
                String s = new String();
                s = tarif.getTarif();
                if(s == "Телеграмм-бот временно не исправен. Обратитесь в техническую поддержку." |
                        s == "Невозможно определить Ваш тарифный план. Обратитесь в техническую поддержку."){
                    replyMsg.setText(s);
                    replyMsg.setReplyMarkup(tpButtons());
                }else if(tarif.getNumbersAgreements().size() > 0){
                    replyMsg.setText(s);
                    replyMsg.setReplyMarkup(tarifButtons(tarif.getNumbersAgreements()));
                    tarif.getNumbersAgreements().clear();
                }else if(s == "Ваш номер телефона не привязан к услуге. Нажмите на кнопку \"Заказать услугу\"."){
                    replyMsg.setText(s);
                    replyMsg.setReplyMarkup(kb.getIkmGetService());
                }else if(tarif.getNumbersAgreements().isEmpty()){
                    replyMsg.setText(s);
                    replyMsg.setReplyMarkup(kb.getIkmGetService());
                }else{
                    replyMsg.setText(s);
                    replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
                }
                replyMsg.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                return replyMsg;
            }
        }

        replyMsg.setReplyMarkup(kb.getIkmGetServiceInfo());
        return replyMsg;
    }

    //кнопки для договоров
    public InlineKeyboardMarkup setButtons(Map<String, String> string){
        String[] key = new String[string.size()];
        String[] values = new String[string.size()];
        int k=0;
        for (Map.Entry<String, String> item : string.entrySet()) {
            key[k] = item.getKey();
            values[k] = item.getValue();
            k++;
        }
        PlasmaKeyboard inlineKeyboardMarkup = new PlasmaKeyboard();
        inlineKeyboardMarkup.setType(2); // 2 - меню для выбора договора
        List<List<InlineKeyboardButton>> lst2 = new ArrayList<>();
        for(int i=0; i<string.size(); i++) {
            List<InlineKeyboardButton> lst = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(values[i]);
            button.setCallbackData("agr-"+key[i]);
            lst.add(button);
            lst2.add(lst);
        }
        List<InlineKeyboardButton> lst1 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Назад");
        button2.setCallbackData("backPromise");
        lst1.add(button2);
        lst2.add(lst1);
        inlineKeyboardMarkup.setKeyboard(lst2);
        return inlineKeyboardMarkup;
    }

    //кнопки для договоров тариф
    public InlineKeyboardMarkup tarifButtons(Map<String, String> string){
        String[] key = new String[string.size()];
        String[] values = new String[string.size()];
        int k=0;
        for (Map.Entry<String, String> item : string.entrySet()) {
            key[k] = item.getKey();
            values[k] = item.getValue();
            k++;
        }
        PlasmaKeyboard inlineKeyboardMarkup = new PlasmaKeyboard();
        inlineKeyboardMarkup.setType(2); // 2 - меню для выбора договора
        List<List<InlineKeyboardButton>> lst2 = new ArrayList<>();
        for(int i=0; i<string.size(); i++) {
            List<InlineKeyboardButton> lst = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(values[i]);
            button.setCallbackData("tarif-"+key[i]);
            lst.add(button);
            lst2.add(lst);
        }
        List<InlineKeyboardButton> lst1 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Назад");
        button2.setCallbackData("backPromise");
        lst1.add(button2);
        lst2.add(lst1);
        inlineKeyboardMarkup.setKeyboard(lst2);
        return inlineKeyboardMarkup;
    }

    //Кнопка ТП
    public InlineKeyboardMarkup tpButtons(){
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> lst = new ArrayList<>();

        List<InlineKeyboardButton> lst2 = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Техническая поддержка");
        button.setUrl("https://telegram.me/terralinkbot");
        button.setCallbackData("tp");
        lst2.add(button);
        lst.add(lst2);

        List<InlineKeyboardButton> lst3 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Основное меню");
        button2.setCallbackData("menu");
        lst3.add(button2);
        lst.add(lst3);
        inline.setKeyboard(lst);
        return inline;
    }


    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String getBotPath() {
        return null;
    }
}
                                                                                                                                                                       //Авторы: Цечоев Багаудин
                                                                                                                                                                       //        Муцольгов Ибрагим