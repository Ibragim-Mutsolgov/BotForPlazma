package com.tsecho.bots.model.common;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;



@Entity
@Table(name="tmuser")
@Getter
@Setter
public class Customer {

    @Id
    @Column(name = "\"id\"")
    Long id;
    @Column(name = "\"username\"")
    String username;
    @Column(name = "\"firstname\"")
    String firstname;
    @Column(name = "\"lastname\"")
    String lastname;
    @Column(name = "\"phone\"")
    String phone;
    @Column(name = "\"uuid\"")
    UUID uuid;
    @Column(name = "\"chatid\"")
    Long chatid;
    @Transient
    boolean contact = false;
    @Transient
    String queryId;
    @Transient
    String pollId;
    @Transient
    String languageCode;
    @Transient
    boolean isBot = false;
    @Transient
    boolean isNew = false;

    public Customer(){

    }




    public String getChatid() {
        return chatid.toString();
    }
}
