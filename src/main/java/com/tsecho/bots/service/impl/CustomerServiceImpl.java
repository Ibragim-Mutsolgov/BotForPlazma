package com.tsecho.bots.service.impl;

import com.tsecho.bots.model.common.Customer;
import com.tsecho.bots.repository.common.CustomerRepository;
import com.tsecho.bots.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    // Создаем виртуальную таблицу пользователей
    HashMap<Long, Customer> customers = new HashMap<Long, Customer>();

    @PostConstruct
    private void init() {
        customers = (HashMap<Long, Customer>) customerRepository.findAll().stream().collect(Collectors.toMap(Customer::getId, Function.identity()));
    }

    @Override
    public Customer add(Customer customer) {
        return customerRepository.save(customer);
    }

    public void setCustomers(Customer customer) {
        this.customers.put(customer.getId(), customer);
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.getById(id);
    }

    private Customer getUser(Message msg) {
        Customer c = customers.get(msg.getFrom().getId());
        if (c != null) {
            return c;
        } else {
            c = customerRepository.getById(msg.getFrom().getId());
        }
        if (c != null) {
            customers.put(c.getId(),c);
            return c;
        } else {
            c = setValues(msg.getFrom());
            c.setChatid(msg.getChatId());
            if (msg.getContact() != null) {
                c.setPhone(msg.getContact().getPhoneNumber());
                customerRepository.save(c);
                customers.put(c.getId(),c);
            }
        }
        return c;
    }
    /*
     *  Ищем пользователя в Хэшмап, затем в БД и если не находим, то создаем нового и записываем в хэшмап и БД
     * */
    private Customer getUser(User user, String queryId, String pollId) {
        Customer c = customers.get(user.getId());
        if (c == null) {
            c = customerRepository.getById(user.getId());
        }
        if (c == null) {
            c = setValues(user);
            c.setQueryId(queryId);
            c.setPollId(pollId);
        }
        return c;
    }


    public Customer requestUser(Update update) {
        Customer customer = null;
        if (update.hasMessage()) {                  // Если это сообщение
            customer = getUser(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            customer = getUser(update.getCallbackQuery().getFrom(), "", "");
        } else if (update.hasChannelPost()) {
            customer = getUser(update.getChannelPost());
        } else if (update.hasEditedChannelPost()) {
            customer = getUser(update.getEditedChannelPost());
        } else if (update.hasEditedMessage()) {
            customer = getUser(update.getEditedMessage());
        } else if (update.hasInlineQuery()) {
            customer = getUser(update.getInlineQuery().getFrom(), update.getInlineQuery().getId(), "");
        } else if (update.hasChosenInlineQuery()) {
            customer = getUser(update.getChosenInlineQuery().getFrom(), update.getChosenInlineQuery().getQuery(), "");
        } else if (update.hasPollAnswer()) {
            customer = getUser(update.getPollAnswer().getUser(), "", update.getPollAnswer().getPollId());
        } else if (update.hasPreCheckoutQuery()) {
            customer = getUser(update.getPreCheckoutQuery().getFrom(), update.getPreCheckoutQuery().getId(), "");
        } else if (update.hasShippingQuery()) {
            customer = getUser(update.getShippingQuery().getFrom(), update.getShippingQuery().getId(), "");
        }

        return customer;
    }

    @Override
    public List<Customer> findAllUsers(Long userId) {
        return customerRepository.findAllById(userId);
    }

    public HashMap<Long, Customer> getCustomers() {
        return customers;
    }

    private Customer setValues(User user) {
        Customer c = new Customer();
        c.setId(user.getId());
        c.setUsername(user.getUserName());
        c.setFirstname(user.getFirstName());
        c.setLastname(user.getLastName());
        c.setLanguageCode(user.getLanguageCode());
        c.setBot(user.getIsBot());
        c.setUuid(UUID.randomUUID());
        return c;
    }

}
