package com.tsecho.bots.repository.common;

import com.tsecho.bots.model.common.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("dsCommon")
public interface UserRepository extends JpaRepository<User,Long> {
    List<User> getAllById(Long id);
}
