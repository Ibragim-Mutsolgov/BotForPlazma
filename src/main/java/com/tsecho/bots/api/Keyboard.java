package com.tsecho.bots.api;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Component
@Getter
public class Keyboard {

    InlineKeyboardMarkup ikmGetServiceInfo;

    ReplyKeyboardMarkup rkmGetPhoneAndLocation;

    List<List<InlineKeyboardButton>> items = new ArrayList<>();

    public Keyboard() {
        ikmGetServiceInfo = createIkmGetServiceInfo() ;
        rkmGetPhoneAndLocation = createReplyKeyboard();
    }

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
        btn.setText(txt);
        btn.setCallbackData(key);
        row.add(btn);
        return row;
    }

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
