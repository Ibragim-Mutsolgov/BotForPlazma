package com.tsecho.bots.service;

import com.tsecho.bots.model.common.Message;
import com.tsecho.bots.repository.common.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void addMessage(Message message) {
        this.messageRepository.save(message);
    }
}
