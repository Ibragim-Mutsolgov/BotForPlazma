package com.tsecho.bots.model.bill;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Immutable
@Data
public class ClientService {
    @Id
    Long id;
    String name;
    String number;
    double balance;
    String descr;
    int blocked;
    String login;
    String pass;
    String mobile;
    String phone;
}
