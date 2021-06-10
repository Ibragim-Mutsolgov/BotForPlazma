package com.tsecho.bots.rest;


import com.tsecho.bots.api.PlasmaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RestController
public class TelegramWebHookController {


    @Autowired
    PlasmaTelegramBot plasmaTelegramBot;

    public TelegramWebHookController(PlasmaTelegramBot plasmaTelegramBot) {
        this.plasmaTelegramBot = plasmaTelegramBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update){
        // в зависимости от типа запроса(ответа)
        // update.getCallbackQuery().
        return plasmaTelegramBot.onWebhookUpdateReceived(update);
    }
}
