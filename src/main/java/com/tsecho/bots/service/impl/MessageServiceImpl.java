package com.tsecho.bots.service.impl;

import com.tsecho.bots.model.common.Messag;
import com.tsecho.bots.repository.common.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl {
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void addMessage(Messag message) {
        this.messageRepository.save(message);
    }
}
