package com.tsecho.bots.model.common;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tmmessages")
@Proxy(lazy=false)
public class Message {
    @Id
    @Column(name="userid")
    Long id;
    String message;
}
