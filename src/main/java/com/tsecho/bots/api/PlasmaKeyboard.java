package com.tsecho.bots.api;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Getter
@Setter
public class PlasmaKeyboard extends InlineKeyboardMarkup {

    private int type;

}
