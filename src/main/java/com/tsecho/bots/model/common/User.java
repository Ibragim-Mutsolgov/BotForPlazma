package com.tsecho.bots.model.common;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;


@Entity
@Table(name="tmuser")
@Data
@Proxy(lazy=false)
public class User {

    @Id
    Long id;
    String username;
    String firstname;
    String lastname;
    String phone;
    UUID uuid;

}
