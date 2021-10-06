package com.tsecho.bots.api;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class Keyboard {

    InlineKeyboardMarkup ikmGetServiceInfo;

    InlineKeyboardMarkup ikmGetService;

    ReplyKeyboardMarkup rkmGetPhoneAndLocation;

    InlineKeyboardMarkup ikmGetYesOrNo;


    //Для четырех основных кнопок
    List<List<InlineKeyboardButton>> items = new ArrayList<>();

    List<List<InlineKeyboardButton>> item = new ArrayList<>();
    //Для кнопок Да и Нет
    List<List<InlineKeyboardButton>> itemsYesOrNo = new ArrayList<>();
    //Для кнопки Перейти
    List<List<InlineKeyboardButton>> itemsUrl = new ArrayList<>();

    public Keyboard() {
        ikmGetServiceInfo = createIkmGetServiceInfo() ;
        rkmGetPhoneAndLocation = createReplyKeyboard();
        ikmGetYesOrNo = createIkmGetServiceYesOrNo();
        ikmGetService = createIkmGetService();
    }

    //Два метода для четырех основных кнопок
    private InlineKeyboardMarkup createIkmGetServiceInfo(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeys = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        items.add(getIKMButton("status","Статус услуги"));
        items.add(getIKMButton("credit","Обещанный платеж"));
        items.add(getIKMButton("new","Заказать услугу"));
        items.add(getIKMButton("support","Открыть чат ТП"));
        inlineKeys.addAll(items);
        inlineKeyboardMarkup.setKeyboard(inlineKeys);
        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> getIKMButton(String key, String txt){
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        if(key == "new" & txt == "Заказать услугу"){
            btn.setUrl("https://plasmatelecom.ru/");
        }
        if(key == "support" & txt == "Открыть чат ТП"){
            btn.setUrl("https://telegram.me/terralinkbot");
        }
        btn.setText(txt);
        btn.setCallbackData(key);
        row.add(btn);
        return row;
    }

    private InlineKeyboardMarkup createIkmGetService(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeys = new ArrayList<>();
        item.add(getIKMButto("credit","Обещанный платеж"));
        item.add(getIKMButto("new","Заказать услугу"));
        item.add(getIKMButto("support","Открыть чат ТП"));
        inlineKeys.addAll(item);
        inlineKeyboardMarkup.setKeyboard(inlineKeys);
        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> getIKMButto(String key, String txt){
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        if(key == "new" & txt == "Заказать услугу"){
            btn.setUrl("https://plasmatelecom.ru/");
        }
        if(key == "support" & txt == "Открыть чат ТП"){
            btn.setUrl("https://telegram.me/terralinkbot");
        }
        btn.setText(txt);
        btn.setCallbackData(key);
        row.add(btn);
        return row;
    }


    //Два метода для кнопок Да и Нет
    private InlineKeyboardMarkup createIkmGetServiceYesOrNo(){
        InlineKeyboardMarkup inlineKeyboardMarkupYesOrNo = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeysYesOrNo = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        itemsYesOrNo.add(getIKMButtonYesOrNo("yes","Да"));
        itemsYesOrNo.add(getIKMButtonYesOrNo("no","Нет"));
        inlineKeysYesOrNo.addAll(itemsYesOrNo);
        inlineKeyboardMarkupYesOrNo.setKeyboard(inlineKeysYesOrNo);
        return inlineKeyboardMarkupYesOrNo;
    }

    private List<InlineKeyboardButton> getIKMButtonYesOrNo(String key, String txt){
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(txt);
        btn.setCallbackData(key);
        row.add(btn);
        return row;
    }

    //Для кнопки Отправить мой номер
    private ReplyKeyboardMarkup createReplyKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        // new list
        List<KeyboardRow> keyboard = new ArrayList<>();

        // first keyboard line
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton getPhoneButton = new KeyboardButton();
        getPhoneButton.setText("Отправить мой номер");
        getPhoneButton.setRequestContact(true);
        getPhoneButton.setRequestLocation(true);
        keyboardFirstRow.add(getPhoneButton);

        // add array to list
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;

    }
}
