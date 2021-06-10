package com.tsecho.bots.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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

    ReplyKeyboardMarkup rkmGetPhoneAndLocation;

    public Keyboard() {
        ikmGetServiceInfo = createIkmGetServiceInfo() ;
    }

    private InlineKeyboardMarkup createIkmGetServiceInfo(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeys = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(getIKMButton("Статус услуги", "getStatus"));
        inlineKeys.add(buttons);
        inlineKeyboardMarkup.setKeyboard(inlineKeys);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton getIKMButton(String btnText, String cbData){
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(btnText);
        btn.setCallbackData(cbData);
        return btn;
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

        // second keyboard line
//        KeyboardRow keyboardSecondRow = new KeyboardRow();
//        KeyboardButton getLocationButton = new KeyboardButton();
//        getLocationButton.setText("Отправить геолокацию");
//        getLocationButton.setRequestLocation(true);
//        keyboardSecondRow.add(getLocationButton);

        // add array to list
        keyboard.add(keyboardFirstRow);
   //     keyboard.add(keyboardSecondRow);
        // add list to our keyboard
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

//    private KeyboardRow getKeyboardRow(String buttonText, boolean contact, boolean location){
//
//    }

}
