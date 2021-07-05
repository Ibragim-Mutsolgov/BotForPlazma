package com.tsecho.bots.service;


import com.tsecho.bots.model.common.Customer;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public interface CustomerService {
    Customer add(Customer customer);
    Customer findById(Long id);
    List<Customer> findAllUsers(Long id);
}
