package com.tsecho.bots.repository.common;

import com.tsecho.bots.model.common.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;


@Repository
@Qualifier("dsCommon")
public interface MessageRepository extends JpaRepository<Message,Long> {
}
