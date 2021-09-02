package com.tsecho.bots.model.common;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "tmmessages")
@Data
public class Messag {
    @Id
    @Column(name="userid")
    Long id;
    String message;

    public Messag(Long id, String text){
        this.id = id;
        this.message = text;
    }

    public Messag() {

    }
}
