package com.tsecho.bots.repository.common;

import com.tsecho.bots.model.common.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    List<Customer> findAllById(Long id);
    Customer getById(Long id);
    Optional<Customer> findById(Long id);
//    @Modifying
//    @Query(value = "insert into tmuser VALUES (:id,:username,:firstname,:lastname,:phone,:uuid,:chatid)", nativeQuery = true)
//    @Transactional
//    void add(@Param("id")Long id, @Param("username")String username,
//             @Param("username")String firstname,
//             @Param("username")String lastname,
//             @Param("username")String phone,
//             @Param("username") UUID uuid,
//             @Param("username")Long chatid);

}
