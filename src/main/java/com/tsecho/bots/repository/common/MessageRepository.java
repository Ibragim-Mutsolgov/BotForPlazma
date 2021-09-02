package com.tsecho.bots.repository.common;

import com.tsecho.bots.model.common.Messag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessageRepository extends JpaRepository<Messag,Long> {
}
